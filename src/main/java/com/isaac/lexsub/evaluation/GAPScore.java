package com.isaac.lexsub.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GAPScore {
	
	public static void evaluate(String goldFilename, String evalFilename, String outputFilename, String noMWE, 
			String random) {
		boolean ignoreMWE = false;
		boolean randomize = false;
		if (noMWE != null && noMWE.equals("no-mwe"))
			ignoreMWE = true;
		if (random != null && random.equals("random"))
			randomize = true;
		try {
			BufferedReader goldFile = new BufferedReader(new FileReader(goldFilename));
			BufferedReader evalFile = new BufferedReader(new FileReader(evalFilename));
			BufferedWriter outFile = new BufferedWriter(new FileWriter(outputFilename));
			Map<String, Map<String, Integer>> goldData = new HashMap<>();
			Map<String, Map<String, Float>> evalData = new HashMap<>();
			String line;
			while ((line = goldFile.readLine()) != null) {
				GoldData data = readGoldLine(line, ignoreMWE);
				goldData.put(data.getInstanceId(), data.getWeight());
			}
			while ((line = evalFile.readLine()) != null) {
				EvalData data = readEvalLine(line, ignoreMWE);
				evalData.put(data.getInstanceId(), data.getWeight());
			}
			goldFile.close();
			evalFile.close();
			int i = 0;
			int ignored = 0;
			float sumGap = 0.0f;
			for (Map.Entry<String, Map<String, Integer>> entry : goldData.entrySet()) {
				Map<String, Integer> goldWeights = entry.getValue();
				Map<String, Float> evalWeights = evalData.get(entry.getKey());
				float gap = GeneralizedAveragePrecision.calc(goldWeights, evalWeights, randomize);
				if (gap < 0.0) {
					ignored++;
					continue;
				}
				outFile.write(entry.getKey() + "\t" + String.valueOf(gap) + "\n");
				i++;
				sumGap += gap;
			}
			float meanGap = sumGap / i;
			outFile.write(String.format("Gold_Data: %d, Eval_Data: %d\n", goldData.size(), evalData.size()));
			outFile.write(String.format("Read %d test instances\n", i));
			outFile.write(String.format("Ignored %d test instances (couldn't compute gap)\n", ignored));
			outFile.write("MEAN_GAP: " + String.valueOf(meanGap) + "\n");
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished!!!");
	}
	
	// gold-line format -- take.v 25 :: consider 2;accept 1;include 1;think about 1;
	private static GoldData readGoldLine(String goldLine, boolean ignoreMWE) {
		String[] segments = goldLine.split("::");
		String instanceId = segments[0].trim();
		Map<String, Integer> goldWeights = new LinkedHashMap<>();
		String[] lineCandidates = segments[1].trim().split(";");
		for (String lineCandidate : lineCandidates) {
			if (lineCandidate.isEmpty())
				continue;
			int delimiterInd = lineCandidate.lastIndexOf(" ");
			String candidate = lineCandidate.substring(0, delimiterInd);
			if (ignoreMWE && (candidate.split(" ").length > 1 || candidate.split("-").length > 1))
				continue;
			String count = lineCandidate.substring(delimiterInd + 1);
			try {
				goldWeights.put(candidate, Integer.parseInt(count));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.err.println(goldLine);
				System.out.print(String.format("cand=%s, count=%s", candidate, count));
				System.exit(1);
			}
		}
		GoldData goldData = new GoldData(instanceId, goldWeights);
		return goldData;
	}
	
	// eval-line format -- RESULT    find.v 71    show 0.34657
	private static EvalData readEvalLine(String evalLine, boolean ignoreMWE) {
		String[] segments = evalLine.split("\t");
		String instanceId = segments[1].trim();
		Map<String, Float> evalWeights = new LinkedHashMap<>();
		for (int i = 2; i < segments.length; i++) {
			String lineCandidate = segments[i].trim();
			if (lineCandidate.isEmpty())
				continue;
			int delimiterInd = lineCandidate.lastIndexOf(" ");
			String candidate = lineCandidate.substring(0, delimiterInd);
			if (ignoreMWE && (candidate.split(" ").length > 1 || candidate.split("-").length > 1))
				continue;
			String count = lineCandidate.substring(delimiterInd + 1);
			try {
				evalWeights.put(candidate, Float.parseFloat(count));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.err.println(String.format("Error appending: %s %s", candidate, count));
			}
		}
		EvalData evalData = new EvalData(instanceId, evalWeights);
		return evalData;
	}
	
	// gold-data structure
	static class GoldData {
		String instanceId;
		Map<String, Integer> weight;
		
		public GoldData(String instanceId, Map<String, Integer> weight) {
			this.instanceId = instanceId;
			this.weight = weight;
		}

		public String getInstanceId() {
			return instanceId;
		}

		public void setInstanceId(String instanceId) {
			this.instanceId = instanceId;
		}

		public Map<String, Integer> getWeight() {
			return weight;
		}

		public void setWeight(Map<String, Integer> weight) {
			this.weight = weight;
		}
	}

	// eval-data structure
	static class EvalData {
		String instanceId;
		Map<String, Float> weight;
		
		public EvalData(String instanceId, Map<String, Float> weight) {
			this.instanceId = instanceId;
			this.weight = weight;
		}

		public String getInstanceId() {
			return instanceId;
		}

		public void setInstanceId(String instanceId) {
			this.instanceId = instanceId;
		}

		public Map<String, Float> getWeight() {
			return weight;
		}

		public void setWeight(Map<String, Float> weight) {
			this.weight = weight;
		}
	}
}
