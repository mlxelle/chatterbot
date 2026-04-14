package org.cis1200;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for MarkovChain */
public class MarkovChainTest {

    /**
     * Helper function to make it easier to create singleton sets of Strings;
     * use this function in your tests as needed.
     *
     * @param s - the String to add to the set
     * @return - a Set containing just s
     */
    private static Set<String> singleton(String s) {
        Set<String> set = new TreeSet<>();
        set.add(s);
        return set;
    }

    /*
     * ILLUSTRATIVE EXAMPLE MARKOV CHAIN trained on:
     *   "a table and a chair"
     *   "a banana! and a banana?"
     *
     * startTokens: { "a":2 }
     * bigramFrequencies:
     * "!": { "and":1 }
     * "?": { "<END>":1 }
     * "a": { "banana":2 "chair":1 "table":1 }
     * "and": { "a":2 }
     * "banana": { "!":1 "?":1 }
     * "chair": { "<END>":1 }
     * "table": { "and":1 }
     */
    @Test
    public void testIllustrativeExampleMarkovChain() {
        /*
         * Note: we provide the pre-parsed sequence of tokens.
         */
        String[] post1 = { "a", "table", "and", "a", "chair" };
        String[] post2 = { "a", "banana", "!", "and", "a", "banana", "?" };

        MarkovChain mc = new MarkovChain();
        mc.addSequence(Arrays.stream(post1).iterator());
        mc.addSequence(Arrays.stream(post2).iterator());

        // Print out the Markov chain
        System.out.println("ILLUSTRATIVE EXAMPLE MARKOV CHAIN:\n" + mc);

        ProbabilityDistribution<String> pdBang = mc.get("!");
        assertEquals(singleton("and"), pdBang.keySet());
        assertEquals(1, pdBang.count("and"));

        ProbabilityDistribution<String> pdQuestion = mc.get("?");
        assertEquals(singleton(MarkovChain.END_TOKEN), pdQuestion.keySet());
        assertEquals(1, pdQuestion.count(MarkovChain.END_TOKEN));

        assertEquals(2, mc.startTokens.getTotal());
        assertEquals(2, mc.startTokens.count("a"));
        ProbabilityDistribution<String> pdA = mc.get("a");
        Set<String> keysA = new TreeSet<>();
        keysA.add("banana");
        keysA.add("chair");
        keysA.add("table");
        assertEquals(keysA, pdA.keySet());
        assertEquals(2, pdA.count("banana"));
        assertEquals(1, pdA.count("chair"));
        assertEquals(1, pdA.count("table"));

        ProbabilityDistribution<String> pdAnd = mc.get("and");
        assertEquals(singleton("a"), pdAnd.keySet());
        assertEquals(2, pdAnd.count("a"));

        ProbabilityDistribution<String> pdBanana = mc.get("banana");
        Set<String> keysBanana = new TreeSet<>();
        keysBanana.add("!");
        keysBanana.add("?");
        assertEquals(keysBanana, pdBanana.keySet());
        assertEquals(1, pdBanana.count("!"));
        assertEquals(1, pdBanana.count("?"));

        ProbabilityDistribution<String> pdChair = mc.get("chair");
        assertEquals(singleton(MarkovChain.END_TOKEN), pdChair.keySet());
        assertEquals(1, pdChair.count(MarkovChain.END_TOKEN));

        ProbabilityDistribution<String> pdTable = mc.get("table");
        assertEquals(singleton("and"), pdTable.keySet());
        assertEquals(1, pdTable.count("and"));
    }

    // addBigram tests

