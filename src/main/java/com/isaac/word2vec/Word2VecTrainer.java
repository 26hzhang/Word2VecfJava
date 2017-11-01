package com.isaac.word2vec;

import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

/**
 * Created by zhanghao on 11/4/17.
 * Modified by zhanghao on 01/11/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Word2VecTrainer {

	public static Word2Vec w2vBuilder(SentenceIterator iter, TokenizerFactory t) {
		return new Word2Vec.Builder()
				.seed(12345)
				.iterate(iter)
				.tokenizerFactory(t)
				.batchSize(1000)
				.allowParallelTokenization(true) // enable parallel tokenization
				.epochs(1) //  number of epochs (iterations over whole training corpus) for training
				.iterations(3) // number of iterations done for each mini-batch during training
				.elementsLearningAlgorithm(new SkipGram<>()) // use SkipGram Model. If CBOW: new CBOW<>()
				.minWordFrequency(50) // discard words that appear less than the times of set value
				.windowSize(5) // set max skip length between words
				.learningRate(0.05) // the starting learning rate
				.minLearningRate(5e-4) // learning rate should not lower than the set threshold value
				.negativeSample(10) // number of negative examples
				// set threshold for occurrence of words. Those that appear with higher frequency will be
				// randomly down-sampled
				.sampling(1e-5)
				.useHierarchicSoftmax(true) // use hierarchical softmax
				.layerSize(300) // size of word vectors
				.workers(8) // number of threads
				.build();
	}

	@SuppressWarnings("unused")
	public static Word2Vec w2vBuilder4SmallCorpus(SentenceIterator iter, TokenizerFactory t) {
		return new Word2Vec.Builder()
				.minWordFrequency(5)
				.iterations(1)
				.layerSize(100)
				.seed(42)
				.windowSize(5)
				.iterate(iter)
				.tokenizerFactory(t)
				.learningRate(0.025)
				.minLearningRate(1e-3)
				.build();
	}
}
