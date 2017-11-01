package com.isaac.examples;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.isaac.examples.Word2VecAnalogyTest.loadSynSemData;
import static com.isaac.examples.Word2VecWS353Test.loadWS353Data;
import static com.isaac.examples.Word2VecWS353Test.pearson;
import static com.isaac.examples.Word2VecTOEFLTest.loadTOEFLData;

public class DL4JWord2VecSemanticExample {

    private static Logger log = LoggerFactory.getLogger(DL4JWord2VecSemanticExample.class);

    public static void main (String[] args) throws FileNotFoundException {
        // download GoogleNews-vectors-negative300.bin.gz first
        // load google news vectors for measurements
        log.info("load word2vec model");
        Word2Vec w2v = WordVectorSerializer.readWord2VecModel(
                new File("/Users/zhanghao/Documents/Files/GoogleNews-vectors-negative300.bin"));
        log.info("done.");

        log.info("Semantic Property Task...");
        // 1. TOEFL test
        log.info("|********************load TOEFL data********************|");
        List<Word2VecTOEFLTest.TFLNode> tflList = loadTOEFLData();
        log.info("run the test");
        TOEFLTest(tflList, w2v);
        log.info("|*************************done.*************************|");

        // 2. Analogy test -- "king - queen = man - woman"
        log.info("|*******************load Syn_Sem data*******************|");
        Map<String, List<Word2VecAnalogyTest.SynSemNode>> anaMap = loadSynSemData();
        log.info("run the test");
        AnalogyTest(anaMap, w2v);
        log.info("|*************************done.*************************|");

        // 3. WS353 test
        log.info("|********************load WS353 data********************|");
        LinkedList<Word2VecWS353Test.WS353Node> wsList = loadWS353Data("ws/ws353.txt");
        LinkedList<Word2VecWS353Test.WS353Node> wsListRel = loadWS353Data("ws/ws353_relatedness.txt");
        LinkedList<Word2VecWS353Test.WS353Node> wsListSim = loadWS353Data("ws/ws353_similarity.txt");
        log.info("done.");
        log.info("run the test");
        WS353Test(w2v, wsList, "WS353");
        WS353Test(w2v, wsListRel, "WS353 Relatedness");
        WS353Test(w2v, wsListSim, "WS353 Similarity");
        log.info("|*************************done.*************************|");
    }

    private static void TOEFLTest(List<Word2VecTOEFLTest.TFLNode> tflList, Word2Vec w2v) {
        int accuracy = 0;
        int ignore = 0;
        for (int i = 0; i < tflList.size(); i++) {
            Word2VecTOEFLTest.TFLNode node = tflList.get(i);
            int bestId = -1;
            double cosValue = Double.MIN_VALUE;
            for (int k = 0; k < node.choices.length; k++) {
                double cosSim = w2v.similarity(node.ques, node.choices[k]);
                if (cosSim > cosValue) {
                    bestId = k;
                    cosValue = cosSim;
                }
            }
            tflList.get(i).setPredict(bestId);
            log.info((i + 1) + "--" + tflList.get(i).toFileString() + "\n");
            if (tflList.get(i).predict == -1)
                ignore++;
            if (tflList.get(i).ans == tflList.get(i).predict)
                accuracy += 1;
        }
        log.info("Total Questions: " + tflList.size() + ", Ignore: " + ignore + ", Accuracy: " +
                String.format("%.2f", (1.0 * accuracy) / tflList.size() * 100.0) + "%(" + accuracy + "/" +
                tflList.size() + ")");
    }

    private static void AnalogyTest(Map<String, List<Word2VecAnalogyTest.SynSemNode>> anaMap, Word2Vec w2v) {
        for (Map.Entry<String, List<Word2VecAnalogyTest.SynSemNode>> entry : anaMap.entrySet()) {
            log.info(entry.getKey());
            List<Word2VecAnalogyTest.SynSemNode> list = entry.getValue();
            int total = 0;
            int pass = 0;
            int ignore = 0;
            for (Word2VecAnalogyTest.SynSemNode node : list) {
                if (w2v.hasWord(node.str1) && w2v.hasWord(node.str2) && w2v.hasWord(node.str3)) {
                    total++;
                    String answer = w2v.wordsNearest(Arrays.asList(node.str2, node.str3),
                            Collections.singletonList(node.str1), 1).iterator().next();
                    // convert answer and gold result to same form to eliminate errors
                    answer = answer.replaceAll("[^A-Za-z]", "");
                    if (answer.length() < 1) {
                        ignore++;
                        continue;
                    }
                    answer = answer.toLowerCase();
                    answer = answer.substring(0, 1).toUpperCase() + answer.substring(1);
                    String nodeStr4 = node.str4.substring(0, 1).toUpperCase() + node.str4.substring(1);
                    if (answer.equals(nodeStr4))
                        pass++;
                    //log.info(node.toString() + "\t" + answer + "\t" + answer.equals(nodeStr4));
                } else {
                    ignore++;
                }
            }
            log.info("Total Questions: " + list.size() + ", Ignore: " + ignore + ", Accuracy: " +
                    String.format("%.2f", (1.0 * pass) / total * 100.0) + "%(" + pass + "/" + total + ")");
        }
    }

    private static void WS353Test(Word2Vec w2v, LinkedList<Word2VecWS353Test.WS353Node> list, String name) {
        int size = list.size();
        double[] values = new double[size];
        double[] cosValues = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = list.get(i).value;
            cosValues[i] = w2v.similarity(list.get(i).word1, list.get(i).word2);
        }
        // pearson
        double res = pearson(values, cosValues);
        log.info(name + ": " + String.format("%.2f", res * 100) + "%");
    }
}
