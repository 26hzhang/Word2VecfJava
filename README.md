## Word2VecfJava

It is a Java implementation of the [paper](http://www.aclweb.org/anthology/P14-2050): Dependency Based Word Embeddings, published by [Levy et al.](https://levyomer.wordpress.com/) in [ACL](https://www.aclweb.org/website/conference-list).

This algorithm uses the Skip-Gram method and train with shallow neural network, the input corpus is pre-processed by [Stanford Dependency Parser](http://nlp.stanford.edu/software/stanford-dependencies.shtml). For more information of word embedding technique, it is better to search the related information online. Usage already shown in examples.

### Requirements
* [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
* [ND4J](http://nd4j.org/), its source codes in GitHub [page](https://github.com/deeplearning4j/nd4j). For [Maven](http://mvnrepository.com/artifact/org.nd4j), import the following snippet:
```XML
<dependency>
    <groupId>org.nd4j</groupId>
    <artifactId>nd4j-native</artifactId>
    <version>${nd4j.version}</version>
</dependency>
```
* [Stanford NLP](https://nlp.stanford.edu/software/), its source codes in GitHub [page](https://github.com/stanfordnlp). For [Maven](https://mvnrepository.com/artifact/edu.stanford.nlp), import the following snippet:
```XML
<dependency>
    <groupId>edu.stanford.nlp</groupId>
    <artifactId>stanford-corenlp</artifactId>
    <version>${stanfordnlp.version}</version>
</dependency>
```
```XML
<dependency>
    <groupId>edu.stanford.nlp</groupId>
    <artifactId>stanford-corenlp</artifactId>
    <version>${stanfordnlp.version}</version>
    <classifier>models</classifier>
</dependency>
```
* [Guava](https://github.com/google/guava). For [Maven](https://mvnrepository.com/artifact/com.google.guava/guava), import the following snippet:
```XML
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>${guava.version}</version>
</dependency>
```

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

### Version 2.1.1
* bugs fixed: fail to load context embeddings of Word2VecfModel.
* Add WordNet Utility (first version) and example.
* For Word2VecModel, 8GB RAM is required, for Word2VecfModel, 16 GB RAM is required. (Testing embeddings: word embeddings--more than 200000 words, 500 dim; context embeddings--more than850000 contexts, 500 dim)

### Version 2.1
* Some bugs fixed.
* Compatible with Word2Vec, say, you can use this repository to load trained result by Word2Vec, and perform similarity, nearest words tasks and etc. (Using Word2Vec and Word2VecModel, see Word2VecExample.java in examples)
* Rewrite embeddings load function, decrease resource occupancy
* Split Word2Vecf and Word2VecfModel and clean up redundant codes, Word2Vecf is separated to Word2Vecf and Word2Vec, where Word2Vecf handles word and context embeddings, while Word2Vec handles word embeddings only. Word2VecfModel is separated to Word2VecModel and Word2VecfModel, similar to Word2Vecf...
* For training, vectors use double-precision (double), then those vectors are converted to single-precision (float) to store (in order to disk space and fast load).

### Version 2.0
* Rewrite the codes and change the structure.
* Add Lexical Substitute methods (Add, BalAdd, Mult, BalMult) and demo test.
* Add WordSim353 task.
* Add TOEFL synonym questions solving task.
* Add SynSem analogy task.
* User defined parameters.
* Still... some bugs need to be fixed.

### Version 1.0
This is the very beginning version.
* Achieves the pre-process and training process.
* The parameter setting is fix according to the Levy's paper. 
* Some bugs also need to be fixed.