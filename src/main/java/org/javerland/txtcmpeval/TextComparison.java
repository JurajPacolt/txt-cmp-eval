package org.javerland.txtcmpeval;

import static java.util.Objects.isNull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Comparison of two texts. Text is split to words and every word is compared
 * with every word of the compared text by {@link WordComparison}, so typos are
 * taken into account too. Then the best pairing of words is evaluated, words
 * which stay unpaired decrease the result.
 * <p>
 * When {@link #setCompareBySentences(boolean)} is set to <code>true</code>,
 * text is split to sentences first and sentences are paired by
 * {@link SentenceComparison}, so words of one sentence can't be paired with
 * words of a different sentence.
 *
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public class TextComparison extends BasicCommonProperties implements Comparison {

    /**
     * End of sentence, it means punctuation or end of line.
     */
    private final static Pattern SENTENCES_SEPARATOR = Pattern.compile("[.!?;:\\r\\n]+");

    private final static String[] NO_SENTENCES = new String[0];

    /**
     * Comparing sentence by sentence instead of word by word, default is false.
     */
    protected boolean compareBySentences = false;
    /**
     * Adhering sentences order, default is true. It's used only when text is
     * compared sentence by sentence.
     */
    protected boolean adhereSentencesOrder = true;

    public TextComparison() {
        super();
    }

    public TextComparison(boolean adhereWordsOrder, boolean stripAccent, boolean ignoreCase) {
        super(adhereWordsOrder, stripAccent, ignoreCase);
    }

    public TextComparison(boolean compareBySentences, boolean adhereSentencesOrder, boolean adhereWordsOrder,
            boolean stripAccent, boolean ignoreCase) {
        super(adhereWordsOrder, stripAccent, ignoreCase);
        this.compareBySentences = compareBySentences;
        this.adhereSentencesOrder = adhereSentencesOrder;
    }

    @Override
    public double compare(String text, String comparedText) {

        // If're both null result is 1.0 or 100%.
        if (isNull(text) && isNull(comparedText)) {
            return 1.0d;
        }

        return compareBySentences ? compareSentences(text, comparedText) : compareWords(text, comparedText);
    }

    /**
     * Compares texts as two sets of words, sentences aren't taken into account.
     * Words separated by punctuation are split in the same way as words of one
     * sentence, so sentence comparison can be used for the whole text.
     */
    private double compareWords(String text, String comparedText) {
        return new SentenceComparison(adhereWordsOrder, stripAccent, ignoreCase).compare(text, comparedText);
    }

    /**
     * Compares texts sentence by sentence, words are paired only inside of the
     * paired sentences.
     */
    private double compareSentences(String text, String comparedText) {
        String[] sentences = splitToSentences(text);
        String[] comparedSentences = splitToSentences(comparedText);
        // Both are without sentences ... it's 100% result
        if (sentences.length == 0 && comparedSentences.length == 0) {
            return 1.0d;
        }
        // Only one of them is without sentences ... there is nothing to compare
        if (sentences.length == 0 || comparedSentences.length == 0) {
            return 0.0d;
        }

        double[][] similarities = new double[sentences.length][comparedSentences.length];
        SentenceComparison sentenceComparison = new SentenceComparison(adhereWordsOrder, stripAccent, ignoreCase);
        for (int i = 0; i < sentences.length; i++) {
            for (int j = 0; j < comparedSentences.length; j++) {
                similarities[i][j] = sentenceComparison.compare(sentences[i], comparedSentences[j]);
            }
        }

        return adhereSentencesOrder ? Aligner.orderedScore(similarities) : Aligner.unorderedScore(similarities);
    }

    /**
     * Splits text to sentences, all sentences without any word are skipped.
     *
     * @param text Text to split.
     * @return Sentences of the text, never <code>null</code>.
     */
    static String[] splitToSentences(String text) {
        if (isNull(text) || text.trim().isEmpty()) {
            return NO_SENTENCES;
        }
        List<String> sentences = new ArrayList<String>();
        for (String sentence : SENTENCES_SEPARATOR.split(text.trim())) {
            if (SentenceComparison.splitToWords(sentence).length > 0) {
                sentences.add(sentence.trim());
            }
        }
        return sentences.toArray(NO_SENTENCES);
    }

    /**
     * Is text compared sentence by sentence?
     *
     * @return <code>true</code> when text is split to sentences before
     * comparison.
     */
    public boolean isCompareBySentences() {
        return compareBySentences;
    }

    /**
     * Sets if text has to be split to sentences before comparison. When it's
     * <code>false</code>, whole text is compared as one set of words.
     *
     * @param compareBySentences Comparing sentence by sentence.
     */
    public void setCompareBySentences(boolean compareBySentences) {
        this.compareBySentences = compareBySentences;
    }

    /**
     * Is sentences order taken into account?
     *
     * @return <code>true</code> when sentences order is adhered.
     */
    public boolean isAdhereSentencesOrder() {
        return adhereSentencesOrder;
    }

    /**
     * Sets if sentences order has to be taken into account. When it's
     * <code>false</code>, sentences of compared texts are paired by their
     * similarity without regard to their position. It's used only when text is
     * compared sentence by sentence.
     *
     * @param adhereSentencesOrder Adhering sentences order.
     */
    public void setAdhereSentencesOrder(boolean adhereSentencesOrder) {
        this.adhereSentencesOrder = adhereSentencesOrder;
    }

    /**
     * Entry point of application for command line using. Both arguments are
     * compared texts, when argument is a path to an existing file, content of
     * the file is compared instead of it.
     * 
     * @param args Arguments for application.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: TextComparison <text-or-file> <compared-text-or-file>");
            return;
        }

        TextComparison comparison = new TextComparison();
        double result;
        try {
            result = comparison.compare(readIfFile(args[0]), readIfFile(args[1]));
        } catch (IOException e) {
            System.out.println("File can't be read: " + e.getMessage());
            return;
        }

        System.out.println(comparison.calcToPercentage(result) + " % (" + result + ")");
    }

    /**
     * Reads content of the file when argument is a path to an existing file,
     * otherwise argument is a compared text itself.
     *
     * @param arg Argument of the application.
     * @return Text to compare.
     * @throws IOException When file exists but can't be read.
     */
    private static String readIfFile(String arg) throws IOException {
        File file = new File(arg);
        if (file.isFile()) {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        }
        return arg;
    }

}
