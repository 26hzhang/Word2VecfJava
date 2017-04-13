package com.isaac.utils;

import java.util.ArrayList;
import java.util.List;

public class Vocabulary {
	public List<VocabWord> vocab;
	public long vocab_max_size;
	public int vocab_size;
	public long word_count;
	public int[] vocab_hash;

	public List<String> wordSet() {
		List<String> list = new ArrayList<>();
		for (VocabWord vw : vocab)
			list.add(vw.word);
		return list;
	}

	public class VocabWord {
		public long cn;
		public String word;

		public VocabWord() {
		}
	}

}
