# txt-cmp-eval

## Basic description

Library for text comparison to evaluation. Result of comparison is points or percentage evaluation of strings.

Texts are split to words and every word is compared with words of the compared text character by character, so
typos, missing or added letters decrease the result only a little. Difference of word order, accent (diacritics)
and letter case is controlled by flags, so the same pair of texts can be evaluated strictly or benevolently.

> __First important note:__
>
> It's simple library and it's don't know compare sense of words and sentences.

## How it works

1. __Words__ are compared by Damerau-Levenshtein distance, it means by count of characters which have to be
   changed, added, removed or transposed to make words equal. Result is `(length - distance) / length` of the
   longer word, so `mesiac` and `mseiac` (transposed characters) are similar at 83 %.
2. __Sentences and texts__ are split to words and every word is compared with every word of the compared side.
   Then the best pairing of words is found, every word can be used only once and words which stay unpaired
   decrease the result. Result is `sum of similarities of pairs / count of words of the longer side`.
3. When words order is adhered, pairs of words can't cross each other, so a moved word can't be paired twice.
   When it isn't adhered, the most similar pairs are taken first without regard to their position.

Result of every comparison is a value from `0.0` to `1.0` and it's symmetric, it means comparison of `a` with `b`
gives the same result as comparison of `b` with `a`.

## Typos

Transposition of two neighbouring characters is the most common typo, so it's counted as one typo only, not as
two changed characters. Values below are real results of `new WordComparison().compare(word, typo)`:

| Typo | Words | Result |
| --- | --- | --- |
| Transposed characters | `mesiac` / `mseiac` | 0.8333 (83 %) |
| Transposed characters in a longer word | `programovanie` / `porgramovanie` | 0.9231 (92 %) |
| Doubled character | `kniznica` / `knizznica` | 0.8889 (88 %) |
| Missing character | `kniznica` / `knznica` | 0.875 (87 %) |
| Added character | `kniznica` / `kniznicaa` | 0.8889 (88 %) |
| Wrong key | `kniznica` / `knuznica` | 0.875 (87 %) |
| Two typos | `kniznica` / `knzunica` | 0.75 (75 %) |
| Nothing common | `kniznica` / `automobil` | 0.0 (0 %) |

Typos are evaluated in the same way inside of sentences and texts, because sentences and texts are compared word
by word. One typo in one word of four decreases the result of the whole sentence to 95 % only.

## Flags

| Flag | Default | Description |
| --- | --- | --- |
| `adhereWordsOrder` | `true` | When it's `false`, shuffled words of the compared text are paired too. |
| `stripAccent` | `true` | When it's `true`, `mesiacik` and `mesiačik` are equal. |
| `ignoreCase` | `true` | When it's `true`, `Slovo` and `slovo` are equal. |
| `compareBySentences` | `false` | Only `TextComparison`, when it's `true`, text is split to sentences first and words are paired only inside of paired sentences. |
| `adhereSentencesOrder` | `true` | Only `TextComparison` with `compareBySentences`, when it's `false`, shuffled sentences are paired too. |

Every flag can be set by constructor or by its setter.

## Maven dependency

	<dependency>
	  <groupId>org.javerland</groupId>
	  <artifactId>txt-cmp-eval</artifactId>
	  <version>26.3.4</version>
	</dependency>

## Examples of using

### Comparison of two words

	WordComparison comparison = new WordComparison();

	comparison.compare("mesiac", "mesiac");           // 1.0    ... equal words
	comparison.compare("mesiac", "mseiac");           // 0.8333 ... one typo, transposed characters
	comparison.compare("auto", "atto");               // 0.75   ... one changed character
	comparison.compare("text", "texts");              // 0.8    ... one added character
	comparison.compare("auto", "vlak");               // 0.0    ... nothing common

Percentage value is available directly:

	comparison.compareToPercentage("mesiac", "mseiac");             // 83
	Comparison.calculateToPercentage(comparison.compare("a", "a")); // 100

