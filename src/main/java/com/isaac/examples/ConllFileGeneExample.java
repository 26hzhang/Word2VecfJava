package com.isaac.examples;

import com.isaac.tools.ConllFileGenerator;

public class ConllFileGeneExample {

	public static void main(String[] args) {
		String filename = "";
		String resultpath = ConllFileGenerator.generate(filename);
		System.out.println("The location of generated conll file: " + resultpath);
	}

}
