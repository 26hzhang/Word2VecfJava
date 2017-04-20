package com.isaac.word2vecf.utils;

/**
 * Created by zhanghao on 18/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class NetworkConfig {
    public final int layerSize;
    public final int iterations;
    public final int numThreads;
    public final int minFrequency;
    public final double initialLearningRate;
    public final int negativeSamples;
    public final double downSampleRate;
    public final int debugMode;

    /** Constructor */
    public NetworkConfig(int layerSize, int iterations, int numThreads, int minFrequency, double initialLearningRate, int negativeSamples, double downSampleRate, int debugMode) {
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
