package org.cis1200;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Tests for ChatterBot class */
public class ChatterBotTest {
    // A helper function for creating lists of strings
    private static List<String> listOfArray(String[] words) {
        List<String> l = new LinkedList<>();
        Collections.addAll(l, words);
        return l;
    }

    private static final String[] POST_1 = { "a", "table", "and", "a", "chair" };
    private static final String[] POST_2 = { "a", "banana", "!", "and", "a", "banana", "?" };

    private static List<List<String>> getTestTrainingDataExample() {
        List<List<String>> trainingData = new LinkedList<>();
        trainingData.add(listOfArray(POST_1));
        trainingData.add(listOfArray(POST_2));
        return trainingData;
    }

    @Test
    public void generatorWorks() {
        ChatterBot cb = new ChatterBot(getTestTrainingDataExample());

        String expected = "a banana?";
        int[] walk = { 0, 0, 1, 0 };
        NumberGenerator ng = new ListNumberGenerator(walk);
        assertEquals(expected, cb.generatePost(ng));
    }

    /**
     * An empty training set should produce an empty MarkovChain, generate an
     * empty post, and never throw or loop infinitely.
     */
    @Test
    public void emptyTrainingDataCreatesEmptyPost() {
        // Checks that your program does not go into an infinite loop
        assertTimeoutPreemptively(
                Duration.ofSeconds(10), () -> {
                    // No exceptions are thrown if training data is empty
                    ChatterBot cb = new ChatterBot(new LinkedList<>());
                    // Checks that the bot creates an empty post
                    assertEquals(0, cb.generatePost().length());
                }
        );
    }

    @Test
    public void testGeneratePostEmptyString() {
        ChatterBot cb = new ChatterBot(new LinkedList<>());
        int[] walk = { 0 };
        NumberGenerator ng = new ListNumberGenerator(walk);
        assertEquals("", cb.generatePost(ng));
    }

    @Test
    public void testGeneratePost2() {
        ChatterBot cb = new ChatterBot(getTestTrainingDataExample());

        // a -> table -> and -> a -> chair -> END
        String expected = "a table and a chair";
        int[] walk = { 0, 3, 0, 0, 2, 0 };
        NumberGenerator ng = new ListNumberGenerator(walk);
        assertEquals(expected, cb.generatePost(ng));
    }
    @Test
    public void testGeneratePost3() {
        ChatterBot cb = new ChatterBot(getTestTrainingDataExample());

        // a -> chair -> END
        String expected = "a chair";
        int[] walk = { 0, 2, 0 };
        NumberGenerator ng = new ListNumberGenerator(walk);
        assertEquals(expected, cb.generatePost(ng));
    }

    @Test
    public void testGeneratePostPunctuationNoSpace() {
        ChatterBot cb = new ChatterBot(getTestTrainingDataExample());

        // a -> banana -> ! -> and -> a -> banana -> ? -> END
        String expected = "a banana! and a banana?";
        int[] walk = { 0, 0, 0, 0, 0, 0, 1, 0 };
        NumberGenerator ng = new ListNumberGenerator(walk);
        assertEquals(expected, cb.generatePost(ng));
    }

}
