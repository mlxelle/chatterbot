package org.cis1200;

import java.util.*;

/**
 * A Markov Chain is a data structure that tracks the frequency with which one
 * token follows another token in a collection of sequences. 
 *
 * This project uses a Markov Chain to model posts by gathering the frequency
 * information from a chat feed. The MarkovChain generates "plausible"
 * posts by constructing a random walk through the chain according to the
 * frequencies.
 *
 * <p>
 */
public class MarkovChain {

    /** probability distribution of initial tokens in a sentence */
    final ProbabilityDistribution<String> startTokens;

    /** for each token, probability distribution of next token in a sentence */
    final Map<String, ProbabilityDistribution<String>> bigramFrequencies;

    /** end of sentence marker */
    static final String END_TOKEN = "<END>";

    /**
     * Construct an empty {@code MarkovChain} that can later be trained.
     */
    public MarkovChain() {
        this.bigramFrequencies = new TreeMap<>();
        this.startTokens = new ProbabilityDistribution<>();
    }

    /**
     * Construct a trained {@code MarkovChain} from the given list of training data.
     * The training data is assumed to be non-null. Uses the {@link #addSequence}
     * method on each of the provided sequences.
     *
     * @param trainingData - the input sequences of tokens from which to construct
     *                     the {@code MarkovChain}
     */
    public MarkovChain(List<List<String>> trainingData) {
        this.bigramFrequencies = new TreeMap<>();
        this.startTokens = new ProbabilityDistribution<>();
        for (List<String> seq : trainingData) {
            addSequence(seq.iterator());
        }
    }

    /**
     * Adds a bigram to the Markov Chain information by
     * recording it in the appropriate probability distribution
     * of {@code bigramFrequencies}. (If this is the first time that {@code first}
     * has appeared in a bigram, creates a new probability distribution first.)
     *
     * @param first  The first token of the Bigram (should not be null)
     * @param second The second token of the Bigram (should not be null)
     * @throws IllegalArgumentException - when either parameter is null
     */
    void addBigram(String first, String second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Empty token");
        }

        if (bigramFrequencies.containsKey(first)) {
            ProbabilityDistribution<String> dist = bigramFrequencies.get(first);
            dist.record(second);
        } else {
            ProbabilityDistribution<String> dist = new ProbabilityDistribution<>();
            dist.record(second);
            bigramFrequencies.put(first, dist);
        }
    }

    /**
     * Adds a single post's training data to the Markov Chain frequency
     * information, by:
     *
     * <ol>
     * <li>recording the first token in {@code startTokens}
     * <li>recording each subsequent bigram of co-occurring pairs of tokens
     * <li>recording a final bigram of the last token of the {@code post} and
     * {@code END_TOKEN} to mark the end of the sequence
     * </ol>
     * <p>
     * Does nothing if the post is empty.
     *
     * @param post an iterator representing one post of training data
     * @throws IllegalArgumentException when the post Iterator is null
     */
    public void addSequence(Iterator<String> post) {
        if (post == null) {
            throw new IllegalArgumentException("Post is empty");
        }
        if (post.hasNext()) {
            String curr = post.next();
            startTokens.record(curr);
            while (post.hasNext()) {
                String next = post.next();
                addBigram(curr, next);
                curr = next;
            }
            addBigram(curr, END_TOKEN);
        }
    }

    /**
     * Returns the ProbabilityDistribution for a given token. Returns null if
     * none exists.
     *
     * @param token - the token for which the ProbabilityDistribution is sought
     * @throws IllegalArgumentException - when parameter is null
     * @return a ProbabilityDistribution
     */
    ProbabilityDistribution<String> get(String token) {
        if (token == null) {
            throw new IllegalArgumentException("token cannot be null.");
        }
        return bigramFrequencies.get(token);
    }

    /**
     * Gets a walk through the Markov Chain that follows
     * the path given by the {@code NumberGenerator}. See
     * {@link MarkovChainIterator} for the details.
     *
     * @param ng the path to follow (represented as a {@code NumberGenerator},
     *           assumed nonnull)
     * @return an {@code Iterator} that yields the tokens on that path
     *
     */
    public Iterator<String> getWalk(NumberGenerator ng) {
        return new MarkovChainIterator(startTokens, bigramFrequencies, ng);
    }

    /**
     * Gets a random walk through the Markov Chain.
     *
     * @return an {@code Iterator} that yields the tokens on that path
     */
    public Iterator<String> getRandomWalk() {
        return getWalk(new RandomNumberGenerator());
    }

    /**
     * Generate a list of numbers such that if it is installed as the
     * number generator for the MarkovChain, and used as an iterator,
     * the tokens returned in sequence will be the list of provided tokens.
     *
     * Note that the length of the list of numbers is equal to the length
     * of the list of tokens plus one (for the {@code END_TOKEN}).
     *
     * @param tokens an ordered list of tokens that the MarkovChain should generate
     *
     * @return a list of integers representing a walk through the Markov Chain that
     *         produces the given sequence of tokens
     *
     * @throws IllegalArgumentException when any of the following are true:
     *                                  <ul>
     *                                  <li>{@code tokens} is null or empty
     *                                  <li>the first token in the list is not in
     *                                  {@code startTokens}
     *                                  <li>any of the tokens in the list is not
     *                                  found as a key in the chain
     *                                  <li>if the last token of the list cannot
     *                                  transition to {@code END_TOKEN}
     *                                  </ul>
     */
    public List<Integer> findWalkChoices(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            throw new IllegalArgumentException("Invalid empty or null tokens");
        }
        tokens.add(MarkovChain.END_TOKEN);
        List<Integer> choices = new LinkedList<>();

        String curToken = tokens.remove(0);
        choices.add(startTokens.index(curToken));

        while (tokens.size() > 0) {
            ProbabilityDistribution<String> curDist = bigramFrequencies.get(curToken);
            String nextToken = tokens.remove(0);
            choices.add(curDist.index(nextToken));
            curToken = nextToken;
        }
        return choices;
    }

    /**
     * Returns a string representation of the Markov Chain showing all
     * tokens and their probability distributions.
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("startTokens: ").append(startTokens.toString());
        res.append("\nbigramFrequencies:\n");
        for (Map.Entry<String, ProbabilityDistribution<String>> c : bigramFrequencies.entrySet()) {
            res.append("\"");
            res.append(c.getKey());
            res.append("\":\t");
            res.append(c.getValue().toString());
            res.append("\n");
        }
        return res.toString();
    }
}
