package com.isaac.lexsub;

public class LexSubConfigs {
    String inferrer = "emb";
    String lstmConfig = null; // only for Context2vecInferrer
    String embeddingPath = null; // only for CsEmbeddingInferrer
    String embeddingPathC = null; // only for CsEmbeddingInferrer
    String vocabFile = null;
    int bow = -1;
    String targetsFile = null;
    String testFile = null;
    String testFileConll = null;
    String candidatesFile = null;
    String resultsFile = null;
    String contextMath = "none";
    boolean ignoreTarget = false;
    boolean noPos = false;
    int topGenerated = 10;
    boolean debug = true;

    public LexSubConfigs() {

    }
	
	public LexSubConfigs(String inferrer, String vocabFile, String testFile, String testFileConll, String candidatesFile,
			String embeddingPath, String embeddingPathC, String contextMath, boolean debug, String resultsFile) {
		this.inferrer = inferrer;
		this.vocabFile = vocabFile;
		this.testFile = testFile;
		this.testFileConll = testFileConll;
		this.candidatesFile = candidatesFile;
		this.embeddingPath = embeddingPath;
		this.embeddingPathC = embeddingPathC;
		this.contextMath = contextMath;
		this.debug = debug;
		this.resultsFile = resultsFile;
	}

    public LexSubConfigs setInferrer(String inferrer) {
        this.inferrer = inferrer;
        return this;
    }

    public LexSubConfigs setLstmConfig(String lstmConfig) {
        this.lstmConfig = lstmConfig;
        return this;
    }

    public LexSubConfigs setEmbeddingPath(String embeddingPath) {
        this.embeddingPath = embeddingPath;
        return this;
    }

    public LexSubConfigs setEmbeddingPathC(String embeddingPathC) {
        this.embeddingPathC = embeddingPathC;
        return this;
    }

    public LexSubConfigs setVocabFile(String vocabFile) {
        this.vocabFile = vocabFile;
        return this;
    }

    public LexSubConfigs setBow(int bow) {
        this.bow = bow;
        return this;
    }

    public LexSubConfigs setTargetsFile(String targetsFile) {
        this.targetsFile = targetsFile;
        return this;
    }

    public LexSubConfigs setTestFile(String testFile) {
        this.testFile = testFile;
        return this;
    }

    public LexSubConfigs setTestFileConll(String testFileConll) {
        this.testFileConll = testFileConll;
        return this;
    }

    public LexSubConfigs setCandidatesFile(String candidatesFile) {
        this.candidatesFile = candidatesFile;
        return this;
    }

    public LexSubConfigs setResultsFile(String resultsFile) {
        this.resultsFile = resultsFile;
        return this;
    }

    public LexSubConfigs setContextMath(String contextMath) {
        this.contextMath = contextMath;
        return this;
    }

    public LexSubConfigs setIgnoreTarget(boolean ignoreTarget) {
        this.ignoreTarget = ignoreTarget;
        return this;
    }

    public LexSubConfigs setNoPos(boolean noPos) {
        this.noPos = noPos;
        return this;
    }

    public LexSubConfigs setTopGenerated(int topGenerated) {
        this.topGenerated = topGenerated;
        return this;
    }

    public LexSubConfigs setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
