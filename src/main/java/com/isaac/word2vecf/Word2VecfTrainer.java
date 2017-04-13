package com.isaac.word2vecf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.isaac.utils.CallableVoid;
import com.isaac.utils.VocabFunctions;
import com.isaac.representation.Vocabulary;
import com.isaac.representation.Vocabulary.VocabWord;
import com.isaac.representation.Word2VecfModel;

public class Word2VecfTrainer {
	/** Sentences longer than this are broken into multiple chunks */
	static final int MAX_SENTENCE_LENGTH = 1_000;
	/** A string longer than this are trunked the first part */
	static final int MAX_STRING = 100;
	/** Boundary for maximum exponent allowed */
	static final int MAX_EXP = 6;
	/** Size of the pre-cached exponent table */
	static final int EXP_TABLE_SIZE = 1_000;
	static final double[] EXP_TABLE = new double[EXP_TABLE_SIZE];
	static {
		for (int i = 0; i < EXP_TABLE_SIZE; i++) {
			// Precompute the exp() table
			EXP_TABLE[i] = Math.exp((i / (double) EXP_TABLE_SIZE * 2 - 1) * MAX_EXP);
			// Precompute f(x) = x / (x + 1)
			EXP_TABLE[i] /= EXP_TABLE[i] + 1;
		}
	}
	/** define word and context vocabularies */
	private final Vocabulary wv;
	private final Vocabulary cv;
	/** define the table size */
	private static final int TABLE_SIZE = (int) 1e8;
	/** configurations of word2vecf training */
	final Word2VecfConfig config;
	final int layer1_size;

	long numTrainedTokens;

	/* The following includes shared state that is updated per worker thread */
	/**
	 * To be precise, this is the number of words in the training data that
	 * exist in the vocabulary which have been processed so far. It includes
	 * words that are discarded from sampling. Note that each word is processed
	 * once per iteration.
	 */
	protected final AtomicInteger actualWordCount;
	/** Learning rate, affects how fast values in the layers get updated */
	volatile double alpha;
	/**
	 * This contains the outer layers of the neural network
	 * First dimension is the vocab size, second is the layer
	 */
	final double[][] syn0;
	final double[][] syn1neg;
	/** Used for negative sampling */
	private final int[] unitable;
	/** train file */
	private final File trainFile;

	/** output file and dumpcv file */
	//private final String outputFile;
	//private final String dumpcvFile;

	public Word2VecfTrainer(Word2VecfConfig config, String trainFilename, String wvocabFilename, String cvocabFilename) {
		this.trainFile = new File(trainFilename);
		this.wv = this.ReadVocab(wvocabFilename);
		this.cv = this.ReadVocab(cvocabFilename);
		this.numTrainedTokens = wv.word_count;
		//this.outputFile = outputFilename;
		//this.dumpcvFile = dumpcvFilename;
		this.actualWordCount = new AtomicInteger();
		this.config = config;
		this.layer1_size = config.layerSize;
		this.alpha = config.initialLearningRate;
		this.unitable = new int[TABLE_SIZE];
		this.syn0 = new double[this.wv.vocab_size][layer1_size];
		this.syn1neg = new double[this.cv.vocab_size][layer1_size];
		this.initializeUnigramTable();
		this.initializeNet();
	}

	private void initializeUnigramTable() {
		double power = 0.75f;
		long normalizer = 0;
		for (VocabWord word : cv.vocab)
			normalizer += Math.pow(word.cn, power);
		int i = 0;
		double d1 = Math.pow(cv.vocab.get(i).cn, power) / normalizer;
		for (int a = 0; a < TABLE_SIZE; a++) {
			unitable[a] = i;
			if (a / (double) TABLE_SIZE > d1) {
				i++;
				d1 = Math.pow(cv.vocab.get(i).cn, power) / normalizer;
			}
			if (i > cv.vocab_size)
				i = cv.vocab_size - 1;
		}
	}

	private void initializeNet() {
		long nextRandom = 1;
		for (int a = 0; a < wv.vocab_size; a++) {
			nextRandom = incrementRandom(nextRandom);
			for (int b = 0; b < layer1_size; b++) {
				nextRandom = incrementRandom(nextRandom);
				syn0[a][b] = (((nextRandom & 0xFFFF) / (double) 65_536) - 0.5f) / layer1_size;
			}
		}
	}

