package com.isaac.word2vecf.examples;

import java.util.List;

import com.isaac.word2vecf.Word2Vec;
import com.isaac.word2vecf.Word2VecModel;
import com.isaac.word2vecf.utils.Pair;

public class Word2VecExample {

	public static void main(String[] args) {
		String filename = "/Users/zhanghao/Documents/Files/GoogleNews-vectors-negative300.bin";
		System.out.println("loading embeddings...");
		Word2VecModel w2vModel = Word2VecModel.fromBinaryFile(filename);
		System.out.println("creating word2vec...");
		Word2Vec w2v = new Word2Vec(w2vModel, true);
		System.out.println("done...");
		String word = "kill";
		List<Pair<String, Double>> list = w2v.wordsNearest(word, 10);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getFirst() + "\t" + list.get(i).getSecond());
		}
		String word1 = "eat";
		String word2 = "pay";
		System.out.println(w2v.wordSimilarity(word1, word2));
	}

}
