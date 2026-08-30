package org.javerland.txtcmpeval;

import static java.util.Objects.isNull;
import java.util.Locale;
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
    /**
     * Ignoring letter case of compared words, default is true.
     */
    protected boolean ignoreCase = true;

    BasicCommonProperties() {
    }

    BasicCommonProperties(boolean stripAccent) {
        this.stripAccent = stripAccent;
    }

    BasicCommonProperties(boolean adhereWordsOrder, boolean stripAccent) {
        this.adhereWordsOrder = adhereWordsOrder;
        this.stripAccent = stripAccent;
    }

    BasicCommonProperties(boolean adhereWordsOrder, boolean stripAccent, boolean ignoreCase) {
        this.adhereWordsOrder = adhereWordsOrder;
        this.stripAccent = stripAccent;
        this.ignoreCase = ignoreCase;
    }

    String stripAccentIfNeeded(String str) {
        if (isNull(str)) {
            return null;
        }
        return stripAccent ? StringUtils.stripAccents(str) : str;
    }

    String toLowerCaseIfNeeded(String str) {
        if (isNull(str)) {
            return null;
        }
        return ignoreCase ? str.toLowerCase(Locale.ROOT) : str;
    }

    /**
     * Prepares string for comparison, it means trimming, removing of accent and
     * lowering of letter case, both by actual settings.
     *
     * @param str String to prepare.
     * @return Prepared string or <code>null</code> for <code>null</code> input.
     */
    String normalize(String str) {
        if (isNull(str)) {
            return null;
        }
        return toLowerCaseIfNeeded(stripAccentIfNeeded(str)).trim();
    }

    /**
     * Is words order taken into account?
     *
     * @return <code>true</code> when words order is adhered.
     */
    public boolean isAdhereWordsOrder() {
        return adhereWordsOrder;
    }

    /**
     * Sets if words order has to be taken into account. When it's
     * <code>false</code>, words of compared sentences are paired by their
     * similarity without regard to their position.
     *
     * @param adhereWordsOrder Adhering words order.
     */
    public void setAdhereWordsOrder(boolean adhereWordsOrder) {
        this.adhereWordsOrder = adhereWordsOrder;
    }

    /**
     * Is accent removed before comparison?
     *
     * @return <code>true</code> when accent is removed.
     */
    public boolean isStripAccent() {
        return stripAccent;
    }

    /**
     * Sets if accent has to be removed before comparison. When it's
     * <code>true</code>, words as <i>"mesiac"</i> and <i>"mesiac"</i> with
     * accent are equal.
     *
     * @param stripAccent Removing accent.
     */
    public void setStripAccent(boolean stripAccent) {
        this.stripAccent = stripAccent;
    }

    /**
     * Is letter case ignored during comparison?
     *
     * @return <code>true</code> when letter case is ignored.
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * Sets if letter case has to be ignored during comparison. When it's
     * <code>true</code>, words <i>"Word"</i> and <i>"word"</i> are equal.
     *
     * @param ignoreCase Ignoring of letter case.
     */
    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
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
