package org.javerland.txtcmpeval;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Juraj Pacolt (juraj.pacolt@gmail.com)
 * @since 03.01.2021
 */
public class TextComparisonTest {

    private final static double DELTA = 0.0001d;

    private final static String TEXT = "Dnes je pekný deň. Zajtra pôjdeme do lesa. Vrátime sa večer.";

    private final static String SHUFFLED_TEXT = "Vrátime sa večer. Dnes je pekný deň. Zajtra pôjdeme do lesa.";

    @Test
    public void testEqualTexts() {
        TextComparison comparison = new TextComparison();

        Assert.assertEquals(1.0d, comparison.compare(TEXT, TEXT), DELTA);
        Assert.assertEquals(100, comparison.compareToPercentage(TEXT, TEXT));
    }

    @Test
    public void testTextWithTyposAndWithoutAccent() {
        TextComparison comparison = new TextComparison();

        // Accent is stripped, so texts are equal.
        String withoutAccent = "Dnes je pekny den. Zajtra pojdeme do lesa. Vratime sa vecer.";
        Assert.assertEquals(1.0d, comparison.compare(TEXT, withoutAccent), DELTA);

        // Two typos in two words of eleven words.
        String withTypos = "Dnes je pekny dan. Zajtra pojdeme do lesa. Vratime sa vecar.";
        double result = comparison.compare(TEXT, withTypos);
        Assert.assertTrue(result > 0.9d);
        Assert.assertTrue(result < 1.0d);
    }

    @Test
    public void testShorterText() {
        TextComparison comparison = new TextComparison();

        // Only the first sentence, it means four words of eleven.
        Assert.assertEquals(4.0d / 11.0d, comparison.compare(TEXT, "Dnes je pekný deň."), DELTA);
    }

    @Test
    public void testAdhereWordsOrderFlag() {
        TextComparison adhering = new TextComparison(true, true, true);
        TextComparison notAdhering = new TextComparison(false, true, true);

        Assert.assertTrue(adhering.compare(TEXT, SHUFFLED_TEXT) < 0.8d);
        Assert.assertEquals(1.0d, notAdhering.compare(TEXT, SHUFFLED_TEXT), DELTA);
    }

    @Test
    public void testCompareBySentencesFlag() {
        TextComparison byWords = new TextComparison();
        TextComparison bySentences = new TextComparison(true, true, true, true, true);
        // Sentences are paired without regard to their position, words inside
        // of the sentence keep their order.
        TextComparison bySentencesWithoutOrder = new TextComparison(true, false, true, true, true);

        Assert.assertFalse(byWords.isCompareBySentences());
        Assert.assertTrue(bySentences.isCompareBySentences());

        Assert.assertTrue(byWords.compare(TEXT, SHUFFLED_TEXT) < 1.0d);
        Assert.assertTrue(bySentences.compare(TEXT, SHUFFLED_TEXT) < 1.0d);
        Assert.assertEquals(1.0d, bySentencesWithoutOrder.compare(TEXT, SHUFFLED_TEXT), DELTA);
    }

    /**
     * Longer text of three complex sentences, it's the example from README.
     */
    private final static String LONG_TEXT = "Knižnica na porovnávanie textov vyhodnotí zhodu dvoch reťazcov v percentách, "
            + "pretože pracuje so vzdialenosťou jednotlivých slov. "
            + "Ak sa v texte vyskytnú preklepy alebo prehodené písmená, výsledok klesne iba nepatrne, "
            + "keďže prehodenie dvoch susedných písmen sa počíta ako jedna chyba. "
            + "Poradie slov, diakritiku a veľkosť písmen je možné zapnúť alebo vypnúť pomocou príznakov.";

    /**
     * The same text without accent and with two typos, "prekelpy" with transposed
     * characters and "chzba" with a wrong key.
     */
    private final static String LONG_TEXT_WITH_TYPOS = "Kniznica na porovnavanie textov vyhodnoti zhodu dvoch retazcov v percentach, "
            + "pretoze pracuje so vzdialenostou jednotlivych slov. "
            + "Ak sa v texte vyskytnu prekelpy alebo prehodene pismena, vysledok klesne iba nepatrne, "
            + "kedze prehodenie dvoch susednych pismen sa pocita ako jedna chzba. "
            + "Poradie slov, diakritiku a velkost pismen je mozne zapnut alebo vypnut pomocou priznakov.";

    /**
     * The same sentences, but the last one is moved to the beginning.
     */
    private final static String LONG_TEXT_WITH_MOVED_SENTENCE = "Poradie slov, diakritiku a veľkosť písmen je možné zapnúť alebo vypnúť pomocou príznakov. "
            + "Knižnica na porovnávanie textov vyhodnotí zhodu dvoch reťazcov v percentách, "
            + "pretože pracuje so vzdialenosťou jednotlivých slov. "
            + "Ak sa v texte vyskytnú preklepy alebo prehodené písmená, výsledok klesne iba nepatrne, "
            + "keďže prehodenie dvoch susedných písmen sa počíta ako jedna chyba.";

