package org.cis1200;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CSVKudosTest {

    // A helper function for creating lists of strings
    private static List<String> listOfArray(String[] words) {
        List<String> l = new LinkedList<String>();
        Collections.addAll(l, words);
        return l;
    }

    @Test
    public void testParseRecordQuotedQuoteKudos() {
        String csvLine = "\"abc\"\"def\"";
        String[] expected = { "abc\"def" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordQuotedQuoteNonterminatedKudos() {
        String csvLine = "\"abc\"\"";
        String[] expected = { "abc\"" };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testParseRecordComplexQuotesKudos() {
        String csvLine = "\"abc\"\"\"\"\"def,  space\",\",, \" end ";
        String[] expected = { "abc\"\"def", "  space,", "", "  end " };
        List<String> results = CSV.parseRecord(csvLine);
        assertEquals(listOfArray(expected), results);
    }

    @Test
    public void testQuotesAndCommasCSVKudos() {
        try {
            // Note that the spaces really matter in this test
            String[][] expected = {
                {
                    "this, containing two commas, is one field",
                    " here is another unquoted field", " and a third",
                    " this one has a comma , "
                },
                {
                    "this field \"contains\" quotes but no commas", "", "\"",
                    " one , quoted comma mixed with  another,"
                },
                {
                    "an unmatched quote continues to the end of the line,"
                            + " even when there are commas"
                },
                {
                    "a mixture ofempty , and\" nonempty\" quotes"
                }
            };
            int i = 0;
            BufferedReader br = new BufferedReader(new FileReader("files/quotes_and_commas.csv"));
            String record = br.readLine();
            while (record != null) {
                List<String> fields = CSV.parseRecord(record);
                assertEquals(listOfArray(expected[i]), fields);
                i++;
                record = br.readLine();
            }
        } catch (IOException e) {
            fail();
        }
    }
}
