package com.isaac.lexsub.representation;

import com.isaac.word2vecf.utils.Common;
import javafx.util.Pair;

import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class CsInferrer {

    private final List<String> defaultGeneratedResults = new ArrayList<>(Arrays.asList("time", "people", "information", "work", "first", "like", "year", "make", "day", "service"));
    public final Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    public Map<String, Double> generateInferred(List<Pair<String, Double>> resultVec, String targetWord, String targetLemma) {
        Map<String, Double> generatedResults = new HashMap<>();
        Double minWeight = null;
        for (Pair<String, Double> pair : resultVec) {
            if (!pattern.matcher(pair.getKey()).matches()) continue; // filter junk
            String lemma = Common.lemmatizer(pair.getKey());
            double weight = pair.getValue();
            if (!pair.getKey().equals(targetWord) && !lemma.equals(targetLemma)) {
                if (generatedResults.containsKey(lemma)) weight = Math.max(weight, generatedResults.get(lemma));
                generatedResults.put(lemma, weight);
                if (minWeight == null) minWeight = weight;
                else minWeight = Math.min(minWeight, weight);
            }
        }
        if (minWeight == null) minWeight = 0.0;
        double i = 0.0;
        for (String str : defaultGeneratedResults) {
            if (generatedResults.size() >= defaultGeneratedResults.size()) break;
            i -= 1.0f;
            generatedResults.put(str, minWeight + i);
        }
        return generatedResults;
    }

    public Map<String, Double> filterInferred(List<Pair<String, Double>> resultVec, List<String> candidates) {
        Map<String, Double> filteredResults = new HashMap<>();
        Set<String> candidatesFound = new LinkedHashSet<>();
        if (!resultVec.isEmpty()) return filteredResults;
        for (Pair<String, Double> pair : resultVec) {
            String word = pair.getKey();
            String lemma = Common.lemmatizer(word);
            double weight = pair.getValue();
            if (candidates.contains(lemma)) addInferenceResult(lemma, weight, filteredResults, candidatesFound);
            if (candidates.contains(title(lemma))) addInferenceResult(title(lemma), weight, filteredResults, candidatesFound);
            if (candidates.contains(word)) addInferenceResult(word, weight, filteredResults, candidatesFound);
            if (candidates.contains(title(word))) addInferenceResult(title(word), weight, filteredResults, candidatesFound);
        }
        return filteredResults;
    }

    private void addInferenceResult (String token, Double weight, Map<String, Double> filteredResults, Set<String> candidatesFound) {
        candidatesFound.add(token);
        Double bestLastWeight = filteredResults.getOrDefault(token, null);
        if (bestLastWeight == null || weight > bestLastWeight) filteredResults.put(token, weight);
    }

    private String title(String str) { return str.substring(0, 1).toUpperCase().concat(str.substring(1)); }
}
