package org.cis1200;

import java.io.*;
import java.util.List;

/**
 * Entry point for ChatterBot. Loads a CSV file of posts, trains a
 * Markov Chain on the text, generates 10 random posts, and writes them
 * to a file.
 * <p>
 * Change {@link #PATH_TO_POSTS} to point to any of the CSV files in
 * the {@code files/} directory. Adjust {@link #POST_COLUMN} if the
 * text is in a different column (columns are zero-indexed).
 */
public class ChatterBotMain {

    /** Path to the CSV file used for training. */
    static final String PATH_TO_POSTS = "files/philosophy_posts.csv";
    /** Column in the PATH_TO_POSTS CSV file to read posts from */
    static final int POST_COLUMN = 2;
    /** File to store generated posts */
    static final String PATH_TO_OUTPUT_POSTS = "files/generated_posts.txt";

    /**
     * Prints ten generated posts to the console so that you can see how your bot is
     * performing!
     */
    public static void main(String[] args) {

        // Obtain a Reader for processing the CSV file
        BufferedReader csvReader = FileUtilities.fileToReader(PATH_TO_POSTS);

        // Extract all the CSV fields at the given POST_COLUMN
        List<String> rawPosts = CSV.csvFieldsAtColumn(csvReader, POST_COLUMN);

        // Parse the raw posts into training data
        List<List<String>> trainingData = PostParser.rawPostsToTrainingData(rawPosts);

        // Construct a trained ChatterBot
        ChatterBot cb = new ChatterBot(trainingData);

        // Generate 10 random posts and print them to the terminal
        List<String> posts = cb.generateRandomPosts(10);
        for (String post : posts) {
            System.out.println(post);
        }

        // Write the generated posts to the given file.
        // Make sure that PATH_TO_OUTPUT_POSTS is set properly.
        FileUtilities.writeStringsToFile(posts, PATH_TO_OUTPUT_POSTS, false);
    }

}
