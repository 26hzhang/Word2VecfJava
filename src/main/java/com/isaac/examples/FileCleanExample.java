package com.isaac.examples;

import com.isaac.tools.Tools;

public class FileCleanExample {

	public static void main(String[] args) {
		String filename = "C:/Users/Dr Erik/Desktop/corpus4";
		Tools.RawCorpusClean(filename, true, true, true, true,
				true);
	}

}
