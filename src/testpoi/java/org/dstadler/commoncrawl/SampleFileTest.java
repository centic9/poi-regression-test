package org.dstadler.commoncrawl;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.stress.FileHandler;
import org.apache.poi.stress.POIFileScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class SampleFileTest {

    public static final File ROOT_DIR = new File("src/testpoi/resources");

    @Parameterized.Parameters(name = "File: {0}, Handler: {1}")
    public static Collection<Object[]> data() throws IOException {
        Collection<Map.Entry<String, FileHandler>> files = POIFileScanner.scan(ROOT_DIR);
        assertNotNull(files);
        assertFalse(files.isEmpty());

        List<Object[]> params = new ArrayList<>();
        for (Map.Entry<String, FileHandler> file : files) {
            params.add(new Object[] { file.getKey(), file.getValue() });
        }

        return params;
    }

    @Parameterized.Parameter
    public String file;
    @Parameterized.Parameter(1)
    public FileHandler handler;

    @Test
    public void test() throws Exception {
        System.out.println("Handling file: " + file);
        File fileIO = new File(ROOT_DIR, file);
        try (InputStream stream = new BufferedInputStream(new FileInputStream(fileIO), 1024 * 100)) {
            try {
                handler.handleFile(stream, file);
            } catch (IOException e) {
                if (!e.getMessage().equals("Truncated ZIP file")) {
                    throw e;
                }
            }
            handler.handleAdditional(fileIO);

            try {
                handler.handleExtracting(fileIO);
            } catch (InvalidOperationException e) {
                if (e.getCause() != null && !e.getCause().getMessage().equals("Truncated ZIP file")) {
                    throw e;
                }
            }
        }
    }
}
