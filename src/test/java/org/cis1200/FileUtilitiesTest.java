package org.cis1200;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.List;
import java.util.LinkedList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

public class FileUtilitiesTest {

    final String testFile = "./files/chatterbot_test.csv";
    final String stringsToFileTest = "./files/strings_to_file.txt";

    @Test
    public void testFileToReaderFilePathNull() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            Assertions.assertThrows(
                    IllegalArgumentException.class, () -> FileUtilities.fileToReader(null)
            );
        });
    }

    @Test
    public void testFileToReaderInvalidFilePath() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            Assertions.assertThrows(
                    IllegalArgumentException.class, () -> FileUtilities.fileToReader("invalid")
            );
        });
    }

    @Test
    public void writeStringsToFileNoAppendTest() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(stringsToFileTest, false)
            );
            bw.write("og line here");
            bw.close();

            // ChatterBot cb = new ChatterBot(FileUtilities.fileToReader(testFile), 1);
            List<String> strings = new LinkedList<>();
            strings.add("Line1");
            strings.add("Line2");
            strings.add("Line3");
            FileUtilities.writeStringsToFile(strings, stringsToFileTest, false);

            try (BufferedReader br = new BufferedReader(new FileReader(stringsToFileTest))) {
                assertEquals("Line1", br.readLine());
                assertEquals("Line2", br.readLine());
                assertEquals("Line3", br.readLine());
            }
        });
    }

    @Test
    public void writeStringsToFileAppend() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(stringsToFileTest, false)
            );
            bw.write("og line here\n");
            bw.close();

            List<String> strings = new LinkedList<>();
            strings.add("Line1");
            strings.add("Line2");
            strings.add("Line3");
            FileUtilities.writeStringsToFile(strings, stringsToFileTest, true);

            try (BufferedReader br = new BufferedReader(new FileReader(stringsToFileTest))) {
                assertEquals("og line here", br.readLine());
                assertEquals("Line1", br.readLine());
                assertEquals("Line2", br.readLine());
                assertEquals("Line3", br.readLine());
            }
        });
    }

    @Test
    public void testFileToReader() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            BufferedReader csvReader = FileUtilities.fileToReader("./files/postparser_test.csv");

            List<String> expected = new LinkedList<>();
            expected.add(
                    "rando1,rando2,rando3,A tweet lives in this line. . . ." +
                            "  12345678910.  Banana.  Despicable Me 2 coming out in theaters soon!"
            );
            expected.add("rando1,rando2,rando3,Banana. @foo");

            expected.add(
                    "rando1,rando2,rando3,I will go to the zoo." +
                            "  There I http://www.cis.upenn.edu/~cis1200 will be happy."
            );
            expected.add(
                    "rando1,rando2,rando3," +
                            "https://www.thisIsAnInvalidTweetAnd" +
                            "WillNotBeIncludedInTheOutputOfParseCsvSentences.com"
            );
            expected.add(
                    "rando1,rando2,rando3,\"This tweet contains a , in quotes.\""
            );
            expected.add(
                    "rando1,rando2,rando3,can't won't didn't"
            );
            List<String> rawPosts = new LinkedList<>();
            String line = csvReader.readLine();
            while (line != null) {
                rawPosts.add(line);
                line = csvReader.readLine();
            }
            assertEquals(expected, rawPosts);
        });
    }

}
