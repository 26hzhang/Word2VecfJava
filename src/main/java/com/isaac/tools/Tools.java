package com.isaac.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import net.sf.junidecode.Junidecode;

public class Tools {

	/**
	 * Clean up the original / raw text corpus, makes it more accurate and less
	 * errors
	 * 
	 * @param filename
	 *            path of the file to be processed
	 * @param lowercase
	 *            convert all the words to lowercase form, default true
	 * @param splitSentence
	 *            split paragraph to sentences by stanford dependency parser,
	 *            default true
	 * @param abbr2Full
	 *            convert some abbreviation formats to the full format, e.g.:
	 *            I'm --> I am, you're --> you are, default true
	 * @param deleteRedundantPuncts
	 *            delete the redundant punctuation, default true
	 * @param uk2usSpells
	 *            change the word with British spell to U.S. spell, default true
	 * @return the directory location of clean result
	 */
	public static String RawCorpusClean(String filename, boolean lowercase, boolean splitSentence, boolean abbr2Full, boolean deleteRedundantPuncts,
			boolean uk2usSpells) {
		Map<String, String> abbr2full = loadAbbr2FullMap();
		Map<String, String> uk2us = loadUk2usMap();
		File file = new File(filename);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			File outfile = new File(filename + ".clean");
			if (outfile.exists())
				outfile.delete();
			outfile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "ascii"));
			Properties props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			String line = "";
			while ((line = br.readLine()) != null) {
				line = Junidecode.unidecode(line); // convert unicode to 7-bits ASCII valid string
				if (lowercase)
					line = line.toLowerCase();
				if (splitSentence) {
					Annotation annotation = new Annotation(line);
					pipeline.annotate(annotation);
					List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
					if (sentences != null) {
						for (int i = 0, sz = sentences.size(); i < sz; i++) {
							String sentence = sentences.get(i).toString();
							if (abbr2Full)
								sentence = Abbr2Full(sentence, abbr2full);
							if (deleteRedundantPuncts)
								sentence = DeleteRedundantPuncts(sentence);
							if (uk2usSpells)
								sentence = UK2USSpell(sentence, uk2us);
							bw.write(sentence + "\n");
							bw.flush();
						}
					}
				} else {
					if (abbr2Full)
						line = Abbr2Full(line, abbr2full);
					if (deleteRedundantPuncts)
						line = DeleteRedundantPuncts(line);
					if (uk2usSpells)
						line = UK2USSpell(line, uk2us);
					bw.write(line + "\n");
					bw.flush();
				}

			}
			br.close();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getParent();
	}

	/**
	 * Use stanford dependency parser to lemmatize each word within a sentence
	 * 
	 * @param string
	 *            string to be lemmatized
	 * @return lemmatized string result
	 */
	public static String Lemmatizer(String string) {
		String lemmas = "";
		//List<String> lemmas = new LinkedList<String>();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// create an empty Annotation just with the given text
		Annotation annotation = new Annotation(string);
		// run all Annotators on this string
		pipeline.annotate(annotation);
		// Iterate over all of the sentences found
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the list of lemmas
				//lemmas.add(token.get(LemmaAnnotation.class));
				lemmas += token.get(LemmaAnnotation.class) + " ";
			}
		}
		return lemmas.trim();
	}

	/**
	 * split paragraph to sentences by the sentence splitter of stanford
	 * dependency parser. Loading paragraph from file and writer split result to
	 * the file with suffix .sent
	 * 
	 * @param filename
	 *            path of the file to be processed
	 * @return the directory location of split result
	 */
	public static String SplitParagraph2Sentences(String filename) {
		File file = new File(filename);
		File outfile = new File(filename + ".sent");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile)));
			Properties props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			Annotation annotation;
			Pattern pattern = Pattern.compile("[a-zA-Z]");
			String line = "";
			int index = 0;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty())
					continue;
				annotation = new Annotation(line.toLowerCase());
				pipeline.annotate(annotation);
				List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
				if (sentences != null) {
					for (int i = 0, sz = sentences.size(); i < sz; i++) {
						CoreMap sentMap = sentences.get(i);
						String sentence = sentMap.toString().trim();
						Matcher matcher = pattern.matcher(sentence);
						if (matcher.find())
							bw.write(sentence.substring(matcher.start()).trim() + "\n");
					}
				}
				bw.flush();
				index++;
				if (index % 1000 == 0)
					System.out.println(index / 1000 + " thousand paragraphs have been processed.");
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getParent();
	}

	private static String DeleteRedundantPuncts(String str) {
		String string = str.replaceAll("\"", "").replaceAll(",+", ",").replaceAll("!{1,}", ".").replaceAll("_+", "_").replaceAll("\\?{1,}", ".")
				.replaceAll("\\.{1,}", ".");
		string = string.replaceAll("#{1,}", " ").replaceAll("'{2,}", "").replaceAll("\\*{1,}", " ").replaceAll("-{1,}", "-").replaceAll("\\+{1,}", "+")
				.replaceAll("``", "").replaceAll(" +", " ");
		string = string.replaceAll("\\<\\>", " ").replaceAll("\\(\\)", " ").replaceAll("\\[\\]", " ").replaceAll("\\{\\}", " ");
		string = string.trim();
		return string;
	}

	private static String UK2USSpell(String str, Map<String, String> uk2us) {
		String[] words = str.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (uk2us.containsKey(words[i]))
				words[i] = uk2us.get(words[i]);
			sb.append(words[i]).append(" ");
		}
		return sb.toString().trim();
	}

	private static String Abbr2Full(String str, Map<String, String> abbr2full) {
		String[] words = str.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (words[i].contains("'ll")) {
				sb.append(words[i].split("'")[0]).append(" will ");
				continue;
			} else if (words[i].contains("'d")) {
				sb.append(words[i].split("'")[0]).append(" would ");
				continue;
			} else if (words[i].contains("'re")) {
				sb.append(words[i].split("'")[0]).append(" are ");
				continue;
			} else if (words[i].contains("'ve")) {
				sb.append(words[i].split("'")[0]).append(" have ");
				continue;
			} else if (words[i].contains("'cause")) {
				sb.append(words[i].split("'")[0]).append(" becasue ");
			} else if (words[i].contains("'til")) {
				sb.append(words[i].split("'")[0]).append(" until ");
			} else if (words[i].contains("'till")) {
				sb.append(words[i].split("'")[0]).append(" still ");
			} else {
				if (abbr2full.containsKey(words[i])) {
					sb.append(abbr2full.get(words[i])).append(" ");
					continue;
				}
				sb.append(words[i]).append(" ");
			}
		}
		return sb.toString().replaceAll(" +", " ").trim();
	}

	private static Map<String, String> loadUk2usMap() {
		Map<String, String> uk2us = new HashMap<String, String>();
		File file = new File("files/UK-US");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] lines = line.split("::");
				uk2us.put(lines[0], lines[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return uk2us;
	}

	private static Map<String, String> loadAbbr2FullMap() {
		Map<String, String> abbr2full = new HashMap<String, String>();
		abbr2full.put("don't", "do not");
		abbr2full.put("don'ts", "do not");
		abbr2full.put("shouldn't", "should not");
		abbr2full.put("would't", "would not");
		abbr2full.put("wasn't", "was not");
		abbr2full.put("won't", "will not");
		abbr2full.put("mustn't", "must not");
		abbr2full.put("couldn't", "could not");
		abbr2full.put("can't", "can not");
		abbr2full.put("hasn't", "has not");
		abbr2full.put("doesnn't", "does not");
		abbr2full.put("wouldn't", "would not");
		abbr2full.put("isn't", "is not");
		abbr2full.put("hadn't", "had not");
		abbr2full.put("havn't", "have not");
		abbr2full.put("didn't", "did not");
		abbr2full.put("doesn't", "does not");
		abbr2full.put("aren't", "are not");
		abbr2full.put("weren't", "were not");
		abbr2full.put("haven't", "have not");
		abbr2full.put("warn't", "was not");
		abbr2full.put("i'm", "i am");
		abbr2full.put("who's", "who is");
		abbr2full.put("there's", "there is");
		abbr2full.put("that's", "that is");
		abbr2full.put("here's", "here is");
		abbr2full.put("where's", "where is");
		abbr2full.put("let's", "let us");
		abbr2full.put("she's", "she is");
		abbr2full.put("he's", "he is");
		abbr2full.put("it's", "it is");
		abbr2full.put("how's", "how is");
		abbr2full.put("which's", "which is");
		abbr2full.put("what's", "what is");
		return abbr2full;
	}
}
