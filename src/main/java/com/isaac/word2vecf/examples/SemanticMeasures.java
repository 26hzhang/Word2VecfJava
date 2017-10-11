package com.isaac.word2vecf.examples;

import com.isaac.word2vecf.models.ModelSerializer;
import com.isaac.word2vecf.models.Word2Vec;
import com.isaac.word2vecf.measure.AnalogyMeasure;
import com.isaac.word2vecf.measure.TOEFLMeasure;
import com.isaac.word2vecf.measure.WS353Measure;

/**
 * Created by zhanghao on 20/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class SemanticMeasures {
	public static void main(String[] args) {
		Word2Vec w2v = ModelSerializer.loadWord2VecModel("/home/zhanghao/Documents/GoogleNews-vectors-negative300.bin", true);
		// 1. TOEFL test
		TOEFLMeasure.measure(w2v);
		// 2. Analogy test -- "king - queen = man - woman"
		AnalogyMeasure.measure(w2v);
		// 3. WS353 test
		WS353Measure.measure(w2v);
		// save Word2Vec Model to binary file
		ModelSerializer.saveWord2VecToBinary("<Path to Save>", w2v);
	}
}
