package org.javerland.txtcmpeval;

import static java.util.Objects.isNull;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
class BasicCommonProperties {

    /**
     * Constant for empty string.
     */
    public final static String EMPTY_STRING = "";

    /**
     * Adhering words order, default is true.
     */
    protected boolean adhereWordsOrder = true;
    /**
     * Removing accent from compared words.
     */
    protected boolean stripAccent = true;

    BasicCommonProperties() {
    }

    BasicCommonProperties(boolean stripAccent) {
        this.stripAccent = stripAccent;
    }

    BasicCommonProperties(boolean adhereWordsOrder, boolean stripAccent) {
        this.adhereWordsOrder = adhereWordsOrder;
        this.stripAccent = stripAccent;
    }

    String stripAccentIfNeeded(String str) {
        if (isNull(str)) {
            return null;
        }
        return stripAccent ? StringUtils.stripAccents(str) : str;
    }

    /**
     * Non-static equivalent of static method
     * {@link Comparison#calculateToPercentage(double)}.
     *
     * @param val Value from 0.0 to 1.0.
     * @return Calculated percentage value.
     */
    public int calcToPercentage(double val) {
        return Comparison.calculateToPercentage(val);
    }

}
