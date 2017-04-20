package com.isaac.word2vecf.examples;

import com.isaac.word2vecf.vocabulary.ExtractionImpl;

/**
 * Created by zhanghao on 20/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 *
 * Gernerate dependency based (word context) pairs as training file for word2vecf
 * Generate word vocabulary
 * Generate context vocabulary
 *
 */
public class WrdCtxDepsExtractionExample {
	public static void main(String[] args) {
		String filePath = "<Your CoNLL file>"; // the training file of this task is the CoNLL format file derived from ConllFileGenerator
		Integer minFrequency = 50; // if null or small than 0, default value is 5
		Integer minCount = 100; // if null or small than 0, same as minFrequency
		new ExtractionImpl(filePath, minFrequency, minCount).run();
		System.out.println("Finished!!!");
	}
}
