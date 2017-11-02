package com.isaac.lexsub.utils;

import com.isaac.lexsub.representation.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Common {

    public static String vec2Str(List<Map.Entry<String, Double>> resultVec, int maxNum) {
        List<String> subStrs = resultVec.stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(maxNum)
                .map((Map.Entry<String, Double> e) -> (e.getKey() + " " + String.format("%.5f", e.getValue())))
                .collect(Collectors.toCollection(LinkedList::new));
        return String.join("\t", subStrs);
    }

    public static String vec2StrGenerated(List<Map.Entry<String, Double>> resultVec, int maxNum) {
        List<String> subStrs = resultVec.stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(maxNum)
                .map(Map.Entry::getKey).collect(Collectors.toCollection(LinkedList::new));
        return String.join(";", subStrs);
    }

    public static List<String> getDependencies(List<Token> sent, int targetInd) {
        List<String> deps = new ArrayList<>();
        for (Token wordLine : sent) {
            Token parentLine = sent.get(wordLine.getHead());
            if (wordLine.getDeptype().equals("preps")) continue;
            String relation;
            String head;
            if (wordLine.getDeptype().equals("pobj") && parentLine.getId() != 0) {
                Token grandparentLine = sent.get(parentLine.getHead());
                if (grandparentLine.getId() != targetInd && wordLine.getId() != targetInd) continue;
                relation = String.join(":", parentLine.getDeptype(), parentLine.getForm());
                head = grandparentLine.getForm();
            } else {
                if (parentLine.getId() != targetInd && wordLine.getId() != targetInd) continue;
                head = parentLine.getForm();
                relation = wordLine.getDeptype();
            }
            if (wordLine.getId() == targetInd) deps.add(String.join("I_", relation, head)); // if head not in stopWords: --> do not implement stop words
            else deps.add(String.join("_", relation, wordLine.getForm())); // if word_line.form not in stopWords: --> do not implement stop words
        }
        return deps;
    }

    public static Map<String, Integer> loadVocab(String vocabFilePath, double factor) {
        if (factor < 0) factor = 1.0;
        Map<String, Integer> counts = new LinkedHashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(vocabFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] tokens = line.trim().split("\t");
                String word = tokens[0].trim();
                int count = Integer.parseInt(tokens[1].trim());
                if (factor != 1.0) count = (int) (count * factor);
                counts.put(word, count);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return counts;
    }

    public static List<List<Token>> readConll(String conllFilePath, boolean lower) {
        List<List<Token>> sents = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(conllFilePath));
            List<Token> sent = new ArrayList<>();
            sent.add(new Token());
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    if (lower) line = line.toLowerCase();
                    String[] words = line.split("\t");
                    Token token = new Token(words);
                    sent.add(token);
                } else {
                    if (sent.size() > 1) sents.add(sent);
                    sent = new ArrayList<>();
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sents;
    }

    public static Map<String, List<String>> readCandidates(String candidatesFilePath) {
        Map<String, List<String>> candidatesMap = new LinkedHashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(candidatesFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] segments = line.split("::");
                String target = segments[0];
                List<String> candidates = new ArrayList<>(Arrays.asList(segments[1].split(";")));
                candidatesMap.put(target, candidates);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return candidatesMap;
    }
}
