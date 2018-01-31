package com.isaac.examples.word2vecf;

import com.isaac.word2vecf.utils.Common;
import com.isaac.word2vecf.utils.WordVectorSerializer;
import com.isaac.word2vecf.Word2Vec;

/**
 * since the only difference between GloVe embeddings and Word2Vec embeddings is that the first line of GloVe file does
 * not contain the vocabSize and layerSize information. To add such information in GloVe embeddings, you can simply run
 * the function in utils package: Common.GloveToWord2VecEmbeddingFormat(filePath, toPath)
 */
public class GloVeExample {
    public static void main (String[] args) {
        String filePath = "/Users/zhanghao/Documents/Files/glove.840B.300d.txt";
        // convert GloVe embeddings file to Word2Vec format
        Common.GloveToWord2VecEmbeddingFormat("<GloVe file path>", "<Path of converted result to save>");
        System.out.println("loading glove embeddings and creating word2vec model...");
        Word2Vec glove = WordVectorSerializer.loadWord2VecModel(filePath, false);
        System.out.println("done...");
        System.out.println("increase -- decrease similarity: " + glove.wordSimilarity("increase", "decrease"));
        System.out.println("increase: " + glove.getWordVector("increase"));
    }
}
