package com.isaac.utils;

import java.util.List;

public class EnsembleW2VfModel {

	public Word2VecfModel wordModel;
	public Word2VecfModel contextModel;

	public EnsembleW2VfModel() {

	}

	public EnsembleW2VfModel(Word2VecfModel wordModel, Word2VecfModel contextModel) {
		this.wordModel = wordModel;
		this.contextModel = contextModel;
	}

	public EnsembleW2VfModel(int layerSize, List<String> words, float[][] wvectors, List<String> contexts, float[][] cvectors) {
		this.wordModel = new Word2VecfModel(layerSize, words, wvectors);
		this.contextModel = new Word2VecfModel(layerSize, contexts, cvectors);
	}

	public Word2VecfModel getWordModel() {
		return wordModel;
	}

	public void setWordModel(Word2VecfModel wordModel) {
		this.wordModel = wordModel;
	}

	public Word2VecfModel getContextModel() {
		return contextModel;
	}

	public void setContextModel(Word2VecfModel contextModel) {
		this.contextModel = contextModel;
	}

}
