package org.dstadler.commoncrawl;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProcessJSONListOfFilesTest {
    @Test
    public void testExcludes() {
        FileHandlingRunnable.ignored.set(0);

        // nothing happens as this line is ignored
        ProcessJSONListOfFiles.process(
                "{ \"fileName\":\"/data2/docs/commoncrawl2/5W/5WUVHHK4YQ5HJQVPA5WG4F3TB2UGZJGF\", \"mediaType\":\"application/vnd.openxmlformats-officedocument.presentationml.presentation\"}",
                null, System.currentTimeMillis(), null);

        assertEquals(1, FileHandlingRunnable.ignored.get());
    }

    @Test
    public void testMimetype() {
        FileHandlingRunnable.ignored.set(0);

        // nothing happens as this line is ignored
        ProcessJSONListOfFiles.process(
                "{ \"fileName\":\"/data2/docs/commoncrawl2/5W/test.html\", \"mediaType\":\"text/html\"}",
                null, System.currentTimeMillis(), null);

        assertEquals(1, FileHandlingRunnable.ignored.get());
    }
}