### Accent and letter case

	// new WordComparison(stripAccent)
	new WordComparison(true).compare("mesiačik", "mesiacik");   // 1.0   ... accent is stripped
	new WordComparison(false).compare("mesiačik", "mesiacik");  // 0.875 ... accent is a difference

	// new WordComparison(stripAccent, ignoreCase)
	new WordComparison(true, true).compare("Slovo", "slovo");   // 1.0   ... letter case is ignored
	new WordComparison(true, false).compare("Slovo", "slovo");  // 0.8   ... letter case is a difference

### Comparison of two sentences

	SentenceComparison comparison = new SentenceComparison();

	comparison.compare("Dnes je pekny den", "Dnes je pekny den");   // 1.0  ... equal sentences
	comparison.compare("Dnes je pekny den", "Dnes je pkeny den");   // 0.95 ... one typo in one word of four
	comparison.compare("Dnes je pekny den", "Dnes je pekny");       // 0.75 ... three words of four are paired

Punctuation and multiple spaces aren't a part of the comparison, so `Dnes je pekny den!` and `Dnes  je pekny den`
are equal.

### Word order

	// new SentenceComparison(adhereWordsOrder, stripAccent)
	SentenceComparison adhering = new SentenceComparison(true, true);
	SentenceComparison notAdhering = new SentenceComparison(false, true);

	adhering.compare("pes hryzie macku", "macku hryzie pes");     // 0.3333 ... only the middle word is paired
	notAdhering.compare("pes hryzie macku", "macku hryzie pes");  // 1.0    ... all words are paired

### Comparison of two texts

	TextComparison comparison = new TextComparison();

	String text = "Dnes je pekny den. Zajtra pojdeme do lesa.";
	String textWithTypo = "Dnes je pkeny den. Zajtra pojdeme do lesa.";
	String shuffledText = "Zajtra pojdeme do lesa. Dnes je pekny den.";

	comparison.compare(text, text);           // 1.0   ... equal texts
	comparison.compare(text, textWithTypo);   // 0.975 ... one typo in one word of eight
	comparison.compare(text, shuffledText);   // 0.5   ... sentences are swapped, words order is adhered

	// new TextComparison(adhereWordsOrder, stripAccent, ignoreCase)
	new TextComparison(false, true, true).compare(text, shuffledText);  // 1.0 ... order isn't adhered

Text can be compared sentence by sentence too, then words of one sentence can't be paired with words of a
different sentence:

	// new TextComparison(compareBySentences, adhereSentencesOrder, adhereWordsOrder, stripAccent, ignoreCase)
	TextComparison bySentences = new TextComparison(true, false, true, true, true);

	bySentences.compare(text, shuffledText);  // 1.0 ... whole sentences are paired, only their order is changed

### Comparison of a longer text

Three complex sentences, 52 words together:

> Knižnica na porovnávanie textov vyhodnotí zhodu dvoch reťazcov v percentách, pretože pracuje so vzdialenosťou
> jednotlivých slov. Ak sa v texte vyskytnú preklepy alebo prehodené písmená, výsledok klesne iba nepatrne,
> keďže prehodenie dvoch susedných písmen sa počíta ako jedna chyba. Poradie slov, diakritiku a veľkosť písmen
> je možné zapnúť alebo vypnúť pomocou príznakov.

The same text is compared with four modifications of it. The first one is written without accent and it has two
typos, `prekelpy` with transposed characters and `chzba` with a wrong key:

> Kniznica na porovnavanie textov vyhodnoti zhodu dvoch retazcov v percentach, pretoze pracuje so vzdialenostou
> jednotlivych slov. Ak sa v texte vyskytnu __prekelpy__ alebo prehodene pismena, vysledok klesne iba nepatrne,
> kedze prehodenie dvoch susednych pismen sa pocita ako jedna __chzba__. Poradie slov, diakritiku a velkost
> pismen je mozne zapnut alebo vypnut pomocou priznakov.

