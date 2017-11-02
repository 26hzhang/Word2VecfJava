package com.isaac.examples;

import com.isaac.lexsub.LexSubConfigs;
import com.isaac.lexsub.LexicalSubstitute;
import com.isaac.lexsub.evaluation.GAPScore;

public class LexicalSubstituteExample {
    public static void main (String[] args) {
        String wordEmbPath = "<Your Embedding Path>";
        String contextEmbPath = "<Your Embedding Path>";
        // perform the lexical substitution evaluation
        LexSubConfigs config = new LexSubConfigs()
                .setInferrer("emb")
                .setVocabFile("src/main/resources/datasets/ukwac.vocab.lower.min100")
                .setTestFile("src/main/resources/datasets/lst_all.preprocessed.lemma")
                .setTestFileConll("src/main/resources/datasets/lst_all.conll.lemma")
                .setCandidatesFile("src/main/resources/datasets/lst.gold.candidates.lemma")
                .setEmbeddingPath(wordEmbPath)
                .setEmbeddingPathC(contextEmbPath)
                .setContextMath("mult")
                .setDebug(true)
                .setResultsFile("src/main/resources/results");
        LexicalSubstitute.run(config);
        // compute the candidate ranking GAP score
        GAPScore.evaluate("src/main/resources/datasets/lst_all.gold.lemma",
                "src/main/resources/results.ranked",
                "src/main/resources/gapscore", "no-mwe", null);
        // compute the OOT and BEST substitute prediction scores -- TODO
    }
}
