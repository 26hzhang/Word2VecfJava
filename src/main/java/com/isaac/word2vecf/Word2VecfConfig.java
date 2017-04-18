package com.isaac.word2vecf;

public class Word2VecfConfig {
	String trainFile = "";
	String wordVocabFile = "";
	String contextVocabFile = "";
	int numThreads = 10;
	int iterations = 1;
	int layerSize = 500;
	int negative = 15;
	int minCount = 50;
	int debugModel = 2;
	int binary = 0;
	double initialLearningRate = 0.025;
	int classes = 0;
	double sample = 0;

	public Word2VecfConfig() {}

	public Word2VecfConfig(String trainFile, String wordVocabFile, String contextVocabFile, int numThreads,
						   int iterations, int layerSize, int negative, int minCount, int debugModel, int binary,
						   int classes, double sample, double initialLearningRate) {
		this.trainFile = trainFile;
		this.wordVocabFile = wordVocabFile;
		this.contextVocabFile = contextVocabFile;
		this.numThreads = numThreads;
		this.iterations = iterations;
		this.layerSize = layerSize;
		this.negative = negative;
		this.minCount = minCount;
		this.debugModel = debugModel;
		this.binary = binary;
		this.classes = classes;
		this.sample = sample;
		this.initialLearningRate = initialLearningRate;
	}

	public Word2VecfConfig setTrainFile(String trainFile) {
		this.trainFile = trainFile;
		return this;
	}

	public String getTrainFile() {
		return this.trainFile;
	}

	public Word2VecfConfig setWordVocabFile(String wordVocabFile) {
		this.wordVocabFile = wordVocabFile;
		return this;
	}

	public Word2VecfConfig setContextVocabFile(String contextVocabFile) {
		this.contextVocabFile = contextVocabFile;
		return this;
	}

	public Word2VecfConfig setNumThreads(int numThreads) {
		this.numThreads = numThreads;
		return this;
	}

	public Word2VecfConfig setIterations(int iterations) {
		this.iterations = iterations;
		return this;
	}

	public Word2VecfConfig setLayerSize(int layerSize) {
		this.layerSize = layerSize;
		return this;
	}

	public Word2VecfConfig setNegative(int negative) {
		this.negative = negative;
		return this;
	}

	public Word2VecfConfig setMinCount(int minCount) {
		this.minCount = minCount;
		return this;
	}

	public Word2VecfConfig setDebugModel(int debugModel) {
		this.debugModel = debugModel;
		return this;
	}

	public Word2VecfConfig setBinary(int binary) {
		this.binary = binary;
		return this;
	}

	public Word2VecfConfig setClasses(int classes) {
		this.classes = classes;
		return this;
	}

	public Word2VecfConfig setSample(double sample) {
		this.sample = sample;
		return this;
	}

	public Word2VecfConfig setInitialLearningRate(double initialLearningRate) {
		this.initialLearningRate = initialLearningRate;
		return this;
	}

	@Override
	public String toString() {
		return String.format(
				"Word2Vecf trainer with %s threads, %s iterations[%s layer size, %s negative samples, %s initial learning rate, %s down sample rate]",
				numThreads, iterations, layerSize, negative, initialLearningRate, sample);
	}
}
