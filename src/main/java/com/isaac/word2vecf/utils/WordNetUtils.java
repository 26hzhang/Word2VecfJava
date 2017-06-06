package com.isaac.word2vecf.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

public class WordNetUtils {
	
	public static final String WORDNETHOME = "src/main/resources/dict3.1";
	public static final IDictionary wndict;
	static {
		URL url = null;
		try {
			url = new URL("file", null, WORDNETHOME);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		wndict = new Dictionary(url);
		try {
			wndict.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isVerb(String word) {
		IIndexWord idxWord = wndict.getIndexWord(word, POS.VERB);
		if (idxWord == null)
			return false;
		else
			return true;
	}
	
	public static boolean isNoun(String word) {
		IIndexWord idxWord = wndict.getIndexWord(word, POS.NOUN);
		if (idxWord == null)
			return false;
		else
			return true;
	}
	
	public static boolean isAdjective(String word) {
		IIndexWord idxWord = wndict.getIndexWord(word, POS.ADJECTIVE);
		if (idxWord == null)
			return false;
		else
			return true;
	}
	
	public static boolean isAdverb(String word) {
		IIndexWord idxWord = wndict.getIndexWord(word, POS.ADVERB);
		if (idxWord == null)
			return false;
		else
			return true;
	}
}
