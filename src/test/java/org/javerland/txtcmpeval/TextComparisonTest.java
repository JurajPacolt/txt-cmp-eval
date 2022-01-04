package org.javerland.txtcmpeval;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public class TextComparisonTest
{

    @Test
    public void testWordComparison()
    {
        WordComparison wcWithAccent = new WordComparison(false);
        WordComparison wcWithoutAccent = new WordComparison(true);
        
        // TODO ... it's needed to solve ...
        String word1 = "abcdefgh";
        String word2 = "abcdefgh";
        
        double result = wcWithoutAccent.compare(word1, word2);
        System.out.println(">>> " + result);
        
        Assert.assertTrue(true);
    }

    @Test
    public void testSentenceComparison()
    {
        // TODO ... it's needed to solve ...
        Assert.assertTrue(true);
    }

    @Test
    public void testTextComparison()
    {
        // TODO ... it's needed to solve ...
        Assert.assertTrue(true);
    }

}
