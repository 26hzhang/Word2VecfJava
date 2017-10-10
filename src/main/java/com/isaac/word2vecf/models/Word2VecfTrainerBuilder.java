package com.isaac.word2vecf.models;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.io.File;

/**
 * Created by zhanghao on 18/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Word2VecfTrainerBuilder {

	private Integer layerSize;
	private Integer iterations;
	private Integer numThreads;
	private Integer minFrequency;
	private Double initialLearningRate;
	private Integer negativeSamples;
	private Double downSampleRate;

	private Integer debugMode = 2;

	private Word2VecfTrainerBuilder(){}

	public Word2VecfTrainerBuilder setLayerSize(int layerSize) {
		Preconditions.checkArgument(layerSize > 0, "layer size must be positive");
		this.layerSize = layerSize;
		return this;
	}

	public Word2VecfTrainerBuilder setNumIterations(int iterations) {
		Preconditions.checkArgument(iterations > 0, "iterations must be positive");
		this.iterations = iterations;
		return this;
	}

	public Word2VecfTrainerBuilder useNumThreads(int numThreads) {
		Preconditions.checkArgument(numThreads > 0, "numThreads must be positive");
		this.numThreads = numThreads;
		return this;
	}

	public Word2VecfTrainerBuilder setMinVocabFrequency(int minFrequency) {
		Preconditions.checkArgument(minFrequency >= 0, "minFrequency must be non-negative");
		this.minFrequency = minFrequency;
		return this;
	}

	public Word2VecfTrainerBuilder setInitialLearningRate(double initialLearningRate) {
		Preconditions.checkArgument(initialLearningRate > 0, "learning rate must be positive");
		this.initialLearningRate = initialLearningRate;
		return this;
	}

	public Word2VecfTrainerBuilder useNegativeSamples(int negativeSamples) {
		Preconditions.checkArgument(negativeSamples >= 0, "negative samples must be non-negative");
		this.negativeSamples = negativeSamples;
		return this;
	}

	public Word2VecfTrainerBuilder setDownSamplingRate(double downSampleRate) {
		Preconditions.checkArgument(downSampleRate >= 0, "down sampling rate must be non-negative");
		this.downSampleRate = downSampleRate;
		return this;
	}

	public Word2VecfTrainerBuilder setDebugMode(int debugMode) {
		Preconditions.checkArgument(debugMode >= 0 && debugMode <= 2, "the value of debug mode must 0<=debugMode<=2");
		this.debugMode = debugMode;
		return this;
	}

	/** @return {@link Word2Vecf} */
	public Word2Vecf train(String trainFile, String wordVocabFile, String contextVocabFile) {
		Preconditions.checkArgument(trainFile != null && !trainFile.isEmpty(), "training file must be assigned");
		if (!(new File(trainFile).exists()))
			throw new IllegalArgumentException("training file not found");
		Preconditions.checkArgument(wordVocabFile != null && !wordVocabFile.isEmpty(), "word vocabulary file must be assigned");
		if (!(new File(wordVocabFile).exists()))
			throw new IllegalArgumentException("word vocabulary file not found");
		Preconditions.checkArgument(contextVocabFile != null && !contextVocabFile.isEmpty(), "context vocabulary file must be assigned");
		if (!(new File(contextVocabFile).exists()))
			throw new IllegalArgumentException("context vocabulary file not found");
		this.layerSize = MoreObjects.firstNonNull(layerSize, 300);
		this.iterations = MoreObjects.firstNonNull(iterations, 1);
		this.numThreads = MoreObjects.firstNonNull(numThreads, Runtime.getRuntime().availableProcessors());
		this.minFrequency = MoreObjects.firstNonNull(minFrequency, 50);
		this.initialLearningRate = MoreObjects.firstNonNull(initialLearningRate, 0.025);
		this.negativeSamples = MoreObjects.firstNonNull(negativeSamples, 15);
		this.downSampleRate = MoreObjects.firstNonNull(downSampleRate, 0.001);
		this.debugMode = MoreObjects.firstNonNull(debugMode, 2);
		return new Word2VecfTrainer(trainFile, wordVocabFile, contextVocabFile,
				new Configurations(
						layerSize,
						iterations,
						numThreads,
						minFrequency,
						initialLearningRate,
						negativeSamples,
						downSampleRate,
						debugMode
				)
		).train();
	}

	/** @return {@link Word2VecfTrainerBuilder} for training a model */
	public static Word2VecfTrainerBuilder trainer() {
		return new Word2VecfTrainerBuilder();
	}

}
