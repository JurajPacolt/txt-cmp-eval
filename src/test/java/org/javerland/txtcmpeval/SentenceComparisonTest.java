package org.javerland.txtcmpeval;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public class SentenceComparisonTest {

    private final static double DELTA = 0.0001d;

    @Test
    public void testEqualSentences() {
        SentenceComparison comparison = new SentenceComparison();

        Assert.assertEquals(1.0d, comparison.compare("Dnes je pekný deň", "Dnes je pekný deň"), DELTA);
        // Punctuation isn't a part of the comparison.
        Assert.assertEquals(1.0d, comparison.compare("Dnes je pekný deň!", "Dnes  je pekný deň"), DELTA);
    }

    @Test
    public void testMissingWord() {
        SentenceComparison comparison = new SentenceComparison();

        // Three words of four are paired.
        Assert.assertEquals(0.75d, comparison.compare("Dnes je pekný deň", "Dnes je pekný"), DELTA);
    }

    @Test
    public void testTypoInSentence() {
        SentenceComparison comparison = new SentenceComparison();

        // Three words are equal, the fourth one has one typo of three characters.
        Assert.assertEquals((3.0d + 2.0d / 3.0d) / 4.0d, comparison.compare("Dnes je pekny den", "Dnes je pekny dan"),
                DELTA);
    }

    @Test
    public void testTransposedCharactersInSentence() {
        SentenceComparison comparison = new SentenceComparison();

        // Three words are equal, the fourth one has transposed characters, it means
        // one typo of five characters.
        Assert.assertEquals((3.0d + 0.8d) / 4.0d, comparison.compare("Dnes je pekny den", "Dnes je pkeny den"),
                DELTA);
    }

    @Test
    public void testAdhereWordsOrderFlag() {
        SentenceComparison adhering = new SentenceComparison(true, true);
        SentenceComparison notAdhering = new SentenceComparison(false, true);

        String sentence = "pes hryzie mačku";
        String comparedSentence = "mačku hryzie pes";

        // Only the middle word can be paired without crossing of pairs.
        Assert.assertTrue(adhering.compare(sentence, comparedSentence) < 0.7d);
        // Words are paired without regard to their position.
        Assert.assertEquals(1.0d, notAdhering.compare(sentence, comparedSentence), DELTA);
    }

    @Test
    public void testStripAccentFlag() {
        SentenceComparison withStripping = new SentenceComparison(true, true);
        SentenceComparison withoutStripping = new SentenceComparison(true, false);

        Assert.assertEquals(1.0d, withStripping.compare("Zajtra bude pršať", "Zajtra bude prsat"), DELTA);
        Assert.assertTrue(withoutStripping.compare("Zajtra bude pršať", "Zajtra bude prsat") < 1.0d);
    }

    @Test
    public void testIgnoreCaseFlag() {
        SentenceComparison ignoringCase = new SentenceComparison(true, true, true);
        SentenceComparison respectingCase = new SentenceComparison(true, true, false);

        Assert.assertEquals(1.0d, ignoringCase.compare("PRVA VETA", "prva veta"), DELTA);
        Assert.assertTrue(respectingCase.compare("PRVA VETA", "prva veta") < 0.5d);
    }

    @Test
    public void testNullAndEmptyValues() {
        SentenceComparison comparison = new SentenceComparison();

        Assert.assertEquals(1.0d, comparison.compare(null, null), DELTA);
        Assert.assertEquals(1.0d, comparison.compare("", "   "), DELTA);
        Assert.assertEquals(1.0d, comparison.compare(" ... ", null), DELTA);
        Assert.assertEquals(0.0d, comparison.compare("Nejaká veta.", null), DELTA);
        Assert.assertEquals(0.0d, comparison.compare("", "Nejaká veta."), DELTA);
    }

    @Test
    public void testDifferentSentences() {
        SentenceComparison comparison = new SentenceComparison();

        Assert.assertTrue(comparison.compare("Vlak mešká na trati", "Zajtra pôjdem do lesa") < 0.4d);
    }

    @Test
    public void testSplittingToWords() {
        Assert.assertArrayEquals(new String[] { "Dnes", "je", "pekný", "deň" },
                SentenceComparison.splitToWords("  Dnes, je pekný deň!  "));
        Assert.assertArrayEquals(new String[] { "veta" }, SentenceComparison.splitToWords("...veta..."));
        Assert.assertEquals(0, SentenceComparison.splitToWords(" .,- ").length);
        Assert.assertEquals(0, SentenceComparison.splitToWords(null).length);
    }

}
