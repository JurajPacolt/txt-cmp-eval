package org.javerland.txtcmpeval;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public class WordComparisonTest {

    private final static double DELTA = 0.0001d;

    @Test
    public void testEqualWords() {
        WordComparison comparison = new WordComparison();

        Assert.assertEquals(1.0d, comparison.compare("abcdefgh", "abcdefgh"), DELTA);
        Assert.assertEquals(100, comparison.compareToPercentage("abcdefgh", "abcdefgh"));
    }

    @Test
    public void testTypos() {
        WordComparison comparison = new WordComparison();

        // One changed character of four.
        Assert.assertEquals(0.75d, comparison.compare("auto", "atto"), DELTA);
        // One missing character of ten.
        Assert.assertEquals(0.9d, comparison.compare("komparacia", "kompracia"), DELTA);
        // One added character.
        Assert.assertEquals(0.8d, comparison.compare("text", "texts"), DELTA);
        // Transposition of two neighbouring characters is one typo of ten characters.
        Assert.assertEquals(0.9d, comparison.compare("porovnanie", "poronvanie"), DELTA);
        // Completely different words.
        Assert.assertTrue(comparison.compare("auto", "vlak") < 0.3d);
    }

    @Test
    public void testTransposedCharacters() {
        WordComparison comparison = new WordComparison();

        // Transposition of two neighbouring characters is one typo only, it isn't
        // counted as two changed characters.
        Assert.assertEquals(5.0d / 6.0d, comparison.compare("mesiac", "mseiac"), DELTA);
        Assert.assertEquals(7.0d / 8.0d, comparison.compare("kniznica", "knizncia"), DELTA);
        Assert.assertEquals(12.0d / 13.0d, comparison.compare("programovanie", "porgramovanie"), DELTA);
        Assert.assertEquals(9.0d / 10.0d, comparison.compare("porovnanie", "poronvanie"), DELTA);

        Assert.assertEquals(1, WordComparison.calculateDistance("mesiac".toCharArray(), "mseiac".toCharArray()));
        Assert.assertEquals(1, WordComparison.calculateDistance("kniznica".toCharArray(), "knizncia".toCharArray()));
        Assert.assertEquals(2, WordComparison.calculateDistance("kniznica".toCharArray(), "knzunica".toCharArray()));
    }

    @Test
    public void testDoubledMissingAndAddedCharacters() {
        WordComparison comparison = new WordComparison();

        // Doubled character.
        Assert.assertEquals(8.0d / 9.0d, comparison.compare("kniznica", "knizznica"), DELTA);
        // Missing character.
        Assert.assertEquals(7.0d / 8.0d, comparison.compare("kniznica", "knznica"), DELTA);
        // Added character.
        Assert.assertEquals(8.0d / 9.0d, comparison.compare("kniznica", "kniznicaa"), DELTA);
        // Wrong key.
        Assert.assertEquals(7.0d / 8.0d, comparison.compare("kniznica", "knuznica"), DELTA);
    }

    @Test
    public void testStripAccentFlag() {
        WordComparison withStripping = new WordComparison(true);
        WordComparison withoutStripping = new WordComparison(false);

        Assert.assertEquals(1.0d, withStripping.compare("mesiačik", "mesiacik"), DELTA);
        // One character of eight is different.
        Assert.assertEquals(0.875d, withoutStripping.compare("mesiačik", "mesiacik"), DELTA);
    }

    @Test
    public void testIgnoreCaseFlag() {
        WordComparison ignoringCase = new WordComparison(true, true);
        WordComparison respectingCase = new WordComparison(true, false);

        Assert.assertEquals(1.0d, ignoringCase.compare("Slovo", "slovo"), DELTA);
        Assert.assertEquals(0.8d, respectingCase.compare("Slovo", "slovo"), DELTA);
    }

    @Test
    public void testFlagsBySetters() {
        WordComparison comparison = new WordComparison();
        Assert.assertTrue(comparison.isStripAccent());
        Assert.assertTrue(comparison.isIgnoreCase());

        comparison.setStripAccent(false);
        comparison.setIgnoreCase(false);
        // Only the first character is different, word has seven characters.
        Assert.assertEquals(6.0d / 7.0d, comparison.compare("Čerešňa", "cerešňa"), DELTA);

        comparison.setStripAccent(true);
        comparison.setIgnoreCase(true);
        Assert.assertEquals(1.0d, comparison.compare("Čerešňa", "cerešňa"), DELTA);
    }

    @Test
    public void testNullAndEmptyValues() {
        WordComparison comparison = new WordComparison();

        Assert.assertEquals(1.0d, comparison.compare(null, null), DELTA);
        Assert.assertEquals(1.0d, comparison.compare("", ""), DELTA);
        Assert.assertEquals(1.0d, comparison.compare(null, ""), DELTA);
        Assert.assertEquals(1.0d, comparison.compare("   ", ""), DELTA);
        Assert.assertEquals(0.0d, comparison.compare("slovo", null), DELTA);
        Assert.assertEquals(0.0d, comparison.compare(null, "slovo"), DELTA);
        Assert.assertEquals(0.0d, comparison.compare("slovo", ""), DELTA);
    }

    @Test
    public void testSymmetryOfComparison() {
        WordComparison comparison = new WordComparison();

        Assert.assertEquals(comparison.compare("porovnanie", "porovnavanie"),
                comparison.compare("porovnavanie", "porovnanie"), DELTA);
    }

    @Test
    public void testDistanceCalculation() {
        Assert.assertEquals(0, WordComparison.calculateDistance("kniha".toCharArray(), "kniha".toCharArray()));
        Assert.assertEquals(1, WordComparison.calculateDistance("kniha".toCharArray(), "knihy".toCharArray()));
        Assert.assertEquals(3, WordComparison.calculateDistance("kitten".toCharArray(), "sitting".toCharArray()));
        Assert.assertEquals(1, WordComparison.calculateDistance("slovo".toCharArray(), "solvo".toCharArray()));
        Assert.assertEquals(5, WordComparison.calculateDistance("".toCharArray(), "slovo".toCharArray()));
    }

    @Test
    public void testCalculationToPercentage() {
        Assert.assertEquals(0, Comparison.calculateToPercentage(0.0d));
        Assert.assertEquals(75, Comparison.calculateToPercentage(0.75d));
        Assert.assertEquals(100, Comparison.calculateToPercentage(1.0d));
    }

}
