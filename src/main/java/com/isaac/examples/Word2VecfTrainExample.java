package com.isaac.examples;

import java.io.File;
import java.io.IOException;

import com.isaac.utils.EnsembleW2VfModel;
import com.isaac.utils.FileUtils;
import com.isaac.word2vecf.Word2VecfConfig;
import com.isaac.word2vecf.Word2VecfTrainerByFile;

public class Word2VecfTrainExample {

	public static void main(String[] args) throws IOException, InterruptedException {
		Word2VecfConfig config = new Word2VecfConfig().setNumThreads(10).setIterations(1).setLayerSize(500).setNegative(15).setMinCount(100).setDebugModel(2)
				.setBinary(0).setClasses(0).setSample(0).setInitialLearningRate(0.025);
		String trainFilename = ""; // conll file
		String wvocabFilename = ""; // word vocab file
		String cvocabFilename = ""; // context vocab file
		EnsembleW2VfModel model = new Word2VecfTrainerByFile(config, trainFilename, wvocabFilename, cvocabFilename).train();
		String directoryName = new File(trainFilename).getParent();
		FileUtils.saveEnsembleModel(directoryName, model);
	}

}
