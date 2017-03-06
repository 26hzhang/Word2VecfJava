package com.isaac.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class DependencyGenerator {

	public enum Type {
		ALL, ONLY_WORD, ONLY_CONTEXT, BOTH
	}

	static DependencyGenerator thisMeasure = null;

	private final StanfordCoreNLP pipeline;

	public DependencyGenerator() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
		props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz");
		this.pipeline = new StanfordCoreNLP(props);
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	public static DependencyGenerator getDepGenerator() {
		if (thisMeasure == null) {
			thisMeasure = new DependencyGenerator();
		}
		return thisMeasure;
	}

	public List<String> getDependency(String sentence, String word, String context) {
		List<String> result = new ArrayList<String>();
		if (sentence == null || sentence.isEmpty())
			return result;
		Type type = Type.ALL; // Default
		if ((word == null || word.isEmpty()) && (context == null || context.isEmpty()))
			type = Type.ALL;
		else if (!word.isEmpty() && !context.isEmpty())
			type = Type.BOTH;
		else if (!word.isEmpty() && (context == null || context.isEmpty()))
			type = Type.ONLY_WORD;
		else
			type = Type.ONLY_CONTEXT;
		Annotation annotation = new Annotation(sentence);
		pipeline.annotate(annotation);
		CoreMap sent = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
		if (sent.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class) != null) {
			Collection<TypedDependency> list = sent.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class).typedDependencies();
			Function<TypedDependency, String> f = x -> x.toString().replaceAll("[\\d-]", "");
			if (type.equals(Type.ALL)) {
				result = list.stream().map(f).collect(Collectors.toList());
			} else if (type.equals(Type.ONLY_WORD)) {
				Pattern pattern = Pattern.compile("\\b" + word + "\\b");
				result = list.stream().map(f).filter(b -> pattern.matcher(b).find()).collect(Collectors.toList());
			} else if (type.equals(Type.ONLY_CONTEXT)) {
				Pattern pattern = Pattern.compile("\\b" + context + "\\b");
				result = list.stream().map(f).filter(b -> pattern.matcher(b).find()).collect(Collectors.toList());
			} else {
				Pattern pattern1 = Pattern.compile("\\b" + context + "\\b");
				Pattern pattern2 = Pattern.compile("\\b" + word + "\\b");
				result = list.stream().map(f).filter(b -> pattern1.matcher(b).find() && pattern2.matcher(b).find()).collect(Collectors.toList());
			}
		}
		return result;
	}

	// Input format:
	// String sentence = "Trump decided to [acquire] the (company).";
	public String getDependency(String sentence) {
		String word = sentence.substring(sentence.indexOf("[") + 1, sentence.indexOf("]"));
		String context = sentence.substring(sentence.indexOf("(") + 1, sentence.indexOf(")"));
		sentence = sentence.replaceAll("[\\[\\]\\(\\)]", "");
		Annotation annotation = new Annotation(sentence);
		pipeline.annotate(annotation);
		CoreMap sent = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
		if (sent.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class) != null) {
			Collection<TypedDependency> list = sent.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class).typedDependencies();
			for (TypedDependency l : list) {
				//System.out.println(l.toString());
				String str = l.toString().replaceAll("[\\d-]", "");
				if (str.contains(word) && str.contains(context)) {
					return str;
				}
			}
		}
		return null;
	}

	// Input format:
	// String sentence = "Trump decided to [acquire] the (company).";
	public static String getDep(String sentence) {
		String tgtWord = sentence.substring(sentence.indexOf("[") + 1, sentence.indexOf("]"));
		String tgtContext = sentence.substring(sentence.indexOf("(") + 1, sentence.indexOf(")"));
		sentence = sentence.replaceAll("[\\[\\]\\(\\)]", "");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
		props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz");
		Annotation annotation = new Annotation(sentence);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		pipeline.annotate(annotation);
		CoreMap sent = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
		if (sent.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class) != null) {
			Collection<TypedDependency> list = sent.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class).typedDependencies();
			for (TypedDependency l : list) {
				String str = l.toString().replaceAll("[\\d-]", "");
				if (str.contains(tgtWord) && str.contains(tgtContext)) {
					return str;
				}
			}
		}
		return String.format("Can not detect the relationaship between %s and %s.", tgtWord, tgtContext);
	}

}
