package org.cis1200;

import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Operations for working with CSV data.
 * <p>
 * For our purposes, CSV data is a series of text lines, each of which is
 * considered to be a <i>record</i> consisting of <i>fields</i> separated by
 * the comma ',' character. (Some variants of CSV allow for multi-line
 * data representations, but we disregard that possibility here.)
 * <p>
 * <p>
 * For example, the file {@code files/illustrative_example.csv} contains two
 * CSV records, one on each line of the file:
 *
 * <pre>
 * col0, col1, a table and a chair
 * cola, colb, a banana! and a banana?
 * </pre>
 * <p>
 * Each of the records in this example contains three fields, but there is no
 * requirement that each record have the same number of fields.
 *
 * <p>
 * There is one subtlety to parsing CSV records: to allow for the possibility
 * of a field that contains the ',' character itself, CSV treats the
 * double quote character '"' specially. You can quote a field that contains
 * commas.
 *
 * <p>
 * For example, the following line has <i>two</i> fields, the first of which
 * has a quoted comma:
 *
 * <pre>
 * "this , is quoted",but there are none in this field
 * </pre>
 */
public class CSV {

    /**
     * Define constants for the "special" characters used in CSV files.
     */
    private final static char DOUBLE_QUOTES = '"';
    private final static char COMMA = ',';

    /**
     * Parses one line of a CSV file into a list of fields.
     * <p>
     * Fields may be enclosed in double quotes, which allows them to contain
     * literal commas. A pair of double quotes inside a quoted field ({@code ""})
     * is interpreted as a single literal quote character.
     *
     * @param csvLine the line of text to parse
     * @return the fields of the record as a list of {@code String}s
     */
    public static List<String> parseRecord(String csvLine) {
        List<String> fields = new LinkedList<>();
        boolean quote = false;
        StringBuilder current = new StringBuilder();
        char[] chars = csvLine.toCharArray();

        for (char c : chars) {
            if (c == DOUBLE_QUOTES) {
                quote = !quote;
            } else if (c == COMMA && !quote) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString()); // add the last field
        return fields;
    }

    /**
     * Given a {@code String} that represents a CSV line and an
     * {@code int} column index, returns the contents of that column.
     * Columns in the buffered reader are zero indexed.
     *
     * @param csvLine   the {@code String} containing the CSV record
     * @param csvColumn the column index of the CSV field whose contents ought to be
     *                  returned
     * @return the field of csvLine corresponding to {@code csvColumn}
     * @throws IllegalArgumentException  if {@code csvLine} is null
     * @throws IndexOutOfBoundsException if {@code csvColumn} is not a
     *                                   valid field index of the record
     */
    static String extractColumn(String csvLine, int csvColumn) {
        if (csvLine == null) {
            throw new IllegalArgumentException("CSV Line is empty");
        }
        List<String> fields = parseRecord(csvLine);
        return fields.get(csvColumn);
    }

    /**
     * Given a {@code BufferedReader} of CSV data and a column index, returns
     * the list of all CSV fields appearing in that column.
     *
     * <p>
     * If a line has no field at the given index, it is skipped.
     *
     * <p>
     * If the line has a field at the given index, it should be returned
     * as an element of the list.
     *
     * @param br        - a BufferedReader that represents posts
     * @param csvColumn - the index of the column in the CSV data
     * @return a {@code List} of CSV fields (none of which is null)
     */
    static List<String> csvFieldsAtColumn(BufferedReader br, int csvColumn) {
        List<String> elements = new LinkedList<>();
        LineIterator li = new LineIterator(br);

        while (li.hasNext()) {
            String current = li.next();
            try {
                elements.add(extractColumn(current, csvColumn));
            } catch (IndexOutOfBoundsException e) {
            } // skip if the line doesn't have the field
        }
        return elements;
    }

}
