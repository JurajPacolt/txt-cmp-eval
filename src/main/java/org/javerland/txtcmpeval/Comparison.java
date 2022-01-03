package org.javerland.txtcmpeval;

/**
 * Basic interface for string comparing.
 * 
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public interface Comparison {
    
    /**
     * Percentage comparison of two strings.
     * 
     * @param str First string.
     * @param comparedStr String compared to.
     * @return Result from 0.0 to 1.0, if percentage is required, 
     *         is needed to multiply with 100.0 .
     */
    double compare(String str, String comparedStr);
    
}
