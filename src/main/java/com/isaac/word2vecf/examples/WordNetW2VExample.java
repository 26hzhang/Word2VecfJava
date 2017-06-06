package com.isaac.word2vecf.examples;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isaac.word2vecf.Word2Vec;
import com.isaac.word2vecf.Word2VecModel;
import com.isaac.word2vecf.utils.Common;
import com.isaac.word2vecf.utils.Pair;
import com.isaac.word2vecf.utils.WordNetUtils;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class WordNetW2VExample {
	
	private static Logger log = LoggerFactory.getLogger(WordNetW2VExample.class);

	public static void main(String[] args) {
		String filename = "/home/zhanghao/Documents/GoogleNews-vectors-negative300.bin";
		log.info("load google news embeddings...");
		Word2VecModel w2vModel = Word2VecModel.fromBinaryFile(filename);
		log.info("create word2vec...");
		Word2Vec w2v = new Word2Vec(w2vModel, true);
		log.info("done...");
		String word = "kill";
		log.info("find top 5000 nearest words...");
		List<Pair<String, Double>> wordLst = w2v.wordsNearest(word, 5000);
		log.info("done...");
		
		// Filtering
		log.info("lemmatizing, filtering top 100 nearest distinct verbs...");
		LinkedList<Pair<String, Double>> result = new LinkedList<>();
		LinkedHashSet<String> vocab = new LinkedHashSet<>();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		for (int i = 0; i < wordLst.size(); i++) {
			String str = Common.lemmatizer(wordLst.get(i).getFirst(), pipeline);
			if (str.equals(word) || !WordNetUtils.isVerb(str))
				continue;
			if (vocab.contains(str))
				continue;
			vocab.add(str);
			result.add(Pair.cons(str, wordLst.get(i).getSecond()));
		}
		result = result.stream().sorted((e1, e2) -> Double.valueOf(e2.getSecond()).compareTo(Double.valueOf(e1.getSecond()))).limit(100).collect(Collectors.toCollection(LinkedList::new));
		for (Pair<String, Double> pair : result) {
			System.out.println(pair.toString());
		}
		log.info("done...");
	}

}
