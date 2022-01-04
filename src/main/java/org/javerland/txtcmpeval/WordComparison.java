package org.javerland.txtcmpeval;

import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;

/**
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

    @Override
    public double compare(String word, String comparedWord) {

        // If're  both null result is 1.0 or 100%.
        if (isNull(word) && isNull(comparedWord)) {
            return 1.0d;
        }

        // First we need prepare strings.
        String w1 = nonNull(word) ? stripAccentIfNeeded(word) : EMPTY_STRING;
        String w2 = nonNull(word) ? stripAccentIfNeeded(comparedWord) : EMPTY_STRING;

        // TODO ... Now we need to stretch the strings to together length ...
        // It's not only about change the length. 
        // Equaling characters in strings must be moved to equals positions 
        // for better evaluation.

        //throw new UnsupportedOperationException("Not supported yet.");
        // FIXME ... result for development ...
        return 0d;
    }

}
