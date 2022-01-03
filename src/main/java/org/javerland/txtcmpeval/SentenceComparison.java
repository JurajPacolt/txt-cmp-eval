package org.javerland.txtcmpeval;

/**
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public class SentenceComparison extends BasicCommonProperties implements Comparison {

    public SentenceComparison() {
        super();
    }

    public SentenceComparison(boolean adhereWordsOrder, boolean stripAccent) {
        super(adhereWordsOrder, stripAccent);
    }

    @Override
    public double compare(String word, String comparedWord) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
