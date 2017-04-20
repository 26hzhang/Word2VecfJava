package com.isaac.word2vecf;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.isaac.word2vecf.utils.Common;
import com.isaac.word2vecf.utils.Pair;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghao on 18/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Word2VecfModel {
	/** For file process usage */
	private final static long ONE_GB = 1024 * 1024 * 1024;
	private final static byte LINE_SEPARATOR = 10;
	private final static byte COLUMN_SEPARATOR = 32;
	private final static ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

	private final int layerSize;
	private final List<String> wordVocab;
	private final double[][] wordVectors;
	private final List<String> contextVocab;
	private final double[][] contextVectors;

	Word2VecfModel(int layerSize, Iterable<String> wordVocab, double[][] wordVectors, Iterable<String> contextVocab, double[][] contextVectors) {
		this.layerSize = layerSize;
		this.wordVocab = ImmutableList.copyOf(wordVocab);
		this.wordVectors = wordVectors;
		this.contextVocab = ImmutableList.copyOf(contextVocab);
		this.contextVectors = contextVectors;
	}

	Word2VecfModel(int layerSize, Iterable<String> wordVocab, double[][] wordVectors) {
		this.layerSize = layerSize;
		this.wordVocab = ImmutableList.copyOf(wordVocab);
		this.wordVectors = wordVectors;
		this.contextVocab = null;
		this.contextVectors = null;
	}

	/** @return Layer size */
	public int getLayerSize() {
		return layerSize;
	}

	/** @return word vocabulary size */
	public int getWordVocabSize() {
		return wordVocab.size();
	}

	/** @return context vocabulary size */
	public int getContextVocabSize() {
		if (contextVocab == null)
			return 0;
		else
			return contextVocab.size();
	}

	/** @return word vocabulary */
	public List<String> getWordVocab() {
		return wordVocab;
	}

	/** @return context vocabulary */
	public List<String> getContextVocab() {
		return contextVocab;
	}

	/** @return word vectors */
	public double[][] getWordVectors() {
		return wordVectors;
	}

	/** @return context vectors */
	public double[][] getContextVectors() {
		return contextVectors;
	}

	public boolean containsContext() {
		if (contextVocab == null)
			return false;
		else
			return true;
	}

	/** @return {@link Word2VecfModel} */
	public static Word2VecfModel fromTextFile(String wordFilePath, String contextFilePath) {
		List<String> wordLines = null;
		List<String> contextLines = null;
		try {
			wordLines = Common.readToList(new File(wordFilePath));
			if (!(contextFilePath == null || contextFilePath.isEmpty()))
				contextLines = Common.readToList(new File(contextFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (contextLines == null)
			return fromTextFile(wordFilePath, wordLines);
		else
			return fromTextFile(wordFilePath, wordLines, contextFilePath, contextLines);
	}

	/** @return {@link Word2VecfModel} */
	public static Word2VecfModel fromBinaryFile(String wordFilePath, String contextFilePath) {
		Pair<List<String>, double[][]> wordPair = null;
		Pair<List<String>, double[][]> contextPair = null;
		try {
			wordPair = fromBinaryFile(wordFilePath);
			contextPair = fromBinaryFile(contextFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (contextPair == null)
			return new Word2VecfModel(wordPair.second[0].length, wordPair.first, wordPair.second);
		else
			return new Word2VecfModel(wordPair.second[0].length, wordPair.first, wordPair.second, contextPair.first, contextPair.second);
	}

	/** Saves the model as text file */
	public void toTextFile(String wordFilePath, String contextFilePath) {

	}

	/** Saves the model as bin file */
	public void toBinaryFile(String wordFilePath, String contextFilePath){
		final Charset cs = StandardCharsets.UTF_8;
		try {
			final OutputStream os = new FileOutputStream(new File(wordFilePath));
			final String header = String.format("%d %d\n", wordVocab.size(), layerSize);
			os.write(header.getBytes(cs));
			final ByteBuffer buffer = ByteBuffer.allocate(4 * layerSize);
			buffer.order(byteOrder);
			for (int i = 0; i < wordVocab.size(); ++i) {
				os.write(String.format("%s ", wordVocab.get(i)).getBytes(cs)); // Write one vocab in byte format, add a space.
				buffer.clear();
				for (int j = 0; j < layerSize; ++j) {
					buffer.putFloat((float) this.wordVectors[i][j]);
				}
				os.write(buffer.array()); // Write all float values of one vector in byte format.
				os.write('\n'); // Add a newline.
			}
			os.flush();
			os.close();
			if (!(contextFilePath == null || contextFilePath.isEmpty())) {
				final OutputStream cos = new FileOutputStream(new File(contextFilePath));
				final String cheader = String.format("%d %d\n", contextVocab.size(), layerSize);
				cos.write(cheader.getBytes(cs));
				final ByteBuffer cbuffer = ByteBuffer.allocate(4 * layerSize);
				cbuffer.order(byteOrder);
				for (int i = 0; i < contextVocab.size(); ++i) {
					cos.write(String.format("%s ", contextVocab.get(i)).getBytes(cs)); // Write one vocab in byte format, add a space.
					cbuffer.clear();
					for (int j = 0; j < layerSize; ++j) {
						cbuffer.putFloat((float) this.contextVectors[i][j]);
					}
					cos.write(cbuffer.array()); // Write all float values of one vector in byte format.
					cos.write('\n'); // Add a newline.
				}
				cos.flush();
				cos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** @return {@link Word2VecfModel} */
	private static Word2VecfModel fromTextFile(String wordFilePath, List<String> wordLines) {
		List<String> wordVocab = Lists.newArrayList();
		int vocabSize = Integer.parseInt(wordLines.get(0).split(" ")[0]);
		int layerSize = Integer.parseInt(wordLines.get(0).split(" ")[1]);
		Preconditions.checkArgument(vocabSize == wordLines.size() - 1, "For file '%s', vocab size is %s, but there are %s word vectors in the file",
				wordFilePath, vocabSize, wordLines.size() - 1);
		double[][] wordVectors = new double[vocabSize][layerSize];
		for (int n = 1; n < wordLines.size(); n++) {
			String[] values = wordLines.get(n).split(" ");
			wordVocab.add(values[0]);
			Preconditions.checkArgument(layerSize == values.length - 1, "For file '%s', on line %s, layer size is %s, but found %s values in the word vector",
					wordFilePath, n, layerSize, values.length - 1); // Sanity check
			for (int d = 1; d < values.length; d++) {
				wordVectors[n - 1][d - 1] = Double.parseDouble(values[d]);
			}
		}
		return new Word2VecfModel(layerSize, wordVocab, wordVectors);
	}

	/** @return {@link Word2VecfModel} */
	private static Word2VecfModel fromTextFile(String wordFilePath, List<String> wordLines, String contextFilePath, List<String> contextLines) {
		List<String> wordVocab = Lists.newArrayList();
		List<String> contextVocab = Lists.newArrayList();
		int wordVocabSize = Integer.parseInt(wordLines.get(0).split(" ")[0]);
		int contextVocabSize = Integer.parseInt(contextLines.get(0).split(" ")[0]);
		int layerSize = Integer.parseInt(wordLines.get(0).split(" ")[1]);
		Preconditions.checkArgument(wordVocabSize == wordLines.size() - 1, "For file '%s', vocab size is %s, but there are %s word vectors in the file",
				wordFilePath, wordVocabSize, wordLines.size() - 1);
		Preconditions.checkArgument(contextVocabSize == contextLines.size() - 1, "For file '%s', vocab size is %s, but there are %s context vectors in the file",
				contextFilePath, contextVocabSize, contextLines.size() - 1);
		double[][] wordVectors = new double[wordVocabSize][layerSize];
		double[][] contextVectors = new double[contextVocabSize][layerSize];
		for (int n = 1; n < wordLines.size(); n++) {
			String[] values = wordLines.get(n).split(" ");
			wordVocab.add(values[0]);
			Preconditions.checkArgument(layerSize == values.length - 1, "For file '%s', on line %s, layer size is %s, but found %s values in the word vector",
					wordFilePath, n, layerSize, values.length - 1); // Sanity check
			for (int d = 1; d < values.length; d++) {
				wordVectors[n - 1][d - 1] = Double.parseDouble(values[d]);
			}
		}
		for (int n = 1; n < contextLines.size(); n++) {
			String[] values = contextLines.get(n).split(" ");
			contextVocab.add(values[0]);
			Preconditions.checkArgument(layerSize == values.length - 1, "For file '%s', on line %s, layer size is %s, but found %s values in the context vector",
					contextFilePath, n, layerSize, values.length - 1); // Sanity check
			for (int d = 1; d < values.length; d++) {
				contextVectors[n - 1][d - 1] = Double.parseDouble(values[d]);
			}
		}
		return new Word2VecfModel(layerSize, wordVocab, wordVectors, contextVocab, contextVectors);
	}

	/** @return {@link Pair}, which format is <List<String>, double[][]> */
	private static Pair<List<String>, double[][]> fromBinaryFile(String filename) throws IOException {
		if (filename == null || filename.isEmpty())
			return null;
		try (final FileInputStream fis = new FileInputStream((new File(filename)));
			final FileChannel channel = fis.getChannel()) {
			final Charset cs = StandardCharsets.UTF_8;
			MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, Math.min(channel.size(), Integer.MAX_VALUE));
			buffer.order(byteOrder);
			int bufferCount = 1;
			// Java's NIO only allows memory-mapping up to 2GB. To work around this problem, we re-map every gigabyte. To calculate offsets correctly, we have to keep
			// track how many gigabytes we've already skipped. That's what this is for.
			List<Byte> list = new ArrayList<>(); // read the first line
			byte c = buffer.get();
			while (c != LINE_SEPARATOR) {
				list.add(c);
				c = buffer.get();
			}
			String firstLine = new String(Bytes.toArray(list), cs);
			int index = firstLine.indexOf(COLUMN_SEPARATOR);
			Preconditions.checkState(index != -1, "Expected a space in the first line of file '%s': '%s'", filename, firstLine);
			final int vocabSize = Integer.parseInt(firstLine.substring(0, index));
			final int layerSize = Integer.parseInt(firstLine.substring(index + 1));
			List<String> vocab = new ArrayList<>(vocabSize);
			double[][] vectors = new double[vocabSize][layerSize];
			final float[] floats = new float[layerSize];
			for (int lineno = 0; lineno < vocabSize; lineno++) {
				// read to vocab
				list.clear();
				c = buffer.get();
				while (c != COLUMN_SEPARATOR) {
					// ignore newlines in front of words (some binary files have newline, some don't)
					if (c != LINE_SEPARATOR) {
						list.add(c);
					}
					c = buffer.get();
				}
				vocab.add(new String(Bytes.toArray(list), cs));
				// read to vectors
				final FloatBuffer floatBuffer = buffer.asFloatBuffer();
				floatBuffer.get(floats);
				for (int i = 0; i < floats.length; ++i) {
					vectors[lineno][i] = floats[i];
				}
				buffer.position(buffer.position() + 4 * layerSize);
				// remap file
				if (buffer.position() > ONE_GB) {
					final int newPosition = (int) (buffer.position() - ONE_GB);
					final long size = Math.min(channel.size() - ONE_GB * bufferCount, Integer.MAX_VALUE);
					buffer = channel.map(FileChannel.MapMode.READ_ONLY, ONE_GB * bufferCount, size);
					buffer.order(byteOrder);
					buffer.position(newPosition);
					bufferCount += 1;
				}
			}
			return Pair.cons(vocab, vectors);
		}
	}

	/** @return {@link Word2VecfTrainerBuilder} for training a model */
	public static Word2VecfTrainerBuilder trainer() {
		return new Word2VecfTrainerBuilder();
	}
}
