package com.isaac.examples;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

import java.util.Collection;
import java.util.Properties;

public class StanfordOpenIEExample {
    public static void main (String[] args) {
        // Create the Stanford CoreNLP pipeline
        Properties props = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie",
                "parse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz",
                "depparse.model", "edu/stanford/nlp/models/parser/nndep/english_SD.gz");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // Annotate an example document.
        String text = "I went into my bedroom and flipped the light switch. Oh, I see that the ceiling lamp is not turning on." +
                " It must be that the light bulb needs replacement. I go through my closet and find a new light bulb that will fit" +
                " this lamp and place it in my pocket. I also get my stepladder and place it under the lamp. I make sure the light" +
                " switch is in the off position. I climb up the ladder and unscrew the old light bulb. I place the old bulb in my " +
                "pocket and take out the new one. I then screw in the new bulb. I climb down the stepladder and place it back into " +
                "the closet. I then throw out the old bulb into the recycling bin. I go back to my bedroom and turn on the light switch." +
                " I am happy to see that there is again light in my room.";
        Annotation doc = new Annotation(text);
        pipeline.annotate(doc);
        // Loop over sentences in the document
        int sentNo = 0;
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            System.out.println("Sentence #" + ++sentNo + ": " + sentence.get(CoreAnnotations.TextAnnotation.class));
            // Get the OpenIE triples for the sentence
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            // Print the triples
            for (RelationTriple triple : triples) {
                System.out.println(triple.confidence + "\t" + triple.subjectLemmaGloss() + "\t" + triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss());
            }
            System.out.println("\n");
        }

    }
}
