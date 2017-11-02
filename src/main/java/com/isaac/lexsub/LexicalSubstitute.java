package com.isaac.lexsub;

import com.isaac.lexsub.representation.ContextInstance;
import com.isaac.lexsub.representation.CsEmbeddingInferrer;
import com.isaac.lexsub.utils.Common;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexicalSubstitute {

    public static void run(LexSubConfigs config) {
        switch (config.inferrer) {
            case "emb":
                CsEmbeddingInferrer inferrer = new CsEmbeddingInferrer(config.vocabFile, config.ignoreTarget, config.contextMath, config.embeddingPath, config.embeddingPathC, config.testFileConll, config.bow, config.topGenerated);
                System.out.println("Using CsEmbeddingInferrer");
                runTestByCsEmbeddingInferrer(inferrer, config);
                break;
            case "lstm":
                // TODO -- Context2VecInferrer
                System.out.println("Using Context2VecInferrer");
                break;
            default:
                throw new IllegalArgumentException("Unknown inferrer type: " + config.inferrer);
        }
        System.out.println("Finished!!!");
    }

    private static void runTestByCsEmbeddingInferrer(CsEmbeddingInferrer inferrer, LexSubConfigs config) {
        Map<String, List<String>> target2Candidates = new HashMap<>();
        if (config.candidatesFile != null) target2Candidates = Common.readCandidates(config.candidatesFile);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(config.testFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(config.resultsFile));
            BufferedWriter writerRanked = new BufferedWriter(new FileWriter(config.resultsFile + ".ranked"));
            BufferedWriter writerGeneratedOot = new BufferedWriter(new FileWriter(config.resultsFile + ".generated.oot"));
            BufferedWriter writerGeneratedBest = new BufferedWriter(new FileWriter(config.resultsFile + ".generated.best"));
            int lines = 0;
            String contextLine;
            while ((contextLine = reader.readLine()) != null) {
                ContextInstance lstInstance = new ContextInstance(contextLine, config.noPos);
                lines++;
                if (config.debug) writer.write("\nTest context:\n***************\n");
                writer.write(lstInstance.decorateContext());
                List<Pair<String, Double>> resultVec = inferrer.findInferred(lstInstance);
                Map<String, Double> generatedResults = inferrer.generateInferred(resultVec, lstInstance.getTarget(), lstInstance.getTargetLemma());
                writer.write("\nGenerated lemmatized results\n***************\n");
                writer.write("GENERATED\t" + String.join(" ", lstInstance.getFullTargetKey(), String.valueOf(lstInstance.getTargetId())) + " ::: " + Common.vec2StrGenerated(new ArrayList<>(generatedResults.entrySet()), config.topGenerated) + "\n");
                writerGeneratedOot.write(String.join(" ", lstInstance.getFullTargetKey(), String.valueOf(lstInstance.getTargetId())) + " ::: " + Common.vec2StrGenerated(new ArrayList<>(generatedResults.entrySet()), config.topGenerated) + "\n");
                writerGeneratedBest.write(String.join(" ", lstInstance.getFullTargetKey(), String.valueOf(lstInstance.getTargetId())) + " ::: " + Common.vec2StrGenerated(new ArrayList<>(generatedResults.entrySet()), 1) + "\n");
                Map<String, Double> filteredResults = inferrer.filterInferred(resultVec, target2Candidates.get(lstInstance.getTargetKey()));
                writer.write("\nFiltered results\n***************\n");
                writer.write("RANKED\t" + String.join(" ", lstInstance.getFullTargetKey(), String.valueOf(lstInstance.getTargetId())) + "\t" + Common.vec2Str(new ArrayList<>(filteredResults.entrySet()), filteredResults.size()) + "\n");
                writerRanked.write("RANKED\t" + String.join(" ", lstInstance.getFullTargetKey(), String.valueOf(lstInstance.getTargetId())) + "\t" + Common.vec2Str(new ArrayList<>(filteredResults.entrySet()), filteredResults.size()) + "\n");
                if (lines % 10 == 0) System.out.println("read " + lines + " lines!");
            }
            reader.close();
            writer.close();
            writerRanked.close();
            writerGeneratedOot.close();
            writerGeneratedBest.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
