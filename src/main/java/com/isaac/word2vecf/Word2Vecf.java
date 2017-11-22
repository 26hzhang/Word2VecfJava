package com.isaac.word2vecf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhanghao on 18/4/17.
 * Modified by zhanghao on 01/11/17
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
@SuppressWarnings("all")
public class Word2Vecf extends Word2Vec {

	private final List<String> contextVocab;
	private final INDArray contextVectors;

	public Word2Vecf (int layerSize, List<String> wordVocab, INDArray wordVectors, List<String> contextVocab, INDArray contextVectors, boolean normalize) {
		super(layerSize, wordVocab, wordVectors, normalize);
		this.contextVocab = contextVocab;
		if (normalize) this.contextVectors = normalize(contextVectors);
		else this.contextVectors = contextVectors;
	}

	/** @return context vocabulary size, 0 if context vocabulary does not exist */
	public int contextVocabSize () {
		return contextVocab.size();
	}

	/** @return true if it is contained in context vocabulary, false if not or context vocabulary is null */
	public boolean hasContext (String str) {
		return contextVocab.contains(str);
	}

	/** @return {@link INDArray} vector if it is contained in context vocabulary, full-zero vector if not or context vocabulary is null */
	public INDArray getContextVector (String str) { return hasContext(str) ? contextVectors.getRow(contextVocab.indexOf(str)) : zeros(); }

	/** @return mean vector, built from contexts passed in */
	public INDArray getContextVectorMean (List<String> contexts) {
		INDArray res = zeros();
		if (contexts == null || contexts.isEmpty()) return res;
		for (String context : contexts) res.addi(getContextVector(context));
		return res.div(Nd4j.scalar(contexts.size()));
	}

	/** @return the index of given context in context vocabulary, -1 not found or context vocabulary does not exist */
	public int contextIndexOf (String context) { return contextVocab.indexOf(context); }

	/** @return cosine similarity between two given contexts */
	public double contextSimilarity (String context1, String context2) {
		return getContextVector(context1).mmul(getContextVector(context2).transpose()).getDouble(0);
	}

	/** @return cosine similarity between given word and context*/
	public double wordContextSimilarity (String word, String context) {
		return getWordVector(word).mmul(getContextVector(context).transpose()).getDouble(0);
	}

	/** @return similarity scores between vector and all records in context vectors, full-zero vector if context vocabulary does not exist */
	private INDArray contextSimilarity (INDArray vector) {
		Preconditions.checkArgument(vector.columns() == layerSize, String.format("vector's dimension(%d) must be the same as layer size(%d)", vector.columns(), layerSize));
		return contextVectors.mmul(vector.transpose());
	}

	/** @return p-cosine similarity scores between vector and all records in word vectors */
	private INDArray wordPosSimilarity (INDArray vector) {
		Preconditions.checkArgument(vector.columns() == layerSize, String.format("vector's dimension(%d) must be the same as layer size(%d)", vector.columns(), layerSize));
		return wordVectors.mmul(vector.transpose()).add(Nd4j.scalar(1)).div(Nd4j.scalar(2));
	}

	/** @return p-cosine similarity scores between vector and all records in context vectors, full-zero vector if context vocabulary does not exist */
	private INDArray contextPosSimilarity (INDArray vector) {
		Preconditions.checkArgument(vector.columns() == layerSize, String.format("vector's dimension(%d) must be the same as layer size(%d)", vector.columns(), layerSize));
		return contextVectors.mmul(vector.transpose()).add(Nd4j.scalar(1)).div(Nd4j.scalar(2));
	}

	/** @return list of nearest {@link Pair} of given context, size num, each {@link Pair} contains result context and it cosine score */
	public List<Pair<String, Double>> contextsNearest (String context, Integer top) {
		return contextsNearest(getContextVector(context), top);
	}

