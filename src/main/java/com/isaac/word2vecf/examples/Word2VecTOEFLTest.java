package com.isaac.word2vecf.examples;

import com.isaac.word2vecf.models.ModelSerializer;
import com.isaac.word2vecf.models.Word2Vec;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by zhanghao on 20/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Word2VecTOEFLTest {

	private static Logger log = LoggerFactory.getLogger(Word2VecTOEFLTest.class);

	public static void main (String[] args) {
		Word2Vec w2v = ModelSerializer.loadWord2VecModel("/home/zhanghao/Documents/GoogleNews-vectors-negative300.bin", true);
		// TOEFL test
		measure(w2v);
	}

	public static void measure(Word2Vec w2v) {
		log.info("load TOEFL data...");
		List<TFLNode> list = new Word2VecTOEFLTest().loadTOEFLData();
		log.info("run the test...");
		int accuracy = 0;
		int ignore = 0;
		for (int i = 0; i < list.size(); i++) {
			TFLNode node = list.get(i);
			int bestId = -1;
			double cosValue = Double.MIN_VALUE;
			for (int k = 0; k < node.choices.length; k++) {
				double cosSim = w2v.wordSimilarity(node.ques, node.choices[k]);
				if (cosSim > cosValue) {
					bestId = k;
					cosValue = cosSim;
				}
			}
			list.get(i).setPredict(bestId);
			log.info((i + 1) + "--" + list.get(i).toString() + "\n");
			if (list.get(i).predict == -1)
				ignore++;
			if (list.get(i).ans == list.get(i).predict)
				accuracy += 1;
		}
		log.info("Total Questions: " + list.size() + ", Ignore: " + ignore + ", Accuracy: " + String.format("%.2f", (1.0 * accuracy) / list.size() * 100.0) +
				"%(" + accuracy + "/" + list.size() + ")");
	}

	private List<TFLNode> loadTOEFLData() {
		List<TFLNode> list = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new ClassPathResource("data/toefl.txt").getFile())));
			String line;
			String ques = "";
			String[] choices = new String[4];
			int ans;
			while ((line = reader.readLine()) != null) {
				if (line.split("\\.\t").length < 2) {
					ans = line.charAt(0) - 'a';
					TFLNode node = new TFLNode(ques, choices, ans);
					list.add(node);
					choices = new String[4];
					ques = "";
				} else {
					String w1 = line.split("\\.\t")[0];
					String w2 = line.split("\\.\t")[1];
					if (Pattern.compile("[0-9]*").matcher(w1).matches()) ques = w2;
					else choices[w1.charAt(0) - 'a'] = w2;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	class TFLNode {
		private String ques;
		private String[] choices = new String[4];
		private int ans;
		private int predict;

		private TFLNode (String ques, String[] choices, int ans) {
			this.ques = ques;
			this.choices = choices.clone();
			this.ans = ans;
		}

		private void setPredict (int predict) {
			this.predict = predict;
		}

		@Override
		public String toString () {
			return "Questions: ".concat(ques).concat("\nChoices: ").concat(Arrays.asList(choices).toString()).concat("\nAnswer: ").concat(choices[ans])
					.concat("\nPredict: ").concat(predict == -1 ? "N/A" : choices[predict]);
		}
	}
}
