package org.cis1200;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes raw post strings into sequences used for Markov Chain training.
 * Words (including contractions, hashtags, and @-mentions) and punctuation
 * marks are treated as separate tokens. URLs are stripped before tokenization.
 */
public class PostParser {

    /**
     * Regular Expressions
     * <p>
     * For the purposes of this project, we consider "word characters" to be
     * alphanumeric characters [a-zA-Z0-9] and apostrophes ['], hashes [#],
     * and [@]. (We use those symbols so that "don't" "#hashtag" and "@user"
     * are parsed as single tokens.)
     * <p>
     * A <b>token</b> is either a {@code WORD_TOKEN}, which is a sequence of
     * word characters, or a {@code PUNCTUATION_TOKEN}, like "!" or "." .
     * Strings matching these constraints are described using <i>regular
     * expressions</i>
     * that the {@link Pattern} class uses to find matching substrings. See that
     * documentation for more details.
     * <p>
     * The {@code URL_REGEX} matches any substring that starts a word with
     * "http" or "https" and continues until some whitespace occurs. It is
     * used in the {@link #removeURLs(String)} static method.
     *
     */
    static final String WORD_TOKEN = "[\\p{Alnum}'@#]+";
    static final String PUNCTUATION_TOKEN = "\\p{Punct}";
    static final String TOKEN = WORD_TOKEN + "|" + PUNCTUATION_TOKEN;
    static final String URL_REGEX = "\\bhttp[s]?://\\S*";

    /**
     * Given a String, remove all substrings that look like a URL. Any word that
     * begins with the character sequence 'http' is simply replaced with the
     * empty string.
     *
     * @param s - a String from which URL-like words should be removed
     * @return s where each "URL-like" string has been deleted
     */
    static String removeURLs(String s) {
        return s.replaceAll(URL_REGEX, "");
    }

    /**
     * Converts a string into a list of training tokens by first removing
     * any URLs and then breaking up the string at any whitespace.
     *
     * @param post a single String to be used as a source of training data tokens
     * @return a list of tokens
     */
    static List<String> parseAndCleanPost(String post) {
        List<String> cleanedPost = new LinkedList<>();

        Pattern p = Pattern.compile(TOKEN);
        Matcher m = p.matcher(removeURLs(post));
        while (m.find()) {
            String word = m.group().trim();
            if (!word.isEmpty()) {
                cleanedPost.add(word);
            }
        }
        return cleanedPost;
    }

    /**
     * Applies the {@code parseAndCleanPost} to a list of raw input posts,
     * returning each cleaned post as a list of tokens to be used as training data.
     * <p>
     * If, after cleaning, a raw post has no tokens (i.e., is empty), it
     * is ignored and does not contribute to the training data.
     *
     * @param rawPosts a list of {@code Strings} to be parsed and cleaned as posts
     * @return a list of training data examples
     */
    public static List<List<String>> rawPostsToTrainingData(
            List<String> rawPosts
    ) {
        List<List<String>> data = new LinkedList<>();

        // clean every post and add all the resulting posts to the
        // training data (if the result is non-empty)
        for (String post : rawPosts) {
            List<String> cleanedPost = parseAndCleanPost(post);
            if (!cleanedPost.isEmpty()) {
                data.add(cleanedPost);
            }
        }
        return data;
    }

}
