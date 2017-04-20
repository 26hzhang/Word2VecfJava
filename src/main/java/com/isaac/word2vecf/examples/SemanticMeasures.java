package com.isaac.word2vecf.examples;

import com.isaac.word2vecf.Word2Vecf;
import com.isaac.word2vecf.Word2VecfModel;
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
		Word2Vecf w2vf = new Word2Vecf(Word2VecfModel.fromBinaryFile("<word vector path>", null), true);
		// 1. TOEFL test
		TOEFLMeasure.measure(w2vf);
		// 2. Analogy test -- "king - queen = man - woman"
		AnalogyMeasure.measure(w2vf);
		// 3. WS353 test
		WS353Measure.measure(w2vf);
	}
}
