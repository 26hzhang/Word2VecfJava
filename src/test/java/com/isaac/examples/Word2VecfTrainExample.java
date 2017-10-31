package com.isaac.examples;

import com.isaac.word2vecf.models.ModelSerializer;
import com.isaac.word2vecf.models.Word2Vecf;
import com.isaac.word2vecf.models.Word2VecfTrainerBuilder;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhanghao on 20/4/17.
 * Modified by zhanghao on 10/10/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Word2VecfTrainExample {
	public static void main(String[] args) {
		String trainFile = "<Your training file>";
		String wordVocabFile = "<Your word vocabulary file>";
		String contextVocabFile = "<Your context vocabulary file>";
		Word2Vecf w2vf = Word2VecfTrainerBuilder.trainer()
				.setLayerSize(500)
				.setNumIterations(5)
				.useNumThreads(10)
				.setMinVocabFrequency(50)
				.setInitialLearningRate(0.025)
				.useNegativeSamples(15)
				.setDownSamplingRate(0.001)
				.setDebugMode(2)
				.train(trainFile, wordVocabFile, contextVocabFile);

		// write the model to a bin file
		ModelSerializer.saveWord2VecfToBinary("<path to store word vectors>", "<path to store context vectors>", w2vf);

		// measure test
		/* Analogy task, "king" - "queen" = "man" - ["woman"], positive words: man, queen; negative words: king; target word: woman. */
		List<String> words = w2vf.wordsNearest(Arrays.asList("man", "queen"), Collections.singletonList("king"), 10);
		for (String word : words)
			System.out.print(word + "\t");
		System.out.println("\n");

		/* similarity task */
		List<Pair<String, Double>> similarityWords = w2vf.wordsNearest("dog", 10);
		for (Pair<String, Double> pair : similarityWords)
			System.out.println(pair.getKey() + "\t" + pair.getValue());
		System.out.println("\n");

		// word similarity
		double cosValue = w2vf.wordSimilarity("car", "truck");
		System.out.println(String.format("cosine similarity between car and truck is %.5f", cosValue));

		// lexical substitute by multiple method
		List<Pair<String, Double>> candidates = w2vf.lexicalSubstituteMult("jaguar", Arrays.asList("possI_engine", "poss_engine"), true, 10);
		for (Pair<String, Double> pair : candidates)
			System.out.println(pair.getKey() + "\t" + pair.getValue());
		System.out.println("\n");

		// lexical substitute by add method
		candidates = w2vf.lexicalSubstituteAdd("jaguar", Arrays.asList("possI_engine", "poss_engine"), true, 10);
		for (Pair<String, Double> pair : candidates)
			System.out.println(pair.getKey() + "\t" + pair.getValue());
		System.out.println("\n");

		// lexical substitutes by adaptive method
		candidates = w2vf.lexicalSubstituteAdaptive("jaguar", Arrays.asList("possI_engine", "poss_engine"), 0.7, 10);
		for (Pair<String, Double> pair : candidates)
			System.out.println(pair.getKey() + "\t" + pair.getValue());
		System.out.println("\n");
	}
}
