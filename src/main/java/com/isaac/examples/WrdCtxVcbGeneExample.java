package com.isaac.examples;

import com.isaac.utils.VocabAndDepsExtract;

public class WrdCtxVcbGeneExample {
	
	public static final int MIN_FREQUENCY = 50;
	public static final int MIN_COUNT = 100;
	// Word / Context Vocabulary Generation Example
	public static void main(String[] args) {
		String filename = ""; // conll filename
		VocabAndDepsExtract extract = new VocabAndDepsExtract(filename, MIN_FREQUENCY, MIN_COUNT);
		extract.train();
		System.out.println("Finished!!");
	}

}
