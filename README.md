## Word2VecfJava

It is a Java implementation of the [paper](http://www.aclweb.org/anthology/P14-2050): Dependency Based Word Embeddings, published by [Levy et al.](https://levyomer.wordpress.com/) in [ACL](https://www.aclweb.org/website/conference-list).

This algorithm uses the Skip-Gram method and train with shallow neural network, the input corpus is pre-processed by [Stanford Dependency Parser](http://nlp.stanford.edu/software/stanford-dependencies.shtml). For more information of word embedding technique, it is better to search the related information online. Usage already shown in examples.

### Version 1.0
This is the very beginning version.
* Achieves the pre-process and training process.
* The parameter setting is fix according to the Levy's paper. 
* Only the training process uses the parallel programming. 
* Some bugs also need to be fixed.

**TODO**