The second one has the same sentences, but the last one is moved to the beginning. The third one is rewritten,
one sentence is missing and two words are changed (`vypočíta` instead of `vyhodnotí`, `aj` instead of `a`):

> Knižnica na porovnávanie textov __vypočíta__ zhodu dvoch reťazcov v percentách, pretože pracuje so
> vzdialenosťou jednotlivých slov. Poradie slov, diakritiku __aj__ veľkosť písmen je možné zapnúť alebo vypnúť
> pomocou príznakov.

The last one is about something completely different:

> Motor automobilu sa pokazil hneď po výjazde z dielne, takže mechanik musel vymeniť celú prevodovku. Oprava
> trvala tri dni a zákazník si vyzdvihol auto až v piatok.

Results of the comparison:

| Compared text | Flags | Result |
| --- | --- | --- |
| Without accent, two typos | default | 0.9937 (99 %) |
| Without accent, two typos | `stripAccent = false` | 0.9142 (91 %) |
| Moved last sentence | default | 0.75 (75 %) |
| Moved last sentence | `adhereWordsOrder = false` | 1.0 (100 %) |
| Moved last sentence | `compareBySentences = true`, `adhereSentencesOrder = false` | 1.0 (100 %) |
| Rewritten, one sentence missing | default | 0.5374 (53 %) |
| Rewritten, one sentence missing | `compareBySentences = true`, `adhereSentencesOrder = false` | 0.6423 (64 %) |
| Different text | default | 0.1521 (15 %) |

	TextComparison comparison = new TextComparison();
	comparison.compare(text, textWithTypos);                            // 0.9937 ... accent is stripped, two typos
	comparison.compare(text, movedSentenceText);                        // 0.75   ... words order is adhered
	comparison.compare(text, differentText);                            // 0.1521 ... only a few common words

	// Moved sentence isn't a difference when words order isn't adhered.
	new TextComparison(false, true, true).compare(text, movedSentenceText);          // 1.0

	// The same result with pairing of whole sentences.
	new TextComparison(true, false, true, true, true).compare(text, movedSentenceText); // 1.0

Two typos and missing accent in a text of 52 words decrease the result by less than one percent, but a missing
sentence and two changed words decrease it to a half, because a half of the words stays unpaired.

### Setting of flags after creating

	TextComparison comparison = new TextComparison();
	comparison.setAdhereWordsOrder(false);
	comparison.setStripAccent(false);
	comparison.setIgnoreCase(false);
	comparison.setCompareBySentences(true);
	comparison.setAdhereSentencesOrder(false);

### Null and empty values

	WordComparison comparison = new WordComparison();

	comparison.compare(null, null);       // 1.0 ... both sides are nothing, so they are equal
	comparison.compare("", "   ");        // 1.0 ... both sides are without any word
	comparison.compare("slovo", null);    // 0.0 ... there is nothing to compare
	comparison.compare("slovo", "");      // 0.0 ... there is nothing to compare

### Command line

Class `TextComparison` has an entry point for comparing of two texts or two files:

	java -cp txt-cmp-eval-26.3.4.jar;commons-lang3-3.20.0.jar org.javerland.txtcmpeval.TextComparison first.txt second.txt

	97 % (0.975)

Arguments which aren't paths to existing files are compared as texts:

	java -cp txt-cmp-eval-26.3.4.jar;commons-lang3-3.20.0.jar org.javerland.txtcmpeval.TextComparison "Dnes je pekny den" "Dnes je pkeny den"

	95 % (0.95)

## Performance

Every word of the text is compared with every word of the compared text, so time and memory of the comparison
grow with the product of counts of their words. It's fine for words, sentences and shorter texts, for longer
texts it's better to compare them sentence by sentence (`compareBySentences`), because words are paired inside
of the paired sentences only.

## Build

	mvn clean install

Signing and releasing to the central repository needs a GPG key and credentials of the `ossrh` server, so it's
a part of the `release` profile only:

	mvn clean deploy -Prelease

## License

GNU General Public License (GPL), Version 3
