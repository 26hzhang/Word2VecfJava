package com.isaac.lexsub.representation;

import com.isaac.lexsub.utils.Common;
import com.isaac.word2vecf.Word2Vecf;
import com.isaac.word2vecf.utils.WordVectorSerializer;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CsEmbeddingInferrer extends CsInferrer {

    private String contextMath;
    private Word2Vecf word2VecfModel;
    private boolean useStopWords = false;
    private boolean ignoreTarget;
    private int windowSize;
    private List<List<Token>> sents;
    private int topInferences2Analyze;
    private Map<String, Integer> w2Counts;
    private Iterator<List<Token>> iter;

    public CsEmbeddingInferrer(String vocabFile, boolean ignoreTarget, String contextMath, String wordPath, String contextPath, String conllFilename, int windowSize, int topInferences2Analyze) {
        super();
        this.contextMath = contextMath;
        this.ignoreTarget = ignoreTarget;
        this.windowSize = windowSize;
        this.topInferences2Analyze = topInferences2Analyze;
        this.word2VecfModel = WordVectorSerializer.loadWord2VecfModel(wordPath, contextPath, true);
        this.w2Counts = Common.loadVocab(vocabFile, -1.0);
        this.sents = Common.readConll(conllFilename, true);
        this.iter = sents.iterator();
    }

    public List<String> extractContexts(ContextInstance lstInstance) {
        List<String> contexts;
        if (this.windowSize < 0) {
            List<Token> curSent = iter.next();
            int curSentTargetInd = lstInstance.getTargetInd() + 1;
            while (curSentTargetInd < curSent.size() && !curSent.get(curSentTargetInd).getForm().equals(lstInstance.getTarget())) {
                System.out.println("Target word form mismatch in target id " + lstInstance.getTargetId() + ": " + curSent.get(curSentTargetInd).getForm() + " != " + lstInstance.getTarget() + " Checking next word.\n");
                curSentTargetInd++;
            }
            if (curSentTargetInd == curSent.size()) {
                System.out.println("Start looking backwards.\n");
                curSentTargetInd = lstInstance.getTargetInd();
                while (curSentTargetInd > 0 && !curSent.get(curSentTargetInd).getForm().equals(lstInstance.getTarget())) {
                    System.out.println("Target word form mismatch in target id " + lstInstance.getTargetId() + ": " + curSent.get(curSentTargetInd).getForm() + " != " + lstInstance.getTarget() + " Checking next word.\n");
                    curSentTargetInd--;
                }
            }
            if (curSentTargetInd == 0) {
                System.out.println("ERROR: Couldn't find a match for target.");
                curSentTargetInd = lstInstance.getTargetInd() + 1;;
            }
            contexts = Common.getDependencies(curSent, curSentTargetInd);
        } else {
            contexts = lstInstance.getNeighbors(this.windowSize);
        }
        return contexts;
    }

    public List<Pair<String, Double>> findInferred(ContextInstance lstInstance) {
        List<String> tmpContexts = extractContexts(lstInstance);
        List<String> contexts = new ArrayList<>();
        for (String context : tmpContexts) { if (word2VecfModel.hasContext(context)) contexts.add(context); }
        String target;
        if (this.ignoreTarget) target = null;
        else {
            if (word2VecfModel.hasWord(lstInstance.getTarget())) target = lstInstance.getTarget();
            else {
                if (word2VecfModel.hasWord(lstInstance.getTargetLemma())) target = lstInstance.getTargetLemma();
                else return null;
            }
        }
        List<Pair<String, Double>> resultVec;
        if (this.contextMath.equals("add")) resultVec = word2VecfModel.lexicalSubstituteAdd(target, contexts, false, word2VecfModel.wordVocabSize());
        else if (this.contextMath.equals("avg")) resultVec = word2VecfModel.lexicalSubstituteAdd(target, contexts, true, word2VecfModel.wordVocabSize());
        else if (this.contextMath.equals("mult")) resultVec = word2VecfModel.lexicalSubstituteMult(target, contexts, false, word2VecfModel.wordVocabSize());
        else if (this.contextMath.equals("geomean")) resultVec = word2VecfModel.lexicalSubstituteMult(target, contexts, true, word2VecfModel.wordVocabSize());
        else if (this.contextMath.equals("none") && !this.ignoreTarget) resultVec = word2VecfModel.wordsNearest(target, word2VecfModel.wordVocabSize());
        else throw new IllegalArgumentException("Unknown context math:".concat(this.contextMath));
        if (resultVec.isEmpty()) System.out.println("Top most similar embeddings: None\n");
        else System.out.println("Top most similar embeddings: ".concat(vec2Str(resultVec, this.topInferences2Analyze)).concat("\n"));
        return resultVec;
    }

    private static String vec2Str(List<Pair<String, Double>> resultVec, int maxNum) {
		List<String> results = resultVec.stream()
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(maxNum)
				.map(pair -> pair.getKey().concat(" ").concat(String.format("%.5f", pair.getValue())))
				.collect(Collectors.toCollection(LinkedList::new));
		return String.join("\t", results);
	}

    public String getContextMath() { return contextMath; }
    public void setContextMath(String contextMath) { this.contextMath = contextMath; }

    public Word2Vecf getWord2VecfModel() { return word2VecfModel; }
    public void setWord2VecfModel(Word2Vecf word2VecfModel) { this.word2VecfModel = word2VecfModel; }

    public boolean isUseStopWords() { return useStopWords; }
    public void setUseStopWords(boolean useStopWords) { this.useStopWords = useStopWords; }

    public boolean isIgnoreTarget() { return ignoreTarget; }
    public void setIgnoreTarget(boolean ignoreTarget) { this.ignoreTarget = ignoreTarget; }

    public int getWindowSize() { return windowSize; }
    public void setWindowSize(int windowSize) { this.windowSize = windowSize; }

    public List<List<Token>> getSents() { return sents; }
    public void setSents(List<List<Token>> sents) { this.sents = sents; }

    public int getTopInferences2Analyze() { return topInferences2Analyze; }
    public void setTopInferences2Analyze(int topInferences2Analyze) { this.topInferences2Analyze = topInferences2Analyze; }

    public Map<String, Integer> getW2Counts() { return w2Counts; }
    public void setW2Counts(Map<String, Integer> w2Counts) { this.w2Counts = w2Counts; }


}
