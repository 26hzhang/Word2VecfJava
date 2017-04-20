package com.isaac.word2vecf.vocabulary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghao on 18/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Vocabulary {
	public List<VocabWord> vocab;
	public long vocabMaxSize;
	public int vocabSize;
	public long wordCount;
	public int[] vocabHash;

	public List<String> wordSet() {
		List<String> list = new ArrayList<>();
		for (VocabWord vw : vocab)
			list.add(vw.word);
		return list;
	}

	public class VocabWord {
		public long cn;
		public String word;
		public VocabWord() {}
	}
}
