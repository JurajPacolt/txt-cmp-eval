package org.javerland.txtcmpeval;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
