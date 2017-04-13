package com.isaac.utils;

import java.util.List;

public class Word2VecfModel {
	private int layerSize;
	List<String> words = null;
	float[][] wordVectors = null;
	List<String> contexts = null;
	float[][] contextVectors = null;

	public Word2VecfModel() {}

	public Word2VecfModel(int layerSize, List<String> words, float[][] wordVectors) { // only keep the word embeddings
		this.layerSize = layerSize;
		this.words = words;
		this.wordVectors = wordVectors;
	}

	public Word2VecfModel(int layerSize, List<String> words, float[][] wordVectors, List<String> contexts,
						  float[][] contextVectors) {
		this.layerSize = layerSize;
		this.words = words;
		this.wordVectors = wordVectors;
		this.contexts = contexts;
		this.contextVectors = contextVectors;
	}

	public int getLayerSize() {
		return layerSize;
	}

	public Word2VecfModel setLayerSize(int layerSize) {
		this.layerSize = layerSize;
		return this;
	}

	public List<String> getWords() {
		return words;
	}

	public Word2VecfModel setWords(List<String> words) {
		this.words = words;
		return this;
	}

	public float[][] getWordVectors() {
		return wordVectors;
	}

	public Word2VecfModel setWordVectors(float[][] wordVectors) {
		this.wordVectors = wordVectors;
		return this;
	}

	public List<String> getContexts() {
		return contexts;
	}

	public Word2VecfModel setContexts(List<String> contexts) {
		this.contexts = contexts;
		return this;
	}

	public float[][] getContextVectors() {
		return contextVectors;
	}

	public Word2VecfModel setContextVectors(float[][] contextVectors) {
		this.contextVectors = contextVectors;
		return this;
	}

}
