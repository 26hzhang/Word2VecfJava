package com.isaac.utils;

import com.isaac.representation.Vocabulary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VocabAndDepsExtract {
	/** Longest length of a word allowed */
	private static final int MAX_STRING = 100;
	/** drop frequency and minimal count */
	private final int MIN_FREQUENCY;
	private final int MIN_COUNT;
	/** the set of words which appearance frequency larger than MIN_FREQUENCY */
	private Set<String> wordSet;
	/** count whole train words */
	private static long train_words = 0;
	/** train file, conll format */
	private final File train_file;
	/** path of results to store */
	private final File depContexts_file;
	private String wvocab_file;
	private String cvocab_file;
	/** create function library to deal with word and context vocabularies */
	private static final VocabFunctions V = new VocabFunctions();
	/** create word and context vocabularies */
	private final Vocabulary wv = V.CreateVocabulary();
	private final Vocabulary cv = V.CreateVocabulary();

	public VocabAndDepsExtract(String filename, int MIN_FREQUENCY, int MIN_COUNT) {
		this.MIN_FREQUENCY = MIN_FREQUENCY;
		this.MIN_COUNT = MIN_COUNT;
		this.train_file = new File(filename);
		this.wordSet = new HashSet<>(this.countWords(train_file));
		this.depContexts_file = new File(this.train_file.getParent() + "/dep.contexts");
		this.wvocab_file = this.train_file.getParent() + "/wv";
		this.cvocab_file = this.train_file.getParent() + "/cv";
	}

	/**
	 * Start to train and filter the original conll corpus
	 * @return the parent path of generated files
	 */
	public String train() {
		try {
			this.extractDepAndCountFilter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return train_file.getParent() + "/";
	}

	/** extract dep.context and generate wv, cv files */
	private void extractDepAndCountFilter() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(train_file)));
		BufferedWriter depWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(depContexts_file)));
		// initiate the list to store tokens of a sentence
		List<Tokens> sent = new ArrayList<>();
		Tokens root = new Tokens(0, "*root*", -1, "rroot");
		sent.add(root);
		String line;
		while ((line = br.readLine()) != null) {
			if (line.equals("")) {
				if (sent.size() > 1) {
					String depContexts = this.extractDependency(sent);
					depWriter.write(depContexts);
					depWriter.flush();
					this.counterAndFilter(new ArrayList<>(Arrays.asList(depContexts.split("\n"))));
				}
				sent = new ArrayList<>();
				sent.add(root);
			} else {
				String[] str = line.toLowerCase().split("\t");
				sent.add(new Tokens(Integer.parseInt(str[0]), str[2], Integer.parseInt(str[6]), str[7]));
			}
		}
		System.out.println("Dependency Contexts have extracted, file path: " + depContexts_file.getAbsolutePath());
		br.close();
		depWriter.close();
		V.SortAndReduceVocab(wv, MIN_COUNT);
		V.SortAndReduceVocab(cv, MIN_COUNT);
		System.out.println(String.format("Count and filter process finished. Minimum count: %s", MIN_COUNT));
		System.out.println(
				String.format("Word vocabulary size: %s\nContext vocabulary size: %s\nWords in train file: %s", wv.vocab_size, cv.vocab_size, train_words));
		V.SaveVocab(wv, wvocab_file);
		V.SaveVocab(cv, cvocab_file);
	}

	private void counterAndFilter(List<String> sentences) throws IOException {
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
			train_words++;
			i = V.SearchVocab(wv, word);
			if (i == -1) {
				a = V.AddWordToVocab(wv, word);
				wv.vocab.get(a).cn = 1;
			} else {
				wv.vocab.get(i).cn++;
			}
			i = V.SearchVocab(cv, context);
			if (i == -1) {
				a = V.AddWordToVocab(cv, context);
				cv.vocab.get(a).cn = 1;
			} else {
				cv.vocab.get(i).cn++;
			}
			V.EnsureVocabSize(wv);
			V.EnsureVocabSize(cv);
		}
	}

	private String extractDependency(List<Tokens> sent) {
		StringBuilder sb = new StringBuilder();
		for (Tokens tok : sent) {
			if (tok.getRelation() == -1)
				continue;
			Tokens par = sent.get(tok.getRelation());
			String m = tok.getWord();
			String h = "";
			if (!wordSet.contains(m))
				continue;
			String ref = tok.getDependency();
			if (ref.equals("adpmod"))
				continue;
			if (ref.equals("adpobj") && par.getIndex() != 0) {
				Tokens ppar = sent.get(par.getRelation());
				ref = new StringBuilder().append(par.getDependency()).append(":").append(par.getWord()).toString();
				h = ppar.getWord();
			} else
				h = par.getWord();
			if (!wordSet.contains(h))
				continue;
			sb.append(h).append(" ").append(ref).append("_").append(m).append("\n");
			sb.append(m).append(" ").append(ref).append("I_").append(h).append("\n");
		}
		return sb.toString();
	}
	
	private void countWords2File(List<String> words, List<Integer> counts) {
		String filename = this.train_file.getParent() + "/counted_vocabulary";
		try {
			File file = new File(filename);
			if (file.exists())
				file.delete();
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (int i = 0; i < words.size(); i++) {
				bw.write(words.get(i) + " " + counts.get(i) + "\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> countWords(File file) {
		Map<String, AtomicInteger> map = new HashMap<String, AtomicInteger>(2000000);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					String str = line.split("\t")[2].trim();
					if (map.containsKey(str))
						map.get(str).incrementAndGet();
					else
						map.put(str, new AtomicInteger(1));
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// add map elements to the list and sort according to the value
		List<Map.Entry<String, AtomicInteger>> list = new ArrayList<>(map.entrySet());
		// create a comparator to compare the value of each element, and sort the list
		list = list.stream().sorted((a, b) -> Integer.compare(b.getValue().get(), a.getValue().get())).collect(Collectors.toList());
		List<String> words = new ArrayList<>();
		List<Integer> counts = new ArrayList<>();
		for (Entry<String, AtomicInteger> entry : list) {
			if (entry.getValue().intValue() < MIN_FREQUENCY)
				break;
			words.add(entry.getKey());
			counts.add(entry.getValue().intValue());
		}
		countWords2File(words, counts);
		System.out.println("count words finished and stored in the wordSet, drop frequency: " + MIN_FREQUENCY);
		return words;
	}
	
	class Tokens {
		private int index;
		private String word;
		private int relation;
		private String dependency;

		public Tokens(int index, String word, int relation, String dependency) {
			this.index = index;
			this.word = word;
			this.relation = relation;
			this.dependency = dependency;
		}

		public int getIndex() {
			return index;
		}
		
		public String getWord() {
			return word;
		}

		public int getRelation() {
			return relation;
		}

		public String getDependency() {
			return dependency;
		}
	}
	
}
