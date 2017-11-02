## Java Implementation of Lexical Substitution Evaluation

This is the Java implementation of Oren Melamud's [lexsub](https://github.com/orenmel/lexsub) project, his lexsub project was used to perform the lexical substitution evaluation described in the following papers:

**[1] A Simple Word Embedding Model for Lexical Substitution** -- [[pdf]](http://u.cs.biu.ac.il/~melamuo/publications/melamud_vsm15.pdf), [[embeddings]](http://u.cs.biu.ac.il/~nlp/resources/downloads/lexsub_embeddings/) download.

Note that the embeddings used in [1] were trained by the method introduced in his another paper -- "**Dependency-Based Word Embeddings**", [[pdf]](https://levyomer.files.wordpress.com/2014/04/dependency-based-word-embeddings-acl-2014.pdf), [[slides]](https://levyomer.wordpress.com/2014/04/25/dependency-based-word-embeddings/), [[codes]](https://bitbucket.org/yoavgo/word2vecf), Java codes can be found [[here]](https://github.com/IsaacChanghau/Word2VecfJava).

**[2] context2vec: Learning Generic Context Embedding with Bidirectional LSTM** -- [[pdf]](http://u.cs.biu.ac.il/~melamuo/publications/context2vec_camera_ready.pdf), [[codes]](https://github.com/orenmel/context2vec).

The datasets used are introduced by the following papers:

**[3] Semeval-2007 task 10: English lexical substitution task** -- [[pdf]](http://hnk.ffzg.hr/bibl/acl2007/SemEval-2007/pdf/SemEval-200709.pdf)

**[4] What substitutes tell us-analysis of an "all-words" lexical substitution corpus**-- [[pdf]](http://www.aclweb.org/anthology/E14-1057)

Note that the data in the repository is already preprocessed by Oren et al.


### Requirements
* [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [ND4J](http://nd4j.org/), its source codes in GitHub [page](https://github.com/deeplearning4j/nd4j). For [Maven](http://mvnrepository.com/artifact/org.nd4j), import the following dependency:
```XML
<dependency>
    <groupId>org.nd4j</groupId>
    <artifactId>nd4j-native</artifactId>
    <version>${nd4j.version}</version>
</dependency>
```
* [Stanford NLP](https://nlp.stanford.edu/software/), its source codes in GitHub [page](https://github.com/stanfordnlp). For [Maven](https://mvnrepository.com/artifact/edu.stanford.nlp), import the following dependency:
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

### Evaluating the word embedding model

**Evaluate by Oren Melamud's python codes**
* Pre-process the embedding files:
```
python jcs/text2numpy.py <word-embeddings-filename> <word-embeddings-filename>
python jcs/text2numpy.py <context-embeddings-filename> <context-embeddings-filename>
```
* To perform the lexical substitution evaluation run (replace the example datasets files and params below as you wish):
```
python jcs/jcs_main.py --inferrer emb -vocabfile datasets/ukwac.vocab.lower.min100 -testfile datasets/lst_all.preprocessed -testfileconll datasets/lst_all.conll -candidatesfile datasets/lst.gold.candidates -embeddingpath <word-embeddings-filename> -embeddingpathc <context-embeddings-filename> -contextmath mult --debug -resultsfile <result-file>
```
* This will create the following output files: 
	- \<result-file\>
	- \<result-file\>.ranked
	- \<result-file\>.generate.oot
	- \<result-file\>.generate.best
* Run the following to compute the candidate ranking GAP score. The results will be written to \<gap-score-file\>.
```
python jcs/evaluation/lst/lst_gap.py ~/datasets/lst_all.gold <result-file>.ranked <gap-score-file> no-mwe
```
* Run the following to compute the OOT and BEST substitute prediction scores. The results will be written to \<xxx-score-file\>. score.pl was distributed in [3].
```
perl dataset/score.pl \<result-file\>.generate.oot datasets/lst_all.gold -t oot > \<oot-score-file\>
perl dataset/score.pl \<result-file\>.generate.best datasets/lst_all.gold -t best > \<best-score-file\>
```
* Sample results of Semeval-2007 dataset by "mult" method are shown in resources/semeval-mult-sample folder

**Evaluate by Java codes**

See details in JCSMain.java in com.isaac.lexsub package, training and gap scores are included. Note that we also use the score.pl file to deal with the OOT and BEST substitute prediction scores.

### TODO
* Implementation of evaluating the context2vec model.
* The Lemmatizer of Oren Melamud's codes is WordNetLemmatizer. In this repository, StanfordNLP Lemmatizer is used.
* Current codes are redundant, further clean up process needs to be done.
