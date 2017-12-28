## Word2VecfJava

![Authour](https://img.shields.io/badge/Author-Zhang%20Hao%20(Isaac%20Changhau)-blue.svg) ![](https://img.shields.io/badge/Java-1.8-brightgreen.svg) ![](https://img.shields.io/badge/DeepLearning4J-0.8.0-yellowgreen.svg) ![](https://img.shields.io/badge/ND4J-0.8.0-yellowgreen.svg) ![](https://img.shields.io/badge/StanfordCoreNLP-3.8.0-yellowgreen.svg) ![](https://img.shields.io/badge/Guava-21.0-yellowgreen.svg)

It is a Java implementation of the [paper](http://www.aclweb.org/anthology/P14-2050): Dependency Based Word Embeddings, published by [Levy et al.](https://levyomer.wordpress.com/) in [ACL](https://www.aclweb.org/website/conference-list), and extensions.

This algorithm uses the Skip-Gram method and train with shallow neural network, the input corpus is pre-processed by [Stanford Dependency Parser](http://nlp.stanford.edu/software/stanford-dependencies.shtml). For more information of word embedding technique, it is better to search the related information online. Usage already shown in examples.

### Requirements
* [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
* [DL4J](https://deeplearning4j.org), its GitHub page: [[link]](https://github.com/deeplearning4j/deeplearning4j), and Maven source: [[link]](https://mvnrepository.com/artifact/org.deeplearning4j).
* [ND4J](http://nd4j.org/), its GitHub page: [[link]](https://github.com/deeplearning4j/nd4j), and Maven source: [[link]](http://mvnrepository.com/artifact/org.nd4j).
* [Stanford NLP](https://nlp.stanford.edu/software/), its GitHub page: [[link]](https://github.com/stanfordnlp), and Maven sources: [[link]](https://mvnrepository.com/artifact/edu.stanford.nlp) (For Maven, please import both corenlp and corenlp with classifier `models` snippets).
* [Guava](https://github.com/google/guava), its Maven sources: [[link]](https://mvnrepository.com/artifact/com.google.guava/guava).

### Notes

The Word2Vecf project is a modification of the original Word2Vec proposed by Mikolov, allowing: 

1. performing multiple iterations over the data. 
2. the use of arbitrary context features. 
3. dumping the context vectors at the end of the process

Unlike the original Word2Vec project, which can be used directly, the Word2Vecf needs some pre-computations, since the Word2Vecf **DOES NOT** handle vocabulary construction and **DOES NOT** read a sentence or paragraph as input directly.

The expected files are:

1. word_vocabulary: file mapping words (strings) to their counts.
2. context_vocabulary: file mapping contexts (strings) to their counts, used for constructing the sampling table for the negative training.
3. training_data: textual file of word-context pairs. each pair takes a separate line. the format of a pair is "(word context)", i.e. space delimited, where <word> and <context> are strings. if we want to prefer some contexts over the others, we should construct the training data to contain the bias.

In order to make the project more usable, the pre-computations are implemented inside the project too. Since the Word2Vecf project is dependency-based word embeddings, the [stanford dependency parser](http://nlp.stanford.edu/software/stanford-dependencies.shtml) is used, more usage information can be found in its [website](http://nlp.stanford.edu/software/lex-parser.shtml).

### Semantic Property Task
* WordSim353: The WordSim353 set contains 353 word pairs. It was constructed by asking human subjects to rate the degree of semantic similarity or relatedness between two words on a numerical scale. The performance is measured by the Pearson correlation of the two word embeddings’ cosine distance and the average score given by the participants. [[pdf]](http://gabrilovich.com/papers/context_search.pdf)
* TOEFL: The TOEFL set contains 80 multiple-choice synonym questions, each with 4 candidates. For example, the question word levied has choices: imposed (correct), believed, requested and correlated. Choose the nearest neighbor of the question word from the candidates based on the cosine distance and use the accuracy to measure the performance. [[pdf]](http://www.indiana.edu/~clcl/Q550_WWW/Papers/Landauer_Dumais_1997.pdf)
* Analogy: The analogy task has approximately 9K semantic and 10.5K syntactic analogy questions. The question are similar to “man is to (woman) as king is to queen” or “predict is to (predicting) as dance is to dancing”. Following the previous work, using the nearest neighbor of "queen − king + man" in the vocabulary as the answer. Additionally, the accuracy is used to measure the performance. This dataset is relatively large compared to the previous two sets; therefore, the results using this dataset are more stable than those using the previous two datasets. [[pdf]](https://arxiv.org/pdf/1301.3781.pdf)

### Reference
- [eikdk/Word2VecJava](https://github.com/eikdk/Word2VecJava)
- [word2vec -- google sources](https://code.google.com/archive/p/word2vec/), [download](https://github.com/dav/word2vec)
- [Yoav Goldberg/word2vecf](https://bitbucket.org/yoavgo/word2vecf/overview)
- [orenmel/lexsub](https://github.com/orenmel/lexsub)
- [GoogleNews-vectors-negative300.bin](https://github.com/mmihaltz/word2vec-GoogleNews-vectors) (Pre-trained Google News corpus (3 billion running words) word vector model (3 million 300-dimension English word vectors))

### Other Information
[Version Log.](others/version_log.md)

[Word2Vecf C Codes Usage](others/w2vf_c_code_usage.md)
