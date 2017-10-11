package com.isaac.word2vecf.examples;

import com.isaac.word2vecf.utils.ConllFileGenerator;

/**
 * Created by zhanghao on 20/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 *
 * CoNLL file is trained by Stanford Core NLP library, which will spend a lot of time to deal with this task.
 *
 */
public class ConllFileGenerationExample {
	public static void main (String[] args) {
		String filePath = "<path to the raw corpus>";
		ConllFileGenerator.generate(filePath);
		System.out.println("CoNLL format file is generated, located at the same directory as train file.");
	}
}
