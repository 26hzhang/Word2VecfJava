package com.isaac.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoNLLUOutputter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ConllFileGenerator {

	/**
	 * Train the CONLL file from original text file
	 * 
	 * @param filename
	 *            path of the file to be processed
	 * @return the directory path of trained file and CONLL file
	 */
	public static String generate(String filename) {
		// Input File Reader
		File file = new File(filename);
		// Create output file
		File outFile = new File(filename + ".conll");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, true)));
			// Set properties
			Properties props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
			/**
			 * default is universal dependency (english_UD.gz), here we choose
			 * stanford dependency (english_SD.gz)
			 */
			props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz");

			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			Annotation annotation;
			String line;
			int index = 0;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty())
					continue;
				annotation = new Annotation(line);
				pipeline.annotate(annotation);
				CoNLLUOutputter conlluOutput = new CoNLLUOutputter();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				conlluOutput.print(annotation, bos, pipeline);
				String result = new String(bos.toByteArray(), pipeline.getEncoding());
				result = deleteErrorResult(result); // filter the error result
				if (result != null) {
					bw.write(result);
					bw.flush();
				}
				index++;
				if (index % 1000000 == 0)
					System.out.println(String.format("%d million lines data have been processed!", index / 1000000));
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getParent();
	}

	private static String deleteErrorResult(String conllOutputter) {
		String result = "";
		List<String> list = new ArrayList<>(Arrays.asList(conllOutputter.split("\n")));
		List<String> temp = new ArrayList<>();
		boolean judge = true;
		for (String line : list) {
			temp.add(line);
			if (line.isEmpty()) {
				if (judge) {
					for (String s : temp) {
						result += s + "\n";
					}
				}
				temp.clear();
				judge = true;
			} else {
				String[] tokens = line.split("\t");
				if (tokens.length != 10 || !isNumeric(tokens[0]) || !isNumeric(tokens[6])) {
					judge = false;
				}
			}
		}
		if (judge && !temp.isEmpty()) {
			for (String s : temp) {
				result += s + "\n";
			}
		}
		return result;
	}
	
	private static boolean isNumeric(String str) {
		// Judge whether the component is a number
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
}
