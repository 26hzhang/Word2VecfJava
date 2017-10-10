package com.isaac.word2vecf.examples;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.isaac.word2vecf.models.ModelSerializer;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isaac.word2vecf.models.Word2Vecf;

/**
 * Created by zhanghao on 5/6/17.
 * Modified by zhanghao on 10/10/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Word2VecfExample {
	
	private static Logger log = LoggerFactory.getLogger(Word2VecfExample.class);

	public static void main(String[] args) {
		String wordsPath = "/home/zhanghao/Documents/embeddings/lexsub_words";
		String contextsPath = "/home/zhanghao/Documents/embeddings/lexsub_contexts";
		log.info("load word and context embeddings and create word2vecf...");
		Word2Vecf w2vf = ModelSerializer.loadWord2VecfModel(wordsPath, contextsPath, false);
		log.info("done...");
		// measure test
		/* Analogy task, "king" - "queen" = "man" - ["woman"], positive words: man, queen; negative words: king; target word: woman. */
		log.info("analogy test...");
		List<String> words = w2vf.wordsNearest(Arrays.asList("man", "queen"), Collections.singletonList("king"), 10);
		for (String word : words)
			log.info(word);

		/* similarity task */
		log.info("similarity test...");
		List<Pair<String, Double>> similarityWords = w2vf.wordsNearest("dog", 10);
		for (Pair<String, Double> pair : similarityWords)
			log.info(pair.getKey() + "\t" + pair.getValue());

		// word similarity
		log.info("word similarity...");
		double cosValue = w2vf.wordSimilarity("car", "truck");
		log.info(String.format("cosine similarity between car and truck is %.5f", cosValue));

		// lexical substitute by multiple method
		log.info("lexical substitute by multiple...");
		List<Pair<String, Double>> candidates = w2vf.lexicalSubstituteMult("jaguar", Arrays.asList("possI_engine", "poss_engine"), true, 10);
		for (Pair<String, Double> pair : candidates)
			log.info(pair.getKey() + "\t" + pair.getValue());

		// lexical substitute by add method
		log.info("lexical substitute by add...");
		candidates = w2vf.lexicalSubstituteAdd("jaguar", Arrays.asList("possI_engine", "poss_engine"), true, 10);
		for (Pair<String, Double> pair : candidates)
			log.info(pair.getKey() + "\t" + pair.getValue());

		// lexical substitutes by adaptive method
		log.info("lexical substitute by adaptive...");
		candidates = w2vf.lexicalSubstituteAdaptive("jaguar", Arrays.asList("possI_engine", "poss_engine"), 0.7, 10);
		for (Pair<String, Double> pair : candidates)
			log.info(pair.getKey() + "\t" + pair.getValue());
	}

}
