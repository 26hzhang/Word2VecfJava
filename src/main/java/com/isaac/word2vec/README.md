# Word2Vec Deeplearning4j Example
It is a Java practice to implement Word2Vec methods and the semantic property measure tasks with [DeepLearning4J](https://deeplearning4j.org) and [ND4J](http://nd4j.org) libraries.

### Word2Vec
* Mathematical theory in Word2Vec: [[link]](http://www.cnblogs.com/peghoty/p/3857839.html). (Chinese)
* Word2Vec: Neural Word Embedding in Java: [[link]](https://deeplearning4j.org/word2vec).
* Word2Vec wikipedia: [[link]](https://en.wikipedia.org/wiki/Word2vec).
* Efficient Estimation of Word Representations in Vector Space, [[pdf]](https://arxiv.org/pdf/1301.3781.pdf)
* Word2Vec Explained: Deriving Mikolov et al.’s Negative-Sampling Word-Embedding Method, [[pdf]](https://arxiv.org/pdf/1402.3722.pdf)

### Semantic Property Task
* WordSim353: The WordSim353 set contains 353 word pairs. It was constructed by asking human subjects to rate the degree of semantic similarity or relatedness between two words on a numerical scale. The performance is measured by the Pearson correlation of the two word embeddings’ cosine distance and the average score given by the participants. [[pdf]](http://gabrilovich.com/papers/context_search.pdf)
* TOEFL: The TOEFL set contains 80 multiple-choice synonym questions, each with 4 candidates. For example, the question word levied has choices: imposed (correct), believed, requested and correlated. Choose the nearest neighbor of the question word from the candidates based on the cosine distance and use the accuracy to measure the performance. [[pdf]](http://www.indiana.edu/~clcl/Q550_WWW/Papers/Landauer_Dumais_1997.pdf)
* Analogy: The analogy task has approximately 9K semantic and 10.5K syntactic analogy questions. The question are similar to “man is to (woman) as king is to queen” or “predict is to (predicting) as dance is to dancing”. Following the previous work, using the nearest neighbor of "queen − king + man" in the vocabulary as the answer. Additionally, the accuracy is used to measure the performance. This dataset is relatively large compared to the previous two sets; therefore, the results using this dataset are more stable than those using the previous two datasets. [[pdf]](https://arxiv.org/pdf/1301.3781.pdf)

### Embedding as the Initialization of NNs
[Erhan et al.](http://www.jmlr.org/papers/volume11/erhan10a/erhan10a.pdf) have demonstrated that a better initialization can cause a neural network model to converge to a better local optimum. In recent neural network approaches to NLP tasks, word embeddings have been used to initialize the first layer.
* Sentence Classification Task: use Convolutional neural networks (CNN) according to [Kim](https://arxiv.org/abs/1408.5882) to perform sentence-level sentiment classification on [aclImdb](http://ai.stanford.edu/~amaas/data/sentiment/aclImdb_v1.tar.gz) movie reviews database. (The codes of this task is obtained from [[here]](https://github.com/deeplearning4j/dl4j-examples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/sentenceclassification/CnnSentenceClassificationExample.java))

### Semantic Property Task Result

Codes at [DL4JWord2VecSemanticExample.java](/src/test/java/com/isaac/examples/DL4JWord2VecSemanticExample.java)
```
[main] INFO com.isaac.word2vec.SemanticMeasure - load word2vec model
[main] INFO com.isaac.word2vec.SemanticMeasure - done.
[main] INFO com.isaac.word2vec.SemanticMeasure - Semantic Property Task...
[main] INFO com.isaac.word2vec.SemanticMeasure - |********************load TOEFL data********************|
[main] INFO com.isaac.word2vec.SemanticMeasure - run the test
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 80, Ignore: 1, Accuracy: 87.50%(70/80)
[main] INFO com.isaac.word2vec.SemanticMeasure - |*************************done.*************************|
[main] INFO com.isaac.word2vec.SemanticMeasure - |*******************load Syn_Sem data*******************|
[main] INFO com.isaac.word2vec.SemanticMeasure - run the test
[main] INFO com.isaac.word2vec.SemanticMeasure - capital-common-countries
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 506, Ignore: 0, Accuracy: 87.15%(441/506)
[main] INFO com.isaac.word2vec.SemanticMeasure - capital-world
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 4524, Ignore: 0, Accuracy: 79.82%(3611/4524)
[main] INFO com.isaac.word2vec.SemanticMeasure - currency
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 866, Ignore: 3, Accuracy: 35.10%(304/866)
[main] INFO com.isaac.word2vec.SemanticMeasure - city-in-state
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 2467, Ignore: 0, Accuracy: 70.94%(1750/2467)
[main] INFO com.isaac.word2vec.SemanticMeasure - family
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 506, Ignore: 0, Accuracy: 84.58%(428/506)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram1-adjective-to-adverb
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 992, Ignore: 0, Accuracy: 28.53%(283/992)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram2-opposite
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 812, Ignore: 0, Accuracy: 42.73%(347/812)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram3-comparative
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 1332, Ignore: 0, Accuracy: 90.84%(1210/1332)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram4-superlative
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 1122, Ignore: 0, Accuracy: 87.34%(980/1122)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram5-present-participle
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 1056, Ignore: 0, Accuracy: 78.22%(826/1056)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram6-nationality-adjective
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 1599, Ignore: 0, Accuracy: 89.93%(1438/1599)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram7-past-tense
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 1560, Ignore: 0, Accuracy: 65.96%(1029/1560)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram8-plural
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 1332, Ignore: 0, Accuracy: 89.86%(1197/1332)
[main] INFO com.isaac.word2vec.SemanticMeasure - gram9-plural-verbs
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions: 870, Ignore: 0, Accuracy: 67.93%(591/870)
[main] INFO com.isaac.word2vec.SemanticMeasure - Total Questions in questions-words.txt: 19544, Accuracy: 70.84%(13844/19544)
[main] INFO com.isaac.word2vec.SemanticMeasure - |*************************done.*************************|
[main] INFO com.isaac.word2vec.SemanticMeasure - |********************load WS353 data********************|
[main] INFO com.isaac.word2vec.SemanticMeasure - Start Testing
[main] INFO com.isaac.word2vec.SemanticMeasure - WS353: 34.98%
[main] INFO com.isaac.word2vec.SemanticMeasure - WS353 Relatedness: 30.50%
[main] INFO com.isaac.word2vec.SemanticMeasure - WS353 Similarity: 44.90%
[main] INFO com.isaac.word2vec.SemanticMeasure - |*************************done.*************************|

Process finished with exit code 0
```
The result of WS353 task is terrible...
