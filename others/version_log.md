## Version Information
This file is used to record the codes development information.

### Version 2.2.2
* Merge Lexical Substitution repository to this Word2Vecf repository
* Add some datasets (all for lexical substitution tasks)
* Cleaned up some redundant codes

### Version 2.2.1
* Rewrite Word2Vecf, which is extends from Word2Vec now, not a standalone model anymore.
* Add DeepLearning4J (DL4J) libraries, and add two examples of using DL4J to implement Word2Vec trainer and tackle some tasks as well as a Convolutional Neural Network based sentence classification tasks according to [Kim](https://arxiv.org/abs/1408.5882). This added files located at [[here]](/com/isaac/word2vec).
* re-structure the Word2Vecf package and cleanup some codes.

### Version 2.2
* Rewrite Word2Vec(f) model loader and saver, merge Word2VecModel and Word2VecfModel to ModelSerializer, cleanup the abundant codes. This process significantly reduce the resource occupancy by increasing the model restore time slightly.
* Delete self-defined Pair class, change to use the Java built-in Pair function. 
* Renamed some functions and classes. 
* Cleaned up CollFileGenerator, DependencyGenerator and ExpressionParser.
* Adjust the codes of examples, WordNetUtils as well as classes and functions in vocabulary package.
* Add Glove embeddings to Word2Vec embeddings converter as well as GloVe embeddings example.

### Version 2.1.1
* Bugs fixed: fail to load context embeddings of Word2VecfModel.
* Add WordNet Utility (first version) and example.
* For Word2VecModel, 8GB RAM is required, for Word2VecfModel, 16 GB RAM is required. (Testing embeddings: word embeddings--more than 200000 words, 500 dim; context embeddings--more than 850000 contexts, 500 dim)

### Version 2.1
* Some bugs fixed.
* Compatible with Word2Vec, say, you can use this repository to load trained result by Word2Vec, and perform similarity, nearest words tasks and etc. (Using Word2Vec and Word2VecModel, see Word2VecExample.java in examples)
* Rewrite embeddings load function, decrease resource occupancy
* Split Word2Vecf and Word2VecfModel and clean up redundant codes, Word2Vecf is separated to Word2Vecf and Word2Vec, where Word2Vecf handles word and context embeddings, while Word2Vec handles word embeddings only. Word2VecfModel is separated to Word2VecModel and Word2VecfModel, similar to Word2Vecf...
* For training, vectors use double-precision (double), then those vectors are converted to single-precision (float) to store (in order to save disk space and fast load).

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