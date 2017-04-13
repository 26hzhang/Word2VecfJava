package com.isaac.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.isaac.representation.Vocabulary;
import com.isaac.representation.Vocabulary.VocabWord;


public class VocabFunctions {

	public static final int vocab_hash_size = 30000000; // Maximum 30*0.7=21M words in the vocabulary
	private static final int MAX_STRING = 100;
	private static final int MAX_INTEGER = Integer.MAX_VALUE;
	private static int min_reduce = 1;

	// Returns hash value of a word
	public int GetWordHash(String word) {
		return (word.hashCode() & MAX_INTEGER) % vocab_hash_size;
	}

	// Returns position of a word in the vocabulary; if the word is not found returns -1
	public int SearchVocab(Vocabulary v, String word) {
		int hash = GetWordHash(word);
		while (true) {
			if (v.vocab_hash[hash] == -1)
				return -1;
			if (word.equals(v.vocab.get(v.vocab_hash[hash]).word))
				return v.vocab_hash[hash];
			hash = (hash + 1) % vocab_hash_size;
		}
	}

	// Adds a word to the vocabulary
	public int AddWordToVocab(Vocabulary v, String word) {
		int hash;
		int length = word.length() > MAX_STRING ? MAX_STRING : word.length();
		VocabWord vocab = new Vocabulary().new VocabWord();
		vocab.word = word;
		vocab.cn = 0;
		v.vocab.add(vocab);
		v.vocab_size++;
		if (v.vocab_size + 2 >= v.vocab_max_size) {
			v.vocab_max_size = v.vocab_max_size + 1000;
		}
		hash = GetWordHash(word);
		while (v.vocab_hash[hash] != -1) {
			hash = (hash + 1) % vocab_hash_size;
		}
		v.vocab_hash[hash] = v.vocab_size - 1;
		return v.vocab_size - 1;
	}

	// Sorts the vocabulary by frequency using word counts
	public void SortAndReduceVocab(Vocabulary v, int min_count) {
		int hash;
		/*Collections.sort(v.vocab, new Comparator<VocabWord>() {
			public int compare(VocabWord o1, VocabWord o2) { return (int) (o2.cn - o1.cn);}
		});*/
		v.vocab = v.vocab.stream().sorted((e1, e2) -> (int) (e2.cn - e1.cn)).collect(Collectors.toList());
		int size = v.vocab_size;
		v.word_count = 0;
		int index = 0;
		while (index < size) {
			// Words occurring less than min_count times will be discarded from the vocab
			if (v.vocab.get(index).cn < min_count) {
				v.vocab = v.vocab.subList(0, index);
				v.vocab_size = v.vocab.size();
				break;
			}
			index++;
		}
		// initialize hash table
		for (int a = 0; a < vocab_hash_size; a++) {
			v.vocab_hash[a] = -1;
		}
		// Hash will be recomputed, as after the sorting it is not actual
		for (int a = 0; a < v.vocab_size; a++) {
			hash = GetWordHash(v.vocab.get(a).word);
			while (v.vocab_hash[hash] != -1) {
				hash = (hash + 1) % vocab_hash_size;
			}
			v.vocab_hash[hash] = a;
			v.word_count = v.word_count + v.vocab.get(a).cn;
		}
	}

	// Creates the vocabulary
	public Vocabulary CreateVocabulary() {
		Vocabulary v = new Vocabulary();
		v.vocab_max_size = 1000;
		v.vocab_size = 0;
		v.vocab = new ArrayList<>(2000000);
		v.vocab_hash = new int[vocab_hash_size];
		for (int a = 0; a < vocab_hash_size; a++)
			v.vocab_hash[a] = -1;
		return v;
	}

	public void SaveVocab(Vocabulary v, String save_vocab_file) throws IOException {
		File file = new File(save_vocab_file);
		if (file.exists())
			file.delete();
		file.createNewFile();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		for (int i = 0; i < v.vocab_size; i++) {
			bw.write(v.vocab.get(i).word + " " + v.vocab.get(i).cn + "\n");
		}
		bw.close();
	}

	// Reduce the vocabulary by removing infrequent tokens
	public void ReduceVocab(Vocabulary v) {
		int hash;
		/*Collections.sort(v.vocab, new Comparator<VocabWord>() {
			public int compare(VocabWord o1, VocabWord o2) {
				return (int) (o2.cn - o1.cn);
			}
		});*/
		v.vocab = v.vocab.stream().sorted((e1, e2) -> (int) (e2.cn - e1.cn)).collect(Collectors.toList());
		int size = v.vocab_size;
		v.word_count = 0;
		int index = 0;
		while (index < size) {
			if (v.vocab.get(index).cn < min_reduce) {
				v.vocab = v.vocab.subList(0, index);
				v.vocab_size = v.vocab.size();
				break;
			}
			index++;
		}
		for (int i = 0; i < vocab_hash_size; i++) {
			v.vocab_hash[i] = -1;
		}
		for (int i = 0; i < v.vocab_size; i++) {
			hash = GetWordHash(v.vocab.get(i).word);
			while (v.vocab_hash[hash] != -1) {
				hash = (hash + 1) % vocab_hash_size;
			}
			v.vocab_hash[hash] = i;
		}
		min_reduce++;
	}

	public void EnsureVocabSize(Vocabulary vocab) {
		if (vocab.vocab_size > vocab_hash_size * 0.7)
			ReduceVocab(vocab);
	}

}
