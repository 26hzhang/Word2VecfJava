package com.isaac.utils;

import java.util.List;

public class Word2VecfModel {
	public int layerSize;
	public List<String> words = null;
	public float[][] vectors = null;

	public Word2VecfModel() {
	}

	public Word2VecfModel(int layerSize, List<String> words, float[][] vectors) {
		this.layerSize = layerSize;
		this.words = words;
		this.vectors = vectors;
	}

	public int getLayerSize() {
		return layerSize;
	}

	public void setLayerSize(int layerSize) {
		this.layerSize = layerSize;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	public float[][] getVectors() {
		return vectors;
	}

	public void setVectors(float[][] vectors) {
		this.vectors = vectors;
	}

}