	private static float[][] convertToFloats(double[][] array) {
		float[][] result = new float[array.length][array[0].length];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				result[i][j] = (float) array[i][j];
			}
		}
		return result;
	}

	/** @return Next random value to use */
	private static long incrementRandom(long r) {
		return r * 25_214_903_917L + 11;
	}

	public Word2VecfModel train() throws IOException, InterruptedException {
		final ListeningExecutorService ex = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(config.numThreads,
				config.numThreads, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(config.numThreads),
				new ThreadPoolExecutor.CallerRunsPolicy()));
		List<String> data = this.loadDataFromFile();
		final Iterable<List<String>> batched = Iterables.partition(data, 1024);
		try {
			for (int iter = config.iterations; iter > 0; iter--) {
				List<ListenableFuture<?>> futures = new ArrayList<>(64);
				int i = 0;
				for (final List<String> batch : batched) {
					futures.add(ex.submit(createWorker(i, iter, batch)));
					i++;
				}
				try {
					Futures.allAsList(futures).get();
				} catch (ExecutionException e) {
					throw new IllegalStateException("Error training word2vecf", e.getCause());
				}
			}
			ex.shutdown();
		} finally {
			ex.shutdownNow();
		}
		return new Word2VecfModel(config.layerSize, wv.wordSet(), convertToFloats(syn0),
				cv.wordSet(), convertToFloats(syn1neg));
	}

	/** @return {@link Worker} to process the given sentences */
	Worker createWorker(int randomSeed, int iter, Iterable<String> batch) {
		return new Worker(randomSeed, iter, batch);
	}

	/** Worker thread that updates the word2vecf model */
	private class Worker extends CallableVoid {
		private static final int LEARNING_RATE_UPDATE_FREQUENCY = 10_000;

		long nextRandom;
		final int iter;
		final Iterable<String> batch;
		/**
		 * The number of words observed in the training data for this worker
		 * that exist in the vocabulary. It includes words that are discarded
		 * from sampling.
		 */
		int wordCount;
		/** Value of wordCount the last time alpha was update */
		int lastWordCount;

		//final double[] neu1 = new double[layer1_size];
		final double[] neu1e = new double[layer1_size];

		Worker(int randomSeed, int iter, Iterable<String> batch) {
			this.nextRandom = randomSeed;
			this.iter = iter;
			this.batch = batch;
		}

		@Override
		public void run() throws InterruptedException {
			for (String s : batch) {
				String word = s.split(" ")[0];
				String context = s.split(" ")[1];
				// update alpha
				if (wordCount - lastWordCount > LEARNING_RATE_UPDATE_FREQUENCY)
					updateAlpha(iter);
				VocabFunctions V = new VocabFunctions();
				int wrdi = V.SearchVocab(wv, word);
				int ctxi = V.SearchVocab(cv, context);
				if (wrdi < 0 || ctxi < 0)
					continue;
				wordCount++;
				if (config.sample > 0) {
					VocabWord wvw = wv.vocab.get(wrdi);
					double random = (Math.sqrt(wvw.cn / (config.sample * wv.word_count)) + 1) *
							(config.sample * wv.word_count) / wvw.cn;
					nextRandom = incrementRandom(nextRandom);
					if (random < (nextRandom & 0xFFFF) / (double) 65_536)
						continue;
					VocabWord cvw = cv.vocab.get(ctxi);
					random = (Math.sqrt(cvw.cn / (config.sample * cv.word_count)) + 1) * (config.sample * cv.word_count)
							/ cvw.cn;
					nextRandom = incrementRandom(nextRandom);
					if (random < (nextRandom & 0xFFFF) / (double) 65_536)
						continue;
				}
				// handle negative sampling
				handleNegativeSampling(wrdi, ctxi);
				// Learn weights input -> hidden
				for (int c = 0; c < layer1_size; c++)
					syn0[wrdi][c] += neu1e[c];
			}
			actualWordCount.addAndGet(wordCount - lastWordCount);
		}

		/**
		 * Degrades the learning rate (alpha) steadily towards 0
		 *
		 * @param iter
		 *            only used for debugging
		 */
		private void updateAlpha(int iter) {
			int currentActual = actualWordCount.addAndGet(wordCount - lastWordCount);
			lastWordCount = wordCount;
			// Degrade the learning rate linearly towards 0 but keep a minimum
			alpha = config.initialLearningRate * Math.max(1 - currentActual /
					(double) (config.iterations * numTrainedTokens), 0.0001);
		}

		private void handleNegativeSampling(int wrdi, int ctxi) {
			for (int d = 0; d <= config.negative; d++) {
				int target;
				final int label;
				if (d == 0) {
					target = ctxi;
					label = 1;
				} else {
					nextRandom = incrementRandom(nextRandom);
					target = unitable[(int) (((nextRandom >> 16) % TABLE_SIZE) + TABLE_SIZE) % TABLE_SIZE];
					if (target == 0)
						target = (int) (((nextRandom % (cv.vocab_size - 1)) + cv.vocab_size - 1) % (cv.vocab_size - 1))
								+ 1;
					if (target == ctxi)
						continue;
					label = 0;
				}
				int l2 = target;
				double f = 0;
				for (int c = 0; c < layer1_size; c++)
					f += syn0[wrdi][c] * syn1neg[l2][c];
				final double g;
				if (f > MAX_EXP)
					g = (label - 1) * alpha;
				else if (f < -MAX_EXP)
					g = label * alpha;
				else
					g = (label - EXP_TABLE[(int) ((f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))]) * alpha;
				for (int c = 0; c < layer1_size; c++)
					neu1e[c] = g * syn1neg[l2][c];
				for (int c = 0; c < layer1_size; c++)
					syn1neg[l2][c] = g * syn0[wrdi][c];
			}
		}

	}

	/** load vocabulary file from file */
	private Vocabulary ReadVocab(String vocabFile) {
		File file = new File(vocabFile);
		if (!file.exists()) {
			System.out.println("Vocabulary file does not exist!!!");
			System.exit(-1);
		}
		VocabFunctions V = new VocabFunctions();
		Vocabulary v = V.CreateVocabulary();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while ((line = br.readLine()) != null) {
				String[] words = line.split(" ");
				int a = V.AddWordToVocab(v, words[0]);
				v.vocab.get(a).cn = Integer.parseInt(words[1]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return v;
	}

	/** load training data from file */
	private List<String> loadDataFromFile() {
		List<String> trainData = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile)));
			String line;
			while ((line = br.readLine()) != null)
				trainData.add(line);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.unmodifiableList(trainData);
	}

}
