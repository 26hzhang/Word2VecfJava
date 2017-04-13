package com.isaac.examples;

import com.isaac.utils.CorpusTools;

public class FileCleanExample {

	public static void main(String[] args) {
		String filename = "C:/Users/Dr Erik/Desktop/corpus4";
		CorpusTools.RawCorpusClean(filename, true, true, true, true,
				true);
	}

}
