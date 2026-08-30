package org.javerland.txtcmpeval;

import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;
import org.apache.commons.lang3.StringUtils;

/**
 * Comparison of two single words. Words are compared character by character,
 * result is based on count of characters which have to be changed, added,
 * removed or transposed to make words equal (Damerau-Levenshtein distance), so
 * common typos decrease the result only a little.
 *
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public class WordComparison extends BasicCommonProperties implements Comparison {

    public WordComparison() {
        super();
    }

    public WordComparison(boolean stripAccent) {
        super(stripAccent);
    }

    public WordComparison(boolean stripAccent, boolean ignoreCase) {
        super(stripAccent);
        this.ignoreCase = ignoreCase;
    }

    @Override
    public double compare(String word, String comparedWord) {

        // If're both null result is 1.0 or 100%.
        if (isNull(word) && isNull(comparedWord)) {
            return 1.0d;
        }

        // First we need prepare strings.
        String w1 = nonNull(word) ? normalize(word) : EMPTY_STRING;
        String w2 = nonNull(comparedWord) ? normalize(comparedWord) : EMPTY_STRING;
        // Both are empty ... it's 100% result
        if (StringUtils.isEmpty(w1) && StringUtils.isEmpty(w2)) {
            return 1.0d;
        }
        // Only one of them is empty ... there is nothing to compare
        if (StringUtils.isEmpty(w1) || StringUtils.isEmpty(w2)) {
            return 0.0d;
        }
        if (w1.equals(w2)) {
            return 1.0d;
        }

        // Distance says how many characters have to be changed, added, removed or
        // transposed,
        // the rest of the longer word is the matching part of both words. Equaling
        // characters are moved to equals positions by the distance calculation
        // itself, so strings don't have to be stretched to the same length before.
        char[] cha1 = w1.toCharArray();
        char[] cha2 = w2.toCharArray();
        int distance = calculateDistance(cha1, cha2);
        int length = Math.max(cha1.length, cha2.length);

        return (length - distance) / (double) length;
    }

    /**
     * Calculates Damerau-Levenshtein distance of two words, it means minimal
     * count of changed, added, removed and transposed characters needed to make
     * words equal. Transposition of two neighbouring characters is counted as
     * one typo only. Just three rows of the matrix are held in the memory.
     *
     * @param cha1 Characters of first word.
     * @param cha2 Characters of compared word.
     * @return Distance from 0 to length of the longer word.
     */
    static int calculateDistance(char[] cha1, char[] cha2) {
        int[] beforePreviousRow = new int[cha2.length + 1];
        int[] previousRow = new int[cha2.length + 1];
        int[] actualRow = new int[cha2.length + 1];

        for (int j = 0; j <= cha2.length; j++) {
            previousRow[j] = j;
        }

        for (int i = 1; i <= cha1.length; i++) {
            actualRow[0] = i;
            for (int j = 1; j <= cha2.length; j++) {
                int substitution = previousRow[j - 1] + (cha1[i - 1] == cha2[j - 1] ? 0 : 1);
                int deletion = previousRow[j] + 1;
                int insertion = actualRow[j - 1] + 1;
                int distance = Math.min(substitution, Math.min(deletion, insertion));
                // Transposition of two neighbouring characters, it's the most
                // common typo.
                if (i > 1 && j > 1 && cha1[i - 1] == cha2[j - 2] && cha1[i - 2] == cha2[j - 1]) {
                    distance = Math.min(distance, beforePreviousRow[j - 2] + 1);
                }
                actualRow[j] = distance;
            }
            int[] swap = beforePreviousRow;
            beforePreviousRow = previousRow;
            previousRow = actualRow;
            actualRow = swap;
        }

        return previousRow[cha2.length];
    }

}
