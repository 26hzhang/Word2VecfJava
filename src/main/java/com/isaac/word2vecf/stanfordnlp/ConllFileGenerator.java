package com.isaac.word2vecf.stanfordnlp;

import com.google.common.base.Preconditions;
import com.isaac.word2vecf.utils.Common;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoNLLUOutputter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by zhanghao on 19/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class ConllFileGenerator {

	private static Logger log = LoggerFactory.getLogger(ConllFileGenerator.class);

	public static void generate (String filename) {
		Preconditions.checkArgument(filename != null && !filename.isEmpty(), "raw corpus file path must be assigned");
		System.gc(); // performs garbage collection
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename.concat(".conll")));
			/* Set properties */
			Properties props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
			/* default is universal dependency (english_UD.gz), here we choose stanford dependency (english_SD.gz) */
			props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			Annotation annotation;
			String line;
			int index = 0;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				annotation = new Annotation(line);
				pipeline.annotate(annotation);
				CoNLLUOutputter outputter = new CoNLLUOutputter();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				outputter.print(annotation, bos, pipeline);
				String result = dropError(new String(bos.toByteArray(), pipeline.getEncoding()));
				if (result != null && !result.isEmpty())
					writer.write(result);
				writer.flush();
				index++;
				if (index % 1000000 == 0)
					log.info(String.format("%d million lines data have been processed!", index / 1000000));
			}
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String dropError(String str) {
		if (str == null || str.isEmpty())
			return null;
		String result = "";
		List<String> list = new ArrayList<>(Arrays.asList(str.split("\n")));
		List<String> temp = new ArrayList<>();
		boolean flag = true;
		for (String line : list) {
			temp.add(line);
			if (line.isEmpty()) {
				if (flag) { for (String s : temp) { result += s + "\n"; } }
				temp.clear();
				flag = true;
			} else {
				String[] tokens = line.split("\t");
				if (tokens.length != 10 || !Common.isNumeric(tokens[0]) || !Common.isNumeric(tokens[6])) flag = false;
			}
		}
		if (flag && !temp.isEmpty()) {
			for (String s : temp) { result += s + "\n"; }
		}
		return result;
	}
}
