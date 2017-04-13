package com.isaac.examples;

import com.isaac.utils.ConllFileGenerator;

public class ConllFileGeneExample {

	public static void main(String[] args) {
		String filename = "";
		String resultPath = ConllFileGenerator.generate(filename);
		System.out.println("The location of generated conll file: " + resultPath);
	}

}
