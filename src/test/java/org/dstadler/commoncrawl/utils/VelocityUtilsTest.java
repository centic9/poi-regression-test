package org.dstadler.commoncrawl.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.tools.ant.taskdefs.Move;
import org.dstadler.commoncrawl.report.ReportItem;
import org.dstadler.commoncrawl.jpa.FileStatus;
import org.junit.AfterClass;
import org.junit.Test;


public class VelocityUtilsTest {
    @AfterClass
    public static void tearDownClass() {
        LogManager.shutdown();
    }

    @Test
    public void testRender() throws Exception {
        File file = File.createTempFile("VelocityUtils", ".test");
        try {
            assertTrue(file.delete());

            List<ReportItem> items = new ArrayList<>();
            items.add(new ReportItem(1, FileStatus.OK, "exception", "stacktrace", "samplefile1"));
            items.add(new ReportItem(1, FileStatus.ERROR, "exception", "stacktrace", "samplefile2"));
            items.add(new ReportItem(1, FileStatus.TIMEOUT, "exception", "stacktrace", "samplefile3"));

            Map<String,Object> context = new HashMap<>();
            context.put("items", items);

            VelocityUtils.render(context, "test.vm", file);

            assertTrue(file.exists());

            String content = FileUtils.readFileToString(file, "UTF-8");
            assertTrue("Had: " + content,
                    content.contains("This is a test: 3"));
        } finally {
            assertTrue(!file.exists() || file.delete());
        }
    }

    @Test
    public void testRenderInvalidFile() throws Exception {
        List<Move> moves = new ArrayList<>();
        moves.add(new Move());
        moves.add(new Move());
        moves.add(new Move());
        Map<String,Object> context = new HashMap<>();
        context.put("moves", moves);

        // use a directory
        File dir = File.createTempFile("VelocityUtils", ".test");
        assertTrue(dir.delete());
        assertTrue(dir.mkdirs());
        try {
            VelocityUtils.render(context, "test.vm", dir);
            fail("Will fail with dir instead of file");
        } catch (@SuppressWarnings("unused") FileNotFoundException e) {
            // expected...
        } finally {
            assertTrue(dir.delete());
        }
    }

    @Test
    public void testRenderReport() throws Exception {
        File file = File.createTempFile("VelocityUtils", ".test");
        try {
            assertTrue(file.delete());

            List<ReportItem> items = new ArrayList<>();
            items.add(new ReportItem(1, FileStatus.OK, "exception", "stacktrace", "samplefile1"));
            items.add(new ReportItem(1, FileStatus.ERROR, "exception", "stacktrace", "samplefile2"));
            items.add(new ReportItem(1, FileStatus.TIMEOUT, "exception", "stacktrace", "samplefile3"));

            Map<String,Object> context = new HashMap<>();
            context.put("items", items);

            VelocityUtils.render(context, "Report.vm", file);

            assertTrue(file.exists());

            String content = FileUtils.readFileToString(file, "UTF-8");
            assertTrue("Had: " + content,
                    content.contains("Having <mark>3</mark> different"));
        } finally {
            assertTrue(!file.exists() || file.delete());
        }
    }

    // helper method to get coverage of the unused constructor
    @Test
    public void testPrivateConstructor() throws Exception {
        org.dstadler.commons.testing.PrivateConstructorCoverage.executePrivateConstructor(VelocityUtils.class);
    }
}
