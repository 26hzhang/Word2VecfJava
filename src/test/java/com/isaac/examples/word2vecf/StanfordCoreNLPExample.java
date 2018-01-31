package com.isaac.examples.word2vecf;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;

public class StanfordCoreNLPExample {
    public static void main (String[] args) {
        String string = "I went into my bedroom and flipped the light switch. Oh, I see that the ceiling lamp is not turning on." +
                " It must be that the light bulb needs replacement. I go through my closet and find a new light bulb that will fit" +
                " this lamp and place it in my pocket. I also get my stepladder and place it under the lamp. I make sure the light" +
                " switch is in the off position. I climb up the ladder and unscrew the old light bulb. I place the old bulb in my " +
                "pocket and take out the new one. I then screw in the new bulb. I climb down the stepladder and place it back into " +
                "the closet. I then throw out the old bulb into the recycling bin. I go back to my bedroom and turn on the light switch." +
                " I am happy to see that there is again light in my room.";
        Properties prop = new Properties();
        prop.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        //prop.setProperty("parse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(prop);
        Annotation annotation = new Annotation(string);
        pipeline.annotate(annotation); // add annotation to pipeline
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                //String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.print(word + "/" + pos);
            }
            System.out.println("\n");
            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            System.out.println("parse tree:\n" + tree);

            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
            System.out.println("dependency graph:\n" + dependencies);
        }

    }
}
