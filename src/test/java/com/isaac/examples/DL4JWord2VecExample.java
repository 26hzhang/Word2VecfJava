package com.isaac.examples;

import com.isaac.word2vec.Word2VecTrainer;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class DL4JWord2VecExample {

    private static Logger log = LoggerFactory.getLogger(DL4JWord2VecExample.class);

    public static void main (String[] args) throws Exception {
        // a 66.6MB sample data extract from around 13GB wikipedia dataset
        String filePath = new ClassPathResource("data/corpus_data.txt").getFile().getAbsolutePath();
        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        // CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
        // So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
        // Additionally it forces lower case for all tokens.
        t.setTokenPreProcessor(new CommonPreprocessor());
        log.info("Building model....");
        Word2Vec vec = Word2VecTrainer.w2vBuilder(iter, t);
        log.info("Fitting Word2Vec model....");
        vec.fit();
        log.info("done...");
        // Write word vectors to file
        log.info("Writing word vectors to file....");
        WordVectorSerializer.writeWord2VecModel(vec, "src/main/resources/word2vec_dl4j_model.bin");
        log.info("done...");
        // Load word vectors to Word2Vec
        log.info("Load word vectors from file");
        Word2Vec w2v = WordVectorSerializer.readWord2VecModel("src/main/resources/word2vec_dl4j_model.bin");
        log.info("Testing result:");
        Collection<String> lst = w2v.wordsNearest("man", 10);
        log.info("Closest Words--10 Words closest to \"man\": " + lst);
        double cosSim = w2v.similarity("man", "woman");
        log.info("Cosine Similarity between \"man\" and \"woman\": " + String.valueOf(cosSim));
    }
}
