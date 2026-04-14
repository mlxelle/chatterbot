package org.cis1200;

import java.io.IOException;
import java.util.Iterator;
import java.io.BufferedReader;
import java.util.NoSuchElementException;

/**
 * Lazily iterates over the lines of a {@link BufferedReader}, reading one
 * line per {@link #next()} call rather than loading the entire file into
 * memory.
 */
public class LineIterator implements Iterator<String> {

    private BufferedReader reader;
    private String nextLine;

    /**
     * Constructs a {@code LineIterator} for the given reader. The first line is
     * read eagerly so that {@link #hasNext()} can be answered without advancing.
     *
     * @param reader the reader to iterate over
     * @throws IllegalArgumentException if reader is null
     */
    public LineIterator(BufferedReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader is empty");
        }
        this.reader = reader;
        try {
            nextLine = reader.readLine();
        } catch (IOException e) {
            nextLine = null;
        }
    }

    /**
     * Creates a {@code LineIterator} directly from a file path.
     *
     * @param filePath path to the file
     * @throws IllegalArgumentException if filePath is null or the file doesn't exist
     */
    public LineIterator(String filePath) {
        this(FileUtilities.fileToReader(filePath));
    }

    /**
     * Returns true if there are lines left to read in the file, and false
     * otherwise.
     * <p>
     * If there are no more lines left, this method attempts to close the
     * BufferedReader. In case of an IOException during the closing process,
     * an error message is printed to the console indicating the issue.
     *
     * @return a boolean indicating whether the LineIterator can produce
     *         another line from the file
     */
    @Override
    public boolean hasNext() {
        if (nextLine == null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error closing reader: " + e.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the next line from the file, or throws a NoSuchElementException
     * if there are no more strings left to return (i.e. hasNext() is false).
     * <p>
     * This method also advances the iterator in preparation for another
     * invocation. If an IOException is thrown during a next() call, your
     * iterator should make note of this such that future calls of hasNext()
     * will return false and future calls of next() will throw a
     * NoSuchElementException
     *
     * @return the next line in the file
     * @throws java.util.NoSuchElementException if there is no more data in the
     *                                          file
     */
    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next line");
        } else {
            String currentLine = nextLine;
            try {
                nextLine = reader.readLine();
            } catch (IOException e) {
                nextLine = null;
            }
            return currentLine;
        }
    }
}