    @Test
    public void testAddBigram() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("1", "2");
        assertTrue(mc.bigramFrequencies.containsKey("1"));
        ProbabilityDistribution<String> pd = mc.bigramFrequencies.get("1");
        assertTrue(pd.getRecords().containsKey("2"));
        assertEquals(1, pd.count("2"));
    }

    @Test
    public void testAddBigramNull() {
        MarkovChain mc = new MarkovChain();
        assertThrows(IllegalArgumentException.class, () -> mc.addBigram(null, null));
    }

    @Test
    public void testAddBigramSamePairTwice() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("a", "b");
        mc.addBigram("a", "b");
        assertEquals(2, mc.bigramFrequencies.get("a").count("b"));
    }

    @Test
    public void testAddBigramSameFirstDifferentSecond() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("a", "b");
        mc.addBigram("a", "c");
        assertEquals(1, mc.bigramFrequencies.get("a").count("b"));
        assertEquals(1, mc.bigramFrequencies.get("a").count("c"));
    }

    // addSequence tests

    @Test
    public void testAddSequence() {
        MarkovChain mc = new MarkovChain();
        String sentence = "1 2 3";
        mc.addSequence(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(3, mc.bigramFrequencies.size());
        ProbabilityDistribution<String> pd1 = mc.bigramFrequencies.get("1");
        assertTrue(pd1.getRecords().containsKey("2"));
        assertEquals(1, pd1.count("2"));
        ProbabilityDistribution<String> pd2 = mc.bigramFrequencies.get("2");
        assertTrue(pd2.getRecords().containsKey("3"));
        assertEquals(1, pd2.count("3"));
        ProbabilityDistribution<String> pd3 = mc.bigramFrequencies.get("3");
        assertTrue(pd3.getRecords().containsKey(MarkovChain.END_TOKEN));
        assertEquals(1, pd3.count(MarkovChain.END_TOKEN));
    }

    @Test
    public void testAddSequenceNull() {
        MarkovChain mc = new MarkovChain();
        assertThrows(IllegalArgumentException.class, () -> mc.addSequence(null));
    }

    @Test
    public void testAddSequenceEmpty() {
        MarkovChain mc = new MarkovChain();
        List<String> empty = new LinkedList<>();
        mc.addSequence(empty.iterator());
        assertEquals(0, mc.bigramFrequencies.size());
        assertEquals(0, mc.startTokens.getTotal());
    }

    @Test
    public void testAddSequenceSingleToken() {
        MarkovChain mc = new MarkovChain();
        List<String> single = new LinkedList<>();
        single.add("hello");
        mc.addSequence(single.iterator());
        assertEquals(1, mc.startTokens.count("hello"));
        assertEquals(1, mc.bigramFrequencies.get("hello").count(MarkovChain.END_TOKEN));
    }

    @Test
    public void testTwoAddSequence() {
        MarkovChain mc = new MarkovChain();
        mc.addSequence(Arrays.stream("hello world".split(" ")).iterator());
        mc.addSequence(Arrays.stream("hello there".split(" ")).iterator());
        assertEquals(2, mc.startTokens.count("hello"));
    }

    // getWalk / walk tests

    @Test
    public void testWalk() {
        /*
         * Using the training data "the cat sat" and "the cat ate the rat",
         * we put some bigrams into the Markov Chain.
         *
         * The given sequence of numbers acts as a path through the Markov Model
         * that should be followed by {@code walk}. Note that the sequence
         * includes a choice for {@code END_TOKEN}, so the length is one longer
         * than the {@code expectedTokens}.
         */

        String[] expectedTokens = { "the", "cat", "ate", "the", "cat", "sat" };
        MarkovChain mc = new MarkovChain();

        String sentence1 = "the cat sat";
        String sentence2 = "the cat ate the rat";
        mc.addSequence(Arrays.stream(sentence1.split(" ")).iterator());
        mc.addSequence(Arrays.stream(sentence2.split(" ")).iterator());

        // it can be illustrative to print out the state of the Markov Chain at this
        // point
        System.out.println(mc);

        // seq drives the walk: start→the, the→cat, cat→ate, ate→the, the→cat, cat→sat, sat→END
        Integer[] seq = { 0, 0, 0, 0, 0, 1, 0 };
        List<Integer> choices = Arrays.asList(seq);

        // ListNumberGenerator will skip over out of bounds values instead of throwing
        // an exception
        ListNumberGenerator listng = new ListNumberGenerator(choices);
        Iterator<String> walk = mc.getWalk(listng);

        for (String token : expectedTokens) {
            assertTrue(walk.hasNext());
            assertEquals(token, walk.next());
        }
        assertFalse(walk.hasNext());

    }

    @Test
    public void testWalk2() {
        /* Uses findWalkChoices to derive the number sequence for a given path */

        String[] expectedWords = { "the", "rat" };
        MarkovChain mc = new MarkovChain();

        String sentence1 = "the cat sat";
        String sentence2 = "the cat ate the rat";
        mc.addSequence(Arrays.stream(sentence1.split(" ")).iterator());
        mc.addSequence(Arrays.stream(sentence2.split(" ")).iterator());

        List<Integer> choices = mc.findWalkChoices(new LinkedList<>(Arrays.asList(expectedWords)));

        // ListNumberGenerator will skip over out of bounds values instead of throwing
        // an exception
        ListNumberGenerator listng = new ListNumberGenerator(choices);
        Iterator<String> walk = mc.getWalk(listng);

        for (String word : expectedWords) {
            assertTrue(walk.hasNext());
            assertEquals(word, walk.next());
        }

    }

    @Test
    public void testWalkEmptyChain() {
        MarkovChain mc = new MarkovChain();
        Iterator<String> walk = mc.getWalk(new ListNumberGenerator(new int[] { 0 }));
        assertFalse(walk.hasNext());
    }

    @Test
    public void testWalkSingleToken() {
        MarkovChain mc = new MarkovChain();
        mc.addSequence(singleton("hello").iterator());

        int[] nums = { 0, 0 };
        Iterator<String> walk = mc.getWalk(new ListNumberGenerator(nums));
        assertTrue(walk.hasNext());
        assertEquals("hello", walk.next());
        assertFalse(walk.hasNext());
    }

    @Test
    public void testConstructorWithTrainingData() {
        List<List<String>> trainingData = new LinkedList<>();
        trainingData.add(Arrays.asList("a", "b"));
        trainingData.add(Arrays.asList("a", "c"));
        MarkovChain mc = new MarkovChain(trainingData);
        assertEquals(2, mc.startTokens.count("a"));
        assertEquals(1, mc.bigramFrequencies.get("a").count("b"));
        assertEquals(1, mc.bigramFrequencies.get("a").count("c"));
    }

    @Test
    public void testWalkNextAfterEnd() {
        MarkovChain mc = new MarkovChain();
        mc.addSequence(singleton("hello").iterator());
        int[] nums = { 0, 0 };
        Iterator<String> walk = mc.getWalk(new ListNumberGenerator(nums));
        walk.next(); // "hello"
        assertFalse(walk.hasNext());
        assertThrows(NoSuchElementException.class, () -> walk.next());
    }

}
