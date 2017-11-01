package com.isaac.examples;

import com.isaac.word2vecf.utils.WordVectorSerializer;
import com.isaac.word2vecf.Word2Vec;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by zhanghao on 20/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Word2VecWS353Test {
	private static Logger log = LoggerFactory.getLogger(Word2VecWS353Test.class);

	public static void main (String[] args) {
		Word2Vec w2v = WordVectorSerializer.loadWord2VecModel("/home/zhanghao/Documents/GoogleNews-vectors-negative300.bin", true);
		// WS353 test
		measure(w2v);
	}

	private static void measure(Word2Vec w2v) {
		log.info("load WS353 data...");
		LinkedList<WS353Node> wsList = loadWS353Data("data/ws353.txt");
		LinkedList<WS353Node> wsListRel = loadWS353Data("data/ws353_relatedness.txt");
		LinkedList<WS353Node> wsListSim = loadWS353Data("data/ws353_similarity.txt");
		log.info("run the test...");
		run(w2v, wsList, "WS353");
		run(w2v, wsListRel, "WS353 Relatedness");
		run(w2v, wsListSim, "WS353 Similarity");
		log.info("done.");
	}

	private static void run(Word2Vec w2v, LinkedList<WS353Node> list, String name) {
		int size = list.size();
		double[] values = new double[size];
		double[] cosValues = new double[size];
		for (int i = 0; i < size; i++) {
			values[i] = list.get(i).value;
			cosValues[i] = w2v.wordSimilarity(list.get(i).word1, list.get(i).word2);
		}
		// pearson
		double res = pearson(values, cosValues);
		log.info(name + ": " + String.format("%.2f", res * 100) + "%");
	}

	static double pearson(double[] values, double[] cosValues) {
		int size = values.length;
		double eps = 1e-8;
		double avg_val = 0;
		double avg_cosVal = 0;
		for (int i = 0; i < size; i++) {
			avg_val += values[i];
			avg_cosVal = cosValues[i];
		}
		avg_val /= size;
		avg_cosVal /= size;
		double v1 = 0, v2 = 0, v3 = 0;
		for (int i = 0; i < size; i++) {
			v1 += (values[i] - avg_val) * (cosValues[i] - avg_cosVal);
			v2 += (values[i] - avg_val) * (values[i] - avg_val);
			v3 += (cosValues[i] - avg_cosVal) * (cosValues[i] - avg_cosVal);
		}
		return v1 / (Math.sqrt(v2 + eps) * Math.sqrt(v3 + eps));
	}

	public static LinkedList<WS353Node> loadWS353Data (String filePath) {
		LinkedList<WS353Node> list = new LinkedList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new ClassPathResource(filePath).getFile())));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] segments = line.trim().split("\t");
				WS353Node node = new WS353Node(segments[0], segments[1], Double.parseDouble(segments[2]));
				list.addLast(node);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	static class WS353Node {
		String word1;
		String word2;
		double value;

		private WS353Node (String word1, String word2, double value) {
			this.word1 = word1;
			this.word2 = word2;
			this.value = value;
		}
	}
}
