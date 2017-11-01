package com.isaac.examples;

import com.isaac.word2vec.CNNSentenceClassification;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class DL4JCNNSentClassifyExample {

    private static Logger log = LoggerFactory.getLogger(DL4JCNNSentClassifyExample.class);

    /** Data URL for downloading */
    private static final String DATA_URL = "http://ai.stanford.edu/~amaas/data/sentiment/aclImdb_v1.tar.gz";
    /** Location to save and extract the training/testing data */
    private static final String DATA_PATH = FilenameUtils.concat(System.getProperty("java.io.tmpdir"),
            "dl4j_w2vSentiment/");
    /** Location (local file system) for the Google News vectors. Set this manually. */
    private static final String WORD_VECTORS_PATH = "/Users/zhanghao/Documents/Files/GoogleNews-vectors-negative300.bin.gz";

    public static void main (String[] args) throws IOException {
        log.info("download and extract data...");
        CNNSentenceClassification.aclImdbDownloader(DATA_URL, DATA_PATH);

        // basic configuration
        int batchSize = 32;
        int vectorSize = 300;               //Size of the word vectors. 300 in the Google News model
        int nEpochs = 1;                    //Number of epochs (full passes of training data) to train on
        int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this
        int cnnLayerFeatureMaps = 100;      //Number of feature maps / channels / depth for each CNN layer
        PoolingType globalPoolingType = PoolingType.MAX;
        Random rng = new Random(12345); //For shuffling repeatability

        log.info("construct cnn model...");
        ComputationGraph net = CNNSentenceClassification.buildCNNGraph(vectorSize, cnnLayerFeatureMaps, globalPoolingType);
        log.info("number of parameters by layer:");
        for (Layer l : net.getLayers()) {
            log.info("\t" + l.conf().getLayer().getLayerName() + "\t" + l.numParams());
        }

        // Load word vectors and get the DataSetIterators for training and testing
        log.info("loading word vectors and creating DataSetIterators...");
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));
        DataSetIterator trainIter = CNNSentenceClassification.getDataSetIterator(DATA_PATH, true, wordVectors, batchSize,
                truncateReviewsToLength, rng);
        DataSetIterator testIter = CNNSentenceClassification.getDataSetIterator(DATA_PATH, false, wordVectors, batchSize,
                truncateReviewsToLength, rng);

        log.info("starting training...");
        for (int i = 0; i < nEpochs; i++) {
            net.fit(trainIter);
            log.info("Epoch " + i + " complete. Starting evaluation:");
            //Run evaluation. This is on 25k reviews, so can take some time
            Evaluation evaluation = net.evaluate(testIter);
            log.info(evaluation.stats());
        }

        // after training: load a single sentence and generate a prediction
        String pathFirstNegativeFile = FilenameUtils.concat(DATA_PATH, "aclImdb/test/neg/0_2.txt");
        String contentsFirstNegative = FileUtils.readFileToString(new File(pathFirstNegativeFile));
        INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator)testIter).loadSingleSentence(contentsFirstNegative);
        INDArray predictionsFirstNegative = net.outputSingle(featuresFirstNegative);
        List<String> labels = testIter.getLabels();
        log.info("\n\nPredictions for first negative review:");
        for( int i=0; i<labels.size(); i++ ){
            log.info("P(" + labels.get(i) + ") = " + predictionsFirstNegative.getDouble(i));
        }
    }
}
