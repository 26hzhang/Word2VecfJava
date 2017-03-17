## Word2VecfJava

It is a Java implementation of the [paper](http://www.aclweb.org/anthology/P14-2050): Dependency Based Word Embeddings, published by [Levy et al.](https://levyomer.wordpress.com/) in [ACL](https://www.aclweb.org/website/conference-list).

This algorithm uses the Skip-Gram method and train with shallow neural network, the input corpus is pre-processed by [Stanford Dependency Parser](http://nlp.stanford.edu/software/stanford-dependencies.shtml). For more information of word embedding technique, it is better to search the related information online. Usage already shown in examples.

### Notes

The Word2Vecf project is a modification of the original Word2Vec proposed by Mikolov, allowing: 

1. performing multiple iterations over the data. 
2. the use of arbitraty context features. 
3. dumping the context vectors at the end of the process

Unlike the original Word2Vec project, which can be used directly, the Word2Vecf needs some pre-computations, since the Word2Vecf **DOES NOT** handle vocabulary construction and **DOES NOT** read a sentence or paragraph as input directly.

The expected files are:

1. word_vocabulary: file mapping words (strings) to their counts.
2. context_vocabulary: file mapping contexts (strings) to their counts, used for constructing the sampling table for the negative training.
3. training_data: textual file of word-context pairs. each pair takes a seperate line. the format of a pair is "<word> <context>", i.e. space delimited, where <word> and <context> are strings. if we want to prefer some contexts over the others, we should construct the training data to contain the bias.

In order to make the project more usable, the pre-computations are implemented inside the project too. Since the Word2Vecf project is dependency-based word embeddings, the [stanford dependency parser](http://nlp.stanford.edu/software/stanford-dependencies.shtml) is used, more usage information can be found in its [website](http://nlp.stanford.edu/software/lex-parser.shtml).

### Version 1.0
This is the very beginning version.
* Achieves the pre-process and training process.
* The parameter setting is fix according to the Levy's paper. 
* Only the training process uses the parallel programming. 
* Some bugs also need to be fixed.

**TODO**