    /**
     * Rewritten text, one sentence is missing and two words are changed.
     */
    private final static String LONG_TEXT_REWRITTEN = "Knižnica na porovnávanie textov vypočíta zhodu dvoch reťazcov v percentách, "
            + "pretože pracuje so vzdialenosťou jednotlivých slov. "
            + "Poradie slov, diakritiku aj veľkosť písmen je možné zapnúť alebo vypnúť pomocou príznakov.";

    private final static String LONG_TEXT_ABOUT_SOMETHING_ELSE = "Motor automobilu sa pokazil hneď po výjazde z dielne, "
            + "takže mechanik musel vymeniť celú prevodovku. "
            + "Oprava trvala tri dni a zákazník si vyzdvihol auto až v piatok.";

    @Test
    public void testLongerTextWithTypos() {
        TextComparison comparison = new TextComparison();

        // Two typos and missing accent in a text of 52 words.
        Assert.assertEquals(0.9937d, comparison.compare(LONG_TEXT, LONG_TEXT_WITH_TYPOS), 0.0001d);

        // The same text when accent is a difference.
        comparison.setStripAccent(false);
        Assert.assertEquals(0.9142d, comparison.compare(LONG_TEXT, LONG_TEXT_WITH_TYPOS), 0.0001d);
    }

    @Test
    public void testLongerTextWithMovedSentence() {
        Assert.assertEquals(0.75d, new TextComparison().compare(LONG_TEXT, LONG_TEXT_WITH_MOVED_SENTENCE), 0.0001d);

        // Moved sentence isn't a difference when words order isn't adhered.
        Assert.assertEquals(1.0d,
                new TextComparison(false, true, true).compare(LONG_TEXT, LONG_TEXT_WITH_MOVED_SENTENCE), DELTA);

        // The same result with pairing of whole sentences.
        Assert.assertEquals(1.0d, new TextComparison(true, false, true, true, true).compare(LONG_TEXT,
                LONG_TEXT_WITH_MOVED_SENTENCE), DELTA);
    }

    @Test
    public void testRewrittenAndDifferentLongerText() {
        TextComparison comparison = new TextComparison();

        // One sentence of three is missing, so a half of words stays unpaired.
        Assert.assertEquals(0.5374d, comparison.compare(LONG_TEXT, LONG_TEXT_REWRITTEN), 0.0001d);
        Assert.assertEquals(0.6423d, new TextComparison(true, false, true, true, true).compare(LONG_TEXT,
                LONG_TEXT_REWRITTEN), 0.0001d);

        // Only a few common words as "sa" or "v".
        Assert.assertEquals(0.1521d, comparison.compare(LONG_TEXT, LONG_TEXT_ABOUT_SOMETHING_ELSE), 0.0001d);
    }

    @Test
    public void testIgnoreCaseFlag() {
        TextComparison comparison = new TextComparison();
        Assert.assertEquals(1.0d, comparison.compare(TEXT, TEXT.toUpperCase()), DELTA);

        comparison.setIgnoreCase(false);
        Assert.assertTrue(comparison.compare(TEXT, TEXT.toUpperCase()) < 0.5d);
    }

    @Test
    public void testNullAndEmptyValues() {
        TextComparison comparison = new TextComparison();

        Assert.assertEquals(1.0d, comparison.compare(null, null), DELTA);
        Assert.assertEquals(1.0d, comparison.compare("", ""), DELTA);
        Assert.assertEquals(0.0d, comparison.compare(TEXT, null), DELTA);
        Assert.assertEquals(0.0d, comparison.compare(null, TEXT), DELTA);

        comparison.setCompareBySentences(true);
        Assert.assertEquals(1.0d, comparison.compare(null, null), DELTA);
        Assert.assertEquals(1.0d, comparison.compare("", " . "), DELTA);
        Assert.assertEquals(0.0d, comparison.compare(TEXT, ""), DELTA);
    }

    @Test
    public void testDifferentTexts() {
        TextComparison comparison = new TextComparison();

        Assert.assertTrue(comparison.compare(TEXT, "Motor auta je pokazený. Mechanik ho opraví.") < 0.4d);
    }

    @Test
    public void testSplittingToSentences() {
        Assert.assertArrayEquals(new String[] { "Prvá veta", "Druhá veta" },
                TextComparison.splitToSentences("Prvá veta. Druhá veta!"));
        Assert.assertArrayEquals(new String[] { "Prvá veta", "Druhá veta" },
                TextComparison.splitToSentences("Prvá veta\nDruhá veta\n"));
        Assert.assertEquals(0, TextComparison.splitToSentences("  ...  ").length);
        Assert.assertEquals(0, TextComparison.splitToSentences(null).length);
    }

}
