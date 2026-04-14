package org.cis1200;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.io.StringReader;
import java.io.BufferedReader;

public class CSVTest {

    // A helper function for creating lists of strings
    private static List<String> listOfArray(String[] words) {
        List<String> l = new LinkedList<>();
        Collections.addAll(l, words);
        return l;
    }

    @Test
    public void testParseRecordEmpty() {
        String csvLine = "";
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(1, results.size());
        assertEquals("", results.get(0));
    }

    @Test
    public void testParseRecordNonEmpty() {
        String csvLine = "abc";
        String[] expected = { "abc" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordTwoFields() {
        String csvLine = "abc,def";
        String[] expected = { "abc", "def" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordThreeFields() {
        String csvLine = "abc,def,x12a";
        String[] expected = { "abc", "def", "x12a" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordQuote1() {
        String csvLine = "\"abc\"";
        String[] expected = { "abc" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordQuotedComma() {
        String csvLine = "\"abc,def\"";
        String[] expected = { "abc,def" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordQuotedTwoFields() {
        String csvLine = "\"abc\",\"def\"";
        String[] expected = { "abc", "def" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordQuoteEmpty() {
        String csvLine = "\"\"";
        String[] expected = { "" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(1, results.size());
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordQuoteUnterminated() {
        String csvLine = "\"abc";
        String[] expected = { "abc" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordEmptyFields() {
        String csvLine = ",,\"\",";
        String[] expected = { "", "", "", "" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(4, results.size());
        assertEquals(listOfArray(expected), results);
    }

    // extractColumn tests ----------------------------------------------------

    @Test
    public void testExtractColumnNull() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            assertThrows(IllegalArgumentException.class, () -> CSV.extractColumn(null, 3));
            assertThrows(IllegalArgumentException.class, () -> CSV.extractColumn(null, 0));
            assertThrows(IllegalArgumentException.class, () -> CSV.extractColumn(null, -1));
        });
    }

    @Test
    public void testExtractColumnGetsCorrectColumn() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            assertEquals(
                    "wrongColumn",
                    CSV.extractColumn(
                            "wrongColumn, wrong column, wrong column!, This is a post.", 0
                    )
            );
            assertEquals(
                    " wrong column",
                    CSV.extractColumn(
                            "wrongColumn, wrong column, wrong column!, This is a post.", 1
                    )
            );
            assertEquals(
                    " wrong column!",
                    CSV.extractColumn(
                            "wrongColumn, wrong column, wrong column!, This is a post.", 2
                    )
            );
            assertEquals(
                    " This is a post.",
                    CSV.extractColumn(
                            "wrongColumn, wrong column, wrong column!, This is a post.", 3
                    )
            );
        });
    }

    @Test
    public void testExtractColumnThrowsIndexOutOfBoundsExceptionForInCorrectColumn() {
        assertTimeoutPreemptively(
                Duration.ofSeconds(10), () -> {
                    // Column number < zero.
                    assertThrows(
                            IndexOutOfBoundsException.class, () -> CSV.extractColumn(
                                    "wrongColumn, wrong column, wrong column!, This is a post.", -1
                            )
                    );
                    // Column number > number of columns.
                    assertThrows(
                            IndexOutOfBoundsException.class, () -> CSV.extractColumn(
                                    "wrongColumn, wrong column, wrong column!, This is a post.", 5
                            )
                    );
                    // Column number = number of columns.
                    assertThrows(
                            IndexOutOfBoundsException.class, () -> CSV.extractColumn(
                                    "wrongColumn, wrong column, wrong column!, This is a post.", 4
                            )
                    );
                }
        );
    }

    @Test
    public void testCSVFieldsAtColumnMiddleColumn() {
        String csvLines = "0,fields should come from this column,not this column\n" +
                "1,and from this column again,but not this one";
        String[] expected = { "fields should come from this column", "and from this column again" };
        StringReader sr = new StringReader(csvLines);
        BufferedReader br = new BufferedReader(sr);
        List<String> fields = CSV.csvFieldsAtColumn(br, 1);
        assertEquals(2, fields.size());
        assertEquals(listOfArray(expected), fields);
    }
}
