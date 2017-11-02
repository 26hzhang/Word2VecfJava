package com.isaac.word2vecf.utils;

import com.isaac.lexsub.representation.Token;
import com.isaac.word2vecf.utils.UnicodeReader;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by zhanghao on 18/4/17.
 * Modified by zhanghao on 02/11/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Common {

	/** Stanford CoreNLP Lemmatizer tool */
	private static final StanfordCoreNLP pipeline;
	static {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}

	/** @return true if i is an even number */
	public static boolean isEven (int i) { return (i & 1) == 0; }

	/** @return true if i is an odd number */
	public static boolean isOdd (int i) { return !isEven(i); }

	/**
	 * Read the file line for line and return the result in a list
	 * @throws IOException upon failure in reading, note that we wrap the underlying IOException with the file name
	 */
	public static List<String> readToList (File f) throws IOException {
		try (final Reader reader = asReaderUTF8Lenient(new FileInputStream(f))) {
			return readToList(reader);
		} catch (IOException ioe) {
			throw new IllegalStateException(String.format("Failed to read %s: %s", f.getAbsolutePath(), ioe), ioe);
		}
	}

	/** Read the Reader line for line and return the result in a list */
	public static List<String> readToList (Reader r) throws IOException {
		try (BufferedReader in = new BufferedReader(r)) {
			List<String> l = new ArrayList<>();
			String line;
			while ((line = in.readLine()) != null) l.add(line);
			return Collections.unmodifiableList(l);
		}
	}

	/** Wrap the InputStream in a Reader that reads UTF-8. Invalid content will be replaced by unicode replacement glyph. */
	public static Reader asReaderUTF8Lenient (InputStream in) {
		return new UnicodeReader(in, "utf-8");
	}

	/** determine whether the input string is a number */
	public static boolean isNumeric (String str) {
		return Pattern.compile("[\\d]+").matcher(str).matches();
	}

	/** determine whether the input string contains only alphanumeric character */
	public static boolean isAlphanumeric (String str) {
		return Pattern.compile("[\\w]+").matcher(str).matches();
	}

	/** @return file size */
	public static long getFileSize (String filename) {
		long fsize = 0;
		try {
			RandomAccessFile file = new RandomAccessFile(filename, "r");
			fsize = file.length();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fsize;
	}

	/** @return Lemmatized string, input string can be a word, sentence or paragraph */
	public static String lemmatizer(String string) {
		List<String> lemmas = new ArrayList<>();
		// create an empty Annotation just with the given text
		Annotation annotation = new Annotation(string);
		// run all Annotators on this string
		pipeline.annotate(annotation);
		// Iterate over all of the sentences found
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the list of lemmas
				lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
			}
		}
		return String.join(" ", lemmas);
	}

	/** @return list of sentences, split from a paragraph or long sentence */
	public static List<String> sentenceSplitter (String paragraph, StanfordCoreNLP pipeline) {
		List<String> sentences = new LinkedList<>();
		if (pipeline == null) {
			Properties props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit");
			pipeline = new StanfordCoreNLP(props);
		}
		Annotation annotation = new Annotation(paragraph);
		pipeline.annotate(annotation);
		List<CoreMap> sents = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		if (sents == null) return sentences;
		for (CoreMap sent : sents) {
			sentences.add(sent.toString().trim());
		}
		return sentences;
	}

	/** Convert Glove embeddings format to Word2Vec embeddings format */
	public static void GloveToWord2VecEmbeddingFormat (String filePath, String toPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String fstLine = reader.readLine();
			int vocabSize = 1;
			int layerSize = fstLine.split(" ").length - 1; // count vector size (format: word vectors)
			while (reader.readLine() != null) vocabSize++; // count vocabulary size
			reader.close();
			reader = new BufferedReader(new FileReader(filePath));
			BufferedWriter writer = new BufferedWriter(new FileWriter(toPath));
			writer.write(String.valueOf(vocabSize).concat(" ").concat(String.valueOf(layerSize)).concat("\n"));
			String line;
			while ((line = reader.readLine()) != null) writer.write(line.trim().concat("\n"));
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
