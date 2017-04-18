package com.isaac.examples;

import java.io.File;
import java.io.IOException;

import com.isaac.utils.FileUtils;
import com.isaac.representation.Word2VecfModel;
import com.isaac.word2vecf.Word2VecfConfig;
import com.isaac.word2vecf.Word2VecfTrainerByFile;

public class Word2VecfTrainExample {

	public static void main(String[] args) throws IOException, InterruptedException {
		Word2VecfConfig config = new Word2VecfConfig()
				.setTrainFile("/Volumes/Isaac Changhau/hyperwords/corpus4dir/dep.contexts")
				.setWordVocabFile("/Volumes/Isaac Changhau/hyperwords/corpus4dir/wv")
				.setContextVocabFile("/Volumes/Isaac Changhau/hyperwords/corpus4dir/cv")
				.setNumThreads(10)
				.setIterations(1)
				.setLayerSize(500)
				.setNegative(15)
				.setMinCount(100)
				.setDebugModel(2)
				.setBinary(0)
				.setClasses(0)
				.setSample(0)
				.setInitialLearningRate(0.025);

		Word2VecfModel model = new Word2VecfTrainerByFile(config).train();
		String directoryName = new File(config.getTrainFile()).getParent();
		FileUtils.saveWord2VecfModel(directoryName, model);
	}

}
