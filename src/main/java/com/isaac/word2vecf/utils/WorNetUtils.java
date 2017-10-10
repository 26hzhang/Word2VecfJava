package com.isaac.word2vecf.utils;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;

public class WorNetUtils {
    /** Initialize IDictionary */
    private static final IDictionary wndict;
    static {
        URL wnUrl = null;
        try {
            String WordNetHome = new ClassPathResource("wordnet_dict").getFile().getAbsolutePath();
            wnUrl = new URL("file", null, WordNetHome);
        } catch (IOException e) {
            e.printStackTrace();
        }
        wndict = new Dictionary(wnUrl);
        try {
            wndict.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** @return true if the target word is a verb*/
    public static boolean isVerb (String word) { return wndict.getIndexWord(word, POS.VERB) != null; }

    /** @return true if the target word is a noun */
    public static boolean isNoun (String word) { return wndict.getIndexWord(word, POS.NOUN) != null; }

    /** @return true if the target word is an adjective */
    public static boolean isAdjective (String word) { return wndict.getIndexWord(word, POS.ADJECTIVE) != null; }

    /** @return true if the target word is an adverb */
    public static boolean isAdverb (String word) { return wndict.getIndexWord(word, POS.ADVERB) != null; }

}
