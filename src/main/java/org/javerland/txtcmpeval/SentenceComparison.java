package org.javerland.txtcmpeval;

import static java.util.Objects.isNull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Comparison of two sentences. Sentences are split to words, every word is
 * compared with every word of the compared sentence by {@link WordComparison}
 * and the best pairing of words is evaluated. Words which stay unpaired
 * decrease the result.
 *
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public class SentenceComparison extends BasicCommonProperties implements Comparison {

    /**
     * Everything what isn't letter or digit is a separator of words.
     */
    private final static Pattern WORDS_SEPARATOR = Pattern.compile("[^\\p{L}\\p{N}]+");

    private final static String[] NO_WORDS = new String[0];

    public SentenceComparison() {
        super();
    }

    public SentenceComparison(boolean adhereWordsOrder, boolean stripAccent) {
        super(adhereWordsOrder, stripAccent);
    }

    public SentenceComparison(boolean adhereWordsOrder, boolean stripAccent, boolean ignoreCase) {
        super(adhereWordsOrder, stripAccent, ignoreCase);
    }

    @Override
    public double compare(String sentence, String comparedSentence) {

        // If're both null result is 1.0 or 100%.
        if (isNull(sentence) && isNull(comparedSentence)) {
            return 1.0d;
        }

        String[] words = splitToWords(sentence);
        String[] comparedWords = splitToWords(comparedSentence);
        // Both are without words ... it's 100% result
        if (words.length == 0 && comparedWords.length == 0) {
            return 1.0d;
        }
        // Only one of them is without words ... there is nothing to compare
        if (words.length == 0 || comparedWords.length == 0) {
            return 0.0d;
        }

        double[][] similarities = new double[words.length][comparedWords.length];
        WordComparison wordComparison = new WordComparison(stripAccent, ignoreCase);
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < comparedWords.length; j++) {
                similarities[i][j] = wordComparison.compare(words[i], comparedWords[j]);
            }
        }

        return adhereWordsOrder ? Aligner.orderedScore(similarities) : Aligner.unorderedScore(similarities);
    }

    /**
     * Splits sentence to words, all separators and empty words are skipped.
     *
     * @param sentence Sentence to split.
     * @return Words of the sentence, never <code>null</code>.
     */
    static String[] splitToWords(String sentence) {
        if (isNull(sentence) || sentence.trim().isEmpty()) {
            return NO_WORDS;
        }
        List<String> words = new ArrayList<String>();
        for (String word : WORDS_SEPARATOR.split(sentence.trim())) {
            // Sentence can begin with a separator, it makes an empty word.
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        return words.toArray(NO_WORDS);
    }

}
