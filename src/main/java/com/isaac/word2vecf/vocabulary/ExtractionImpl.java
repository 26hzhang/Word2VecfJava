package com.isaac.word2vecf.vocabulary;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Pre-process, create input data, which is in the form of (word context) pairs, as well as word and context vocabularies
 * from raw corpus
 *
 * Created by zhanghao on 20/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class ExtractionImpl {
	/** Longest length of a word allowed */
	private static final int MAX_STRING = 100;
	/** drop frequency and minimal count */
	private final int MIN_FREQUENCY;
	private final int MIN_COUNT;
	/** count whole train words */
	private static long trainedWordsCount = 0;

	/** the set of words which appearance frequency larger than MIN_FREQUENCY */
	private final ImmutableSet<String> wordSet;

	/** train file */
	private final String trainFile;
	/** train file directory */
	private final String directory;

	/** functions to deal with word and context vocabularies */
	private static final VocabFunctions V = new VocabFunctions();
	/** create word and context vocabularies */
	private final Vocabulary wv = V.createVocabulary();
	private final Vocabulary cv = V.createVocabulary();

	/** constructor */
	public ExtractionImpl(String trainFile, Integer minFrequency, Integer minCount) {
		Preconditions.checkArgument(trainFile != null && !trainFile.isEmpty(), "train file path must be assigned");
		this.trainFile = trainFile;
		this.directory = trainFile.substring(0, trainFile.lastIndexOf("/"));
		minFrequency = MoreObjects.firstNonNull(minFrequency, 5);
		this.MIN_FREQUENCY = minFrequency < 0 ? 5 : minFrequency;
		minCount = MoreObjects.firstNonNull(minCount, minFrequency);
		this.MIN_COUNT = minCount < 0 ? this.MIN_FREQUENCY : minCount;
		this.wordSet = buildWordVocab(trainFile);
	}

	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(trainFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory.concat("/dep.context")));
			// initiate the list to store tokens of a sentence
			List<Token> sent = new ArrayList<>();
			Token root = new Token(0, "*root*", -1, "rroot");
			sent.add(root);
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
					if (sent.size() > 1) {
						String depContexts = this.extractDependency(sent);
						writer.write(depContexts);
						writer.flush();
						this.countFilter(new ArrayList<>(Arrays.asList(depContexts.split("\n"))));
					}
					sent = new ArrayList<>();
					sent.add(root);
				} else {
					String[] str = line.toLowerCase().split("\t");
					sent.add(new Token(Integer.parseInt(str[0]), str[1], Integer.parseInt(str[6]), str[7]));
				}
			}
			System.out.println("Dependency Contexts have extracted, file path: " + directory.concat("/dep.context"));
			reader.close();
			writer.close();
			V.sortAndReduceVocab(wv, MIN_COUNT);
			V.sortAndReduceVocab(cv, MIN_COUNT);
			System.out.println(String.format("Count and filter process finished. Minimum count: %s", MIN_COUNT));
			System.out.println(String.format("Word vocabulary size: %s\nContext vocabulary size: %s\nWords in train file: %s",
					wv.vocabSize, cv.vocabSize, trainedWordsCount));
			V.saveVocab(wv, directory.concat("/wv"));
			V.saveVocab(cv, directory.concat("/cv"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void countFilter(List<String> sentences) {
		int a, i;
		for (String line : sentences) {
			if (line.isEmpty())
				continue;
			String word = line.split(" ")[0];
			String context = line.split(" ")[1];
			if (word.length() > MAX_STRING)
				word = word.substring(0, MAX_STRING - 1);
			if (context.length() > MAX_STRING)
				context = context.substring(0, MAX_STRING - 1);
			trainedWordsCount++;
			i = V.searchVocab(wv, word);
			if (i == -1) {
				a = V.addWordToVocab(wv, word);
				wv.vocab.get(a).cn = 1;
			} else {
				wv.vocab.get(i).cn++;
			}
			i = V.searchVocab(cv, context);
			if (i == -1) {
				a = V.addWordToVocab(cv, context);
				cv.vocab.get(a).cn = 1;
			} else {
				cv.vocab.get(i).cn++;
			}
			V.ensureVocabSize(wv);
			V.ensureVocabSize(cv);
		}
	}

	/** @return (word, dependency_context) pairs */
	private String extractDependency(List<Token> sent) {
		StringBuilder sb = new StringBuilder();
		for (Token tok : sent) {
			if (tok.getRelation() == -1) continue;
			Token par = sent.get(tok.getRelation());
			String m = tok.getWord();
			String h;
			if (!wordSet.contains(m))
				continue;
			String ref = tok.getDependency();
			if (ref.equals("prep")) continue; // for english_SD model, if english_UD model is used, change to "adpmod"
			if (ref.equals("pobj") && par.getIndex() != 0) { // for english_SD model, if english_UD model is used, change to "adpobj"
				Token ppar = sent.get(par.getRelation());
				ref = par.getDependency().concat(":").concat(par.getWord());
				h = ppar.getWord();
			} else h = par.getWord();
			if (!wordSet.contains(h)) continue;
			sb.append(h).append(" ").append(ref).append("_").append(m).append("\n");
			sb.append(m).append(" ").append(ref).append("I_").append(h).append("\n");
		}
		return sb.toString();
	}

	/** @return {@link ImmutableSet} of word vocabulary */
	private ImmutableSet<String> buildWordVocab(String filename) {
		Map<String, AtomicInteger> map = new HashMap<>(2000000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				String str = line.split("\t")[1].trim();
				if (map.containsKey(str)) map.get(str).incrementAndGet();
				else map.put(str, new AtomicInteger(1));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashSet<String> wordSet = map.entrySet().stream().filter((Map.Entry<String, AtomicInteger> e) -> e.getValue().get() > MIN_FREQUENCY)
				.sorted((e1, e2) -> Integer.compare(e2.getValue().get(), e1.getValue().get()))
				.map(Map.Entry::getKey).collect(Collectors.toCollection(HashSet::new));
		return ImmutableSet.copyOf(wordSet);
	}

}