	/** @return list of nearest {@link Pair} of given context, size num, each {@link Pair} contains result context and it cosine score */
	public List<Pair<String, Double>> contextsNearest (INDArray context, Integer top) {
		top = MoreObjects.firstNonNull(top, 10);
		INDArray res = contextSimilarity(context);
		List<Pair<String, Double>> list = new ArrayList<>(contextVocab.size());
		for (int i = 0; i < contextVocab.size(); i++) { list.add(new Pair<>(contextVocab.get(i), res.getDouble(i))); }
		return list.stream().sorted((e1, e2) -> Double.valueOf(e2.getValue()).compareTo(Double.valueOf(e1.getValue()))).limit(top).collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Lexical Substitute task by Multiple Method
	 * @param word target word
	 * @param contexts list of given contexts
	 * @param average average the context vectors of given contexts
	 * @param top number of results return
	 * @return a list of {@link Pair}
	 */
	public List<Pair<String, Double>> lexicalSubstituteMult (String word, List<String> contexts, boolean average, Integer top) {
		top = MoreObjects.firstNonNull(top, 10);
		INDArray targetVec = getWordVector(word);
		INDArray scores = wordPosSimilarity(targetVec);
		for (String context : contexts) {
			if (hasContext(context)) {
				INDArray multScores = wordPosSimilarity(getContextVector(context));
				if (average) multScores = Transforms.pow(multScores, 1.0 / contexts.size());
				scores.muli(multScores);
			}
		}
		List<Pair<String, Double>> list = new ArrayList<>(wordVocab.size());
		for (int i = 0; i < wordVocab.size(); i++) { list.add(new Pair<>(wordVocab.get(i), scores.getDouble(i))); }
		return list.stream().sorted((e1, e2) -> Double.valueOf(e2.getValue()).compareTo(Double.valueOf(e1.getValue()))).limit(top).collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Lexical Substitute task by Add Method
	 * @param word target word
	 * @param contexts list of given contexts
	 * @param average average the context vectors of given contexts
	 * @param top number of results return
	 * @return a list of {@link Pair}
	 */
	public List<Pair<String, Double>> lexicalSubstituteAdd (String word, List<String> contexts, boolean average, Integer top) {
		top = MoreObjects.firstNonNull(top, 10);
		INDArray targetVec = getWordVector(word);
		INDArray ctxVec = zeros();
		int found = 0;
		for (String context : contexts) {
			if (!hasContext(context)) continue;
			found++;
			ctxVec.addi(getContextVector(context));
		}
		if (average && (found != 0)) ctxVec.divi(Nd4j.scalar(found));
		targetVec.addi(ctxVec);
		double norm = Math.sqrt(targetVec.mmul(targetVec.transpose()).getDouble(0));
		norm = norm == 0.0 ? 1.0 : norm;
		targetVec.divi(Nd4j.scalar(norm));
		INDArray scores = wordSimilarity(targetVec);
		List<Pair<String, Double>> list = new ArrayList<>(wordVocab.size());
		for (int i = 0; i < wordVocab.size(); i++) { list.add(new Pair<>(wordVocab.get(i), scores.getDouble(i))); }
		return list.stream().sorted((e1, e2) -> Double.valueOf(e2.getValue()).compareTo(Double.valueOf(e1.getValue()))).limit(top).collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Lexical Substitute task by Adaptive Method
	 * @param word target word
	 * @param contexts list of given contexts
	 * @param parameter parameter to adjust the weight between word and contexts
	 * @param top number of results return
	 * @return a list of {@link Pair}
	 */
	public List<Pair<String, Double>> lexicalSubstituteAdaptive (String word, List<String> contexts, double parameter, Integer top) {
		top = MoreObjects.firstNonNull(top, 10);
		if (parameter < 0 && parameter > 1.0)
			parameter = 0.5; // set default value
		INDArray targetVec = getWordVector(word);
		INDArray ctxVec = zeros();
		int found = 0;
		for (String context : contexts) {
			if (hasContext(context)) {
				found++;
				ctxVec.addi(getContextVector(context));
			}
		}
		if (found != 0) ctxVec.divi(Nd4j.scalar(found));
		INDArray wscores = wordSimilarity(targetVec);
		if (wscores.minNumber().doubleValue() < 0.0) wscores.addi(wscores.minNumber().doubleValue() * (-1.0f)).divi(wscores.maxNumber());
		else wscores.divi(wscores.maxNumber());
		INDArray cscores = wordVectors.subRowVector(targetVec).mmul(ctxVec.transpose());
		if (cscores.minNumber().doubleValue() < 0.0) cscores.addi(cscores.minNumber().doubleValue() * (-1.0)).divi(cscores.maxNumber());
		else cscores.divi(cscores.maxNumber());
		INDArray scores = wscores.mul(1.0 - parameter).add(cscores.mul(parameter));
		scores.divi(scores.maxNumber());
		List<Pair<String, Double>> list = new ArrayList<>(wordVocab.size());
		for (int i = 0; i < getWordVocab().size(); i++) { list.add(new Pair<>(wordVocab.get(i), scores.getDouble(i))); }
		return list.stream().sorted((e1, e2) -> Double.valueOf(e2.getValue()).compareTo(Double.valueOf(e1.getValue()))).limit(top).collect(Collectors.toCollection(LinkedList::new));
	}

	public List<String> getContextVocab () { return contextVocab; }

	public INDArray getContextVectors () { return contextVectors; }

}
