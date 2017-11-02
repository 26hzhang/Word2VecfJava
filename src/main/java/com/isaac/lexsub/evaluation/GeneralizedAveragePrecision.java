package com.isaac.lexsub.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralizedAveragePrecision {
	
	public static float calc(Map<String, Integer> goldVector, Map<String, Float> evalVector, 
			boolean randomize) {
		List<Map.Entry<String, Integer>> sortedGoldVector = goldVector.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.collect(Collectors.toCollection(LinkedList::new));
		LinkedList<Tuple> goldVectorAccumulated = accumulateScore(sortedGoldVector);
		// first we use the eval score to sort the eval vector accordingly
		List<Map.Entry<String, Float>> sortedEvalVector;
		if (randomize) {
			sortedEvalVector = new LinkedList<>(evalVector.entrySet());
			Collections.shuffle(sortedEvalVector);
		} else {
			sortedEvalVector = evalVector.entrySet().stream()
					.sorted(Map.Entry.<String, Float>comparingByValue().reversed())
					.collect(Collectors.toCollection(LinkedList::new));
		}
		// now we replace the eval score with the gold score
		LinkedList<Tuple> sortedEvalVectorWithGoldScores = new LinkedList<>();
		for (Map.Entry<String, Float> entry : sortedEvalVector) {
			float goldScore = 0.0f;
			String key = entry.getKey();
			if (goldVector.containsKey(key))
				goldScore = (float) goldVector.get(key);
			sortedEvalVectorWithGoldScores.addLast(new Tuple(key, goldScore));
		}
		LinkedList<Tuple> evalVectorAccumulated = accumulateScore(sortedEvalVectorWithGoldScores);
		// this is sum of precisions over all recall points
		int i = 0;
		float nominator = 0.0f;
		for (Tuple tuple : evalVectorAccumulated) {
			i++;
			if (goldVector.containsKey(tuple.getKey()) && goldVector.get(tuple.getKey()) > 0)
				nominator += tuple.getValue() / i;
		}
		// this is the optimal sum of precisions possible based on the gold standard ranking
		i = 0;
		float denominator = 0.0f;
		for (Tuple tuple : goldVectorAccumulated) {
			if (goldVector.get(tuple.getKey()) > 0) {
				i++;
				denominator += tuple.getValue() / i;
			}
		}
		if (denominator == 0.0f)
			return -1.0f;
		else
			return nominator / denominator;
	}
	
	private static LinkedList<Tuple> accumulateScore(List<Map.Entry<String, Integer>> vector) {
		float accumulatedScore = 0.0f;
		LinkedList<Tuple> result = new LinkedList<>();
		for (Map.Entry<String, Integer> entry : vector) {
			accumulatedScore += (float) entry.getValue();
			result.addLast(new Tuple(entry.getKey(), accumulatedScore));
		}
		return result;
	}
	
	private static LinkedList<Tuple> accumulateScore(LinkedList<Tuple> vector) {
		float accumulatedScore = 0.0f;
		LinkedList<Tuple> result = new LinkedList<>();
		for (Tuple tuple : vector) {
			accumulatedScore += tuple.getValue();
			result.addLast(new Tuple(tuple.getKey(), accumulatedScore));
		}
		return result;
	}
	
	static class Tuple {
		String key;
		float value;
		
		public Tuple(String key, float value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public float getValue() {
			return value;
		}

		public void setValue(float value) {
			this.value = value;
		}
	}
}
