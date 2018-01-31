package com.isaac.examples.word2vecf;

import java.util.List;

import com.isaac.word2vecf.Word2Vec;
import com.isaac.word2vecf.utils.WordVectorSerializer;
import javafx.util.Pair;

public class Word2VecExample {

	public static void main(String[] args) {
		String filename = "/Users/zhanghao/Documents/Files/GoogleNews-vectors-negative300.bin";
		System.out.println("loading embeddings and creating word2vec...");
		Word2Vec w2v = WordVectorSerializer.loadWord2VecModel(filename, true);
		System.out.println("done...");
		String word = "kill";
		List<Pair<String, Double>> list = w2v.wordsNearest(word, 10);
		for (Pair<String, Double> aList : list) {
			System.out.println(aList.getKey() + "\t" + aList.getValue());
		}
		String word1 = "tree";
		String word2 = "oxygen";
		System.out.println(w2v.wordSimilarity(word1, word2));
		String word3 = "yard";
		System.out.println(w2v.wordSimilarity(word1, word3));
	}

}
