package org.javerland.txtcmpeval;

/**
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
class BasicCommonProperties {
    
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

}
