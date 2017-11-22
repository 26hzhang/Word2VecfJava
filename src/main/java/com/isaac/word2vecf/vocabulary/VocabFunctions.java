package com.isaac.word2vecf.vocabulary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.isaac.word2vecf.vocabulary.Vocabulary.VocabWord;

/**
 * Created by zhanghao on 18/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
@SuppressWarnings("all")
public class VocabFunctions {

	private static final int vocabHashSize = 30000000; // Maximum 30*0.7=21M words in the vocabulary
	private static final int MAX_STRING = 100;
	private static final int MAX_INTEGER = Integer.MAX_VALUE;
	private static int minReduce = 1;

	/** @return hash value of a word */
	private int getWordHash(String word) {
		return (word.hashCode() & MAX_INTEGER) % vocabHashSize;
	}

	/** @return position of a word in the vocabulary, -1 if the word not found */
	public int searchVocab (Vocabulary v, String word) {
		int hash = getWordHash(word);
		while (true) {
			if (v.vocabHash[hash] == -1) return -1;
			if (word.equals(v.vocab.get(v.vocabHash[hash]).word)) return v.vocabHash[hash];
			hash = (hash + 1) % vocabHashSize;
		}
	}

	/** @return last index of vocabulary, adds a word to the vocabulary */
	public int addWordToVocab (Vocabulary v, String word) {
		int hash;
		int length = word.length() > MAX_STRING ? MAX_STRING : word.length();
		VocabWord vocab = new Vocabulary().new VocabWord();
		vocab.word = word;
		vocab.cn = 0;
		v.vocab.add(vocab);
		v.vocabSize++;
		if (v.vocabSize + 2 >= v.vocabMaxSize) v.vocabMaxSize = v.vocabMaxSize + 1000;
		hash = getWordHash(word);
		while (v.vocabHash[hash] != -1) hash = (hash + 1) % vocabHashSize;
		v.vocabHash[hash] = v.vocabSize - 1;
		return v.vocabSize - 1;
	}

	/** Sorts the vocabulary by frequency using word counts */
	public void sortAndReduceVocab (Vocabulary v, int min_count) {
		int hash;
		v.vocab = v.vocab.stream().sorted((e1, e2) -> (int) (e2.cn - e1.cn)).collect(Collectors.toList());
		int size = v.vocabSize;
		v.wordCount = 0;
		for (int index = 0; index < size; index++) {
			if (v.vocab.get(index).cn < min_count) {
				v.vocab = v.vocab.subList(0, index);
				v.vocabSize = v.vocab.size();
				break;
			}
		}
		// initialize hash table
		for (int a = 0; a < vocabHashSize; a++) {
			v.vocabHash[a] = -1;
		}
		// Hash will be recomputed, as after the sorting it is not actual
		for (int a = 0; a < v.vocabSize; a++) {
			hash = getWordHash(v.vocab.get(a).word);
			while (v.vocabHash[hash] != -1) hash = (hash + 1) % vocabHashSize;
			v.vocabHash[hash] = a;
			v.wordCount = v.wordCount + v.vocab.get(a).cn;
		}
	}

	/** @return {@link Vocabulary} Creates the vocabulary */
	public Vocabulary createVocabulary () {
		Vocabulary v = new Vocabulary();
		v.vocabMaxSize = 1000;
		v.vocabSize = 0;
		v.vocab = new ArrayList<>(2000000);
		v.vocabHash = new int[vocabHashSize];
		for (int a = 0; a < vocabHashSize; a++) v.vocabHash[a] = -1;
		return v;
	}

	/** Saves to a file */
	public void saveVocab (Vocabulary v, String vocabFilePath) throws IOException {
		File file = new File(vocabFilePath);
		//if (file.exists()) file.delete();
		//file.createNewFile();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		for (int i = 0; i < v.vocabSize; i++) {
			bw.write(v.vocab.get(i).word + " " + v.vocab.get(i).cn + "\n");
		}
		bw.close();
	}

	/** Reduce the vocabulary by removing infrequent tokens */
	public void reduceVocab (Vocabulary v) {
		int hash;
		v.vocab = v.vocab.stream().sorted((e1, e2) -> (int) (e2.cn - e1.cn)).collect(Collectors.toList());
		int size = v.vocabSize;
		v.wordCount = 0;
		int index = 0;
		while (index < size) {
			if (v.vocab.get(index).cn < minReduce) {
				v.vocab = v.vocab.subList(0, index);
				v.vocabSize = v.vocab.size();
				break;
			}
			index++;
		}
		for (int i = 0; i < vocabHashSize; i++) {
			v.vocabHash[i] = -1;
		}
		for (int i = 0; i < v.vocabSize; i++) {
			hash = getWordHash(v.vocab.get(i).word);
			while (v.vocabHash[hash] != -1) hash = (hash + 1) % vocabHashSize;
			v.vocabHash[hash] = i;
		}
		minReduce++;
	}

	public void ensureVocabSize (Vocabulary vocab) {
		if (vocab.vocabSize > vocabHashSize * 0.7) reduceVocab(vocab);
	}

}
