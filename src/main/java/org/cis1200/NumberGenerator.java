package org.cis1200;

/**
 * Produces integers bounded by a given maximum. Used by
 * {@link MarkovChain} and {@link ProbabilityDistribution} to sample
 * from probability distributions.
 * <p>
 * {@link RandomNumberGenerator} provides a random implementation for
 * production use; {@link ListNumberGenerator} provides a deterministic
 * implementation for testing.
 */
public interface NumberGenerator {
    /**
     * Generates a number based off of a specified bound.
     *
     * @param bound - the max value that can be returned by the generator.
     * @return a number between 0 (inclusive) and bound (exclusive).
     */
    int next(int bound);
}
