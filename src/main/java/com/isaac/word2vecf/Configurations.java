package com.isaac.word2vecf;

/**
 * Created by zhanghao on 18/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Configurations {
    final int layerSize;
    final int iterations;
    final int numThreads;
    private final int minFrequency;
    final double initialLearningRate;
    final int negativeSamples;
    final double downSampleRate;
    private final int debugMode;

    /** Constructor */
    Configurations(int layerSize, int iterations, int numThreads, int minFrequency, double initialLearningRate, int negativeSamples, double downSampleRate, int debugMode) {
        this.layerSize = layerSize;
        this.iterations = iterations;
        this.numThreads = numThreads;
        this.minFrequency = minFrequency;
        this.initialLearningRate = initialLearningRate;
        this.negativeSamples = negativeSamples;
        this.downSampleRate = downSampleRate;
        this.debugMode = debugMode;
    }

    @Override
    public String toString() {
        return String.format("Training Configurations: threads: %d, iterations: %d, layer size: %d, minimum vocabulary frequency: %d, initial learning rate: %.5f, negative samples: %d, down sampling rate: %.5f, debug mode: %d\n",
                numThreads, iterations, layerSize, minFrequency, initialLearningRate, negativeSamples, downSampleRate, debugMode);
    }
}
