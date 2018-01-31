package com.isaac.examples.word2vecf;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.isaac.word2vecf.utils.WorNetUtils;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isaac.word2vecf.Word2Vec;
import com.isaac.word2vecf.utils.WordVectorSerializer;
import com.isaac.word2vecf.utils.Common;

public class WordNetExample {
	
	private static Logger log = LoggerFactory.getLogger(WordNetExample.class);

	public static void main(String[] args) {
		String filename = "/home/zhanghao/Documents/GoogleNews-vectors-negative300.bin";
		log.info("load google news embeddings and create word2vec...");
		Word2Vec w2v = WordVectorSerializer.loadWord2VecModel(filename, true);
		log.info("done...");
		String word = "kill";
		log.info("find top 5000 nearest words...");
		List<Pair<String, Double>> wordLst = w2v.wordsNearest(word, 5000);
		log.info("done...");
		
		// Filtering
		log.info("lemmatizing, filtering top 100 nearest distinct verbs...");
		LinkedList<Pair<String, Double>> result = new LinkedList<>();
		LinkedHashSet<String> vocab = new LinkedHashSet<>();
		for (Pair<String, Double> aWordLst : wordLst) {
			String str = Common.lemmatizer(aWordLst.getKey());
			if (str.equals(word) || !WorNetUtils.isVerb(str))
				continue;
			if (vocab.contains(str))
				continue;
			vocab.add(str);
			result.add(new Pair<>(str, aWordLst.getValue()));
		}
		result = result.stream().sorted((e1, e2) -> Double.valueOf(e2.getValue()).compareTo(Double.valueOf(e1.getValue())))
				.limit(100).collect(Collectors.toCollection(LinkedList::new));
		for (Pair<String, Double> pair : result) {
			System.out.println(pair.toString());
		}
		log.info("done...");
	}

}
