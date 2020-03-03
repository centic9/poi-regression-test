package org.dstadler.commoncrawl.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class BugAnnotationsTest {

    @Test
    public void testGetAnnotation() {
        assertEquals("", BugAnnotations.getAnnotation(""));
        assertEquals("", BugAnnotations.getAnnotation("abcdefgh"));
        assertEquals("fixed", BugAnnotations.getAnnotation("ReadOnlySharedStringsTable.getEntryAt(ReadOnlySharedStringsTable.java:182)"));
    }

    @Test
    public void testGetReplacement() {
        assertEquals("", BugAnnotations.getReplacement(""));
        assertEquals("abcdef", BugAnnotations.getReplacement("abcdef"));
        assertEquals("java.lang.IndexOutOfBoundsException: Block * not found",
                BugAnnotations.getReplacement("java.lang.IndexOutOfBoundsException: Block 1249 not found"));
        assertEquals("java.io.EOFException: unexpected EOF - expected len: * - actual len: *",
                BugAnnotations.getReplacement("java.io.EOFException: unexpected EOF - expected len: 177846 - actual len: 114051"));
        assertEquals("java.lang.IllegalStateException: Invalid file format version number: *\n" +
                "\tat o.a.p.hwpf.model.FileInformationBlock.assertCbRgFcLcb(FileInformationBlock.java:164)\n" +
                "\tat o.a.p.hwpf.model.FileInformationBlock.(FileInformationBlock.java:140)\n" +
                "\tat o.a.p.hwpf.HWPFDocumentCore.(HWPFDocumentCore.java:157)\n" +
                "\tat o.a.p.hwpf.HWPFDocument.(HWPFDocument.java:218)\n" +
                "\tat o.a.p.hwpf.HWPFDocument.(HWPFDocument.java:186)\n" +
                "\tat o.a.p.hwpf.HWPFDocument.(HWPFDocument.java:174)\n" +
                "\tat o.a.p.stress.HWPFFileHandler.handleFile(HWPFFileHandler.java:32)\n" +
                "\tat o.a.p.BaseIntegrationTest.handleFile(BaseIntegrationTest.java:93)\n" +
                "\tat o.a.p.BaseIntegrationTest.test(BaseIntegrationTest.java:42)\n" +
                "\tat org.dstadler.commoncrawl.ProcessFiles$FileHandlingRunnable.run(ProcessFiles.java:220)",
                BugAnnotations.getReplacement("java.lang.IllegalStateException: Invalid file format version number: 195\n" +
                        "\tat o.a.p.hwpf.model.FileInformationBlock.assertCbRgFcLcb(FileInformationBlock.java:164)\n" +
                        "\tat o.a.p.hwpf.model.FileInformationBlock.(FileInformationBlock.java:140)\n" +
                        "\tat o.a.p.hwpf.HWPFDocumentCore.(HWPFDocumentCore.java:157)\n" +
                        "\tat o.a.p.hwpf.HWPFDocument.(HWPFDocument.java:218)\n" +
                        "\tat o.a.p.hwpf.HWPFDocument.(HWPFDocument.java:186)\n" +
                        "\tat o.a.p.hwpf.HWPFDocument.(HWPFDocument.java:174)\n" +
                        "\tat o.a.p.stress.HWPFFileHandler.handleFile(HWPFFileHandler.java:32)\n" +
                        "\tat o.a.p.BaseIntegrationTest.handleFile(BaseIntegrationTest.java:93)\n" +
                        "\tat o.a.p.BaseIntegrationTest.test(BaseIntegrationTest.java:42)\n" +
                        "\tat org.dstadler.commoncrawl.ProcessFiles$FileHandlingRunnable.run(ProcessFiles.java:220)"));

        String replacement = BugAnnotations.getReplacement("java.io.IOException: Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data. " +
                "This may indicate that the file is used to inflate memory usage and thus could pose a security risk. " +
                "You can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit. " +
                "Uncompressed size: 106496, Raw/compressed size: 749, ratio: 0.007033 Limits: MIN_INFLATE_RATIO: 0.010000, Entry: word/media/image1.emf");
        assertEquals("java.io.IOException: Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data. " +
                        "This may indicate that the file is used to inflate memory usage and thus could pose a security risk. " +
                        "You can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit. " +
                        "Uncompressed size: *, Raw/compressed size: *, ratio: * Limits: MIN_INFLATE_RATIO: *, Entry: *",
                replacement);

        replacement = BugAnnotations.getReplacement("java.io.IOException: Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data. " +
                "This may indicate that the file is used to inflate memory usage and thus could pose a security risk. " +
                "You can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit. " +
                "Uncompressed size: 106496, Raw/compressed size: 970, ratio: 0.009108 Limits: MIN_INFLATE_RATIO: 0.010000, Entry: xl/media/image1.emf");
        assertEquals("java.io.IOException: Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data. " +
                        "This may indicate that the file is used to inflate memory usage and thus could pose a security risk. " +
                        "You can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit. " +
                        "Uncompressed size: *, Raw/compressed size: *, ratio: * Limits: MIN_INFLATE_RATIO: *, Entry: *",
                replacement);

        replacement = BugAnnotations.getReplacement("java.io.IOException: Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data.\n" +
                "This may indicate that the file is used to inflate memory usage and thus could pose a security risk.\n" +
                "You can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit.\n" +
                "Uncompressed size: 106496, Raw/compressed size: 970, ratio: 0.009108\n" +
                "Limits: MIN_INFLATE_RATIO: 0.010000, Entry: xl/media/image1.emf");
        assertEquals("java.io.IOException: Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data.\n" +
                        "This may indicate that the file is used to inflate memory usage and thus could pose a security risk.\n" +
                        "You can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit.\n" +
                        "Uncompressed size: *, Raw/compressed size: *, ratio: * Limits: MIN_INFLATE_RATIO: *, Entry: *",
                replacement);

        replacement = BugAnnotations.getReplacement("java.io.FileNotFoundException: no such entry: \"WordDocument\", had: [__substg1.0_0E1D001E, __substg1.0_007F0102, __substg1.0_65E30102, __substg1.0_0E02001E, __substg1.0_00470102, __substg1.0_0070001E, __substg1.0_800D001E, __substg1.0_0064001E, __substg1.0_007D001E, __substg1.0_0C1A001E, __attach_version1.0_#00000000, __substg1.0_0065001E, __substg1.0_0E04001E, __substg1.0_00410102, __substg1.0_0040001E, __substg1.0_00710102, __substg1.0_00520102, __recip_version1.0_#00000000, __substg1.0_65E20102, __substg1.0_0C190102, __substg1.0_0E03001E, __substg1.0_3FFA001E, __substg1.0_001A001E, __substg1.0_0037001E, __substg1.0_300B0102, __substg1.0_003F0102, __substg1.0_8006001E, __substg1.0_10090102, __substg1.0_0C1E001E, __substg1.0_00510102, __substg1.0_0076001E, __substg1.0_0042001E, __substg1.0_00430102, __substg1.0_0C1F001E, __substg1.0_0077001E, __substg1.0_1000001E, __substg1.0_0078001E, __substg1.0_800A001E, __substg1.0_0C1D0102, __substg1.0_10F3001E, __substg1.0_800C001E, __nameid_version1.0, __substg1.0_8009001E, __substg1.0_003D001E, __substg1.0_1035001E, __substg1.0_0044001E, __substg1.0_0075001E, __substg1.0_800B001E, __properties_version1.0, __substg1.0_003B0102]");
        assertEquals("java.io.FileNotFoundException: no such entry: \"WordDocument\", had: [*]",
                replacement);
    }

    @Test
    public void testPatternReplace() {
        Pattern pattern = Pattern.compile("abcd\\d+");
        Matcher matcher = pattern.matcher("abcd123\nasdkasd\nasdasd");
        assertFalse(matcher.matches());
        matcher = pattern.matcher("abcd123\nasdkasd\nasdasd");
        assertTrue(matcher.find());
        assertEquals("abcd*\nasdkasd\nasdasd", matcher.replaceFirst("abcd*"));
    }

    // helper method to get coverage of the unused constructor
    @Test
    public void testPrivateConstructor() throws Exception {
        org.dstadler.commons.testing.PrivateConstructorCoverage.executePrivateConstructor(BugAnnotations.class);
    }
}
