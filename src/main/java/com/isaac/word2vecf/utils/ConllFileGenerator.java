package com.isaac.word2vecf.utils;

import com.google.common.base.Preconditions;
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
				if (line.isEmpty()) continue;
				annotation = new Annotation(line); // create annotation
				pipeline.annotate(annotation); // add annotation to pipeline
				CoNLLUOutputter outputter = new CoNLLUOutputter();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				// performs CoNLLU generator on annotation and output to ByteArrayOutputStream
				outputter.print(annotation, bos, pipeline);
				// validate format, drop sentences with error format
				String result = dropError(new String(bos.toByteArray(), pipeline.getEncoding()));
				if (result != null && !result.isEmpty()) writer.write(result);
				writer.flush();
				index++;
				if (index % 1000000 == 0) log.info(String.format("%d million lines data have been processed!", index / 1000000));
			}
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** @return cleaned CoNLLU format {@link String} */
	/*CoNLLU format sample
	For a sentence:
		Kosgi Santosh sent an email to Stanford University. He didn't get a reply.
	CoNLLU Output:
		1	Kosgi	Kosgi	_	NNP	_	2	compound	_	_
		2	Santosh	Santosh	_	NNP	_	3	nsubj	_	_
		3	sent	send	_	VBD	_	0	root	_	_
		4	an	a	_	DT	_	5	det	_	_
		5	email	email	_	NN	_	3	dobj	_	_
		6	to	to	_ TO	_	8	case	_	_
		7	Stanford	Stanford	_	NNP	_	8	compound	_	_
		8	University	University	_	NNP	_	3	nmod	_	_
		9	.	.	_	.	_	3	punct	_	_
		(a blank line)
		1	He	he	_	PRP	_	4	subj	_	_
		2	did	do	_	VBD	_	4	aux	_	_
		3	n't	not	_	RB	_	4	neh	_	_
		4	get	get	_	VB	_	0	root	_	_
		5	a	a	_	DT	_	6	det	_	_
		6	reply	reply	_	NN	_	4	dobj	_	_
		7	.	.	_	.	_	4	punct	_	_
	 */
	private static String dropError(String str) {
		if (str == null || str.isEmpty()) return null;
		String result = "";
		List<String> list = new ArrayList<>(Arrays.asList(str.split("\n")));
		List<String> temp = new ArrayList<>();
		boolean flag = true;
		for (String line : list) {
			temp.add(line);
			if (line.isEmpty()) {
				if (flag) for (String s : temp) result = result.concat(s).concat("\n");
				temp.clear();
				flag = true;
			} else {
				String[] tokens = line.split("\t");
				if (tokens.length != 10 || !Common.isNumeric(tokens[0]) || !Common.isNumeric(tokens[6])) flag = false;
			}
		}
		if (flag && !temp.isEmpty()) for (String s : temp) result = result.concat(s).concat("\n");
		return result;
	}
}
