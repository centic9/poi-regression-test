package org.dstadler.commoncrawl.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        replacement = BugAnnotations.getReplacement("java.io.FileNotFoundException: no such entry: \"WordDocument\", had: [EncryptedPackage, EncryptionInfo, \u0006DataSpaces]");
        assertEquals("java.io.FileNotFoundException: no such entry: \"WordDocument\", had: [*]",
                replacement);
        replacement = BugAnnotations.getReplacement("java.io.FileNotFoundException: no such entry: \"WordDocument\", had: []");
        assertEquals("java.io.FileNotFoundException: no such entry: \"WordDocument\", had: [*]",
                replacement);

        replacement = BugAnnotations.getReplacement("org.opentest4j.TestAbortedException: Assumption failed: File OpenOfficeBugDatabase/22206_registraƒnÃ\u00AD_karta_vznik.doc excluded because it is unsupported old Excel format");
        assertEquals("org.opentest4j.TestAbortedException: Assumption failed: File * excluded because it is unsupported old Excel format",
                replacement);

        replacement = BugAnnotations.getReplacement("org.opentest4j.TestAbortedException: Assumption failed: File OpenOfficeBugDatabase/22206_registraƒnÃ\u00AD_karta_vznik.doc excluded because it is unsupported old Excel format");
        assertEquals("org.opentest4j.TestAbortedException: Assumption failed: File * excluded because it is unsupported old Excel format",
                replacement);

        replacement = BugAnnotations.getReplacement("org.opentest4j.TestAbortedException: Assumption failed: File OpenOfficeBugDatabase/22206_registraƒnÃ\u00AD_karta_ukonƒenÃ\u00AD.doc excluded because it is unsupported old Excel format");
        assertEquals("org.opentest4j.TestAbortedException: Assumption failed: File * excluded because it is unsupported old Excel format",
                replacement);

        replacement = BugAnnotations.getReplacement("org.opentest4j.TestAbortedException: Assumption failed: File OpenOfficeBugDatabase/22206_informaƒnÃ\u00AD_karta_vznikхПояснительазпк-ç¬¬å…\u00ADç« ç´—ç·šçš„å¹¾ä½•æ€§è³ªå’Œå“'$'\\302\\201''è³ªè©•å®š.pp.doc excluded because it is unsupported old Excel format");
        assertEquals("org.opentest4j.TestAbortedException: Assumption failed: File * excluded because it is unsupported old Excel format",
                replacement);

        replacement = BugAnnotations.getReplacement("java.lang.IllegalArgumentException: Invalid char (/) found at index (5) in sheet name 'Ibiza/Cordoba 1993-1999r. (2)'");
        assertEquals("java.lang.IllegalArgumentException: Invalid char (*) found at index (*) in sheet name *",
                replacement);

        replacement = BugAnnotations.getReplacement("java.lang.IllegalArgumentException: newLimit > capacity: (64 > 60)");
        assertEquals("java.lang.IllegalArgumentException: newLimit > capacity: (*)",
                replacement);

        replacement = BugAnnotations.getReplacement("java.lang.IllegalArgumentException: Width (0) and height (1) cannot be <= 0");
        assertEquals("java.lang.IllegalArgumentException: Width (*) and height (*) cannot be <= 0",
                replacement);

        replacement = BugAnnotations.getReplacement("java.io.IOException: Unsupported blocksize (2^105). Expected 2^9 or 2^12.");
        assertEquals("java.io.IOException: Unsupported blocksize (*). Expected 2^9 or 2^12.",
                replacement);

        replacement = BugAnnotations.getReplacement("org.apache.xmlbeans.SchemaTypeLoaderException: XML-BEANS compiled schema: Could not locate compiled schema resource org/apache/poi/schemas/ooxml/system/ooxml/stpageorientation8781type.xsb (o.a.p.schemas.ooxml.system.ooxml.stpageorientation8781type) - code 0");
        assertEquals("org.apache.xmlbeans.SchemaTypeLoaderException: XML-BEANS compiled schema: Could not locate compiled schema resource org/apache/poi/* (o.a.p.*) - code 0",
                replacement);

        replacement = BugAnnotations.getReplacement("java.lang.IllegalStateException: Category and values must have the same point count, but had 1 categories and 112 values.");
        assertEquals("java.lang.IllegalStateException: Category and values must have the same point count, but had * categories and * values.",
                replacement);

        replacement = BugAnnotations.getReplacement("o.a.p.ss.formula.eval.NotImplementedException: Error evaluating cell 'MainDisplay (2)'!B2");
        assertEquals("o.a.p.ss.formula.eval.NotImplementedException: Error evaluating cell *",
                replacement);

        replacement = BugAnnotations.getReplacement("java.lang.RuntimeException: Could not resolve external workbook name 'DSHS%20Data%20Collection%20SFY2013.xls'. The following workbook names are valid: ('')");
        assertEquals("java.lang.RuntimeException: Could not resolve external workbook name * The following workbook names are valid: *",
                replacement);

        replacement = BugAnnotations.getReplacement("java.lang.IllegalArgumentException: column index may not be negative, but had .*");
        assertEquals("java.lang.IllegalArgumentException: column index may not be negative, but had *",
                replacement);

        replacement = BugAnnotations.getReplacement("o.a.p.ss.formula.FormulaParseException: The column doesn't exist in table ProjectTimelineData");
        assertEquals("o.a.p.ss.formula.FormulaParseException: The column doesn't exist in table *",
                replacement);

        replacement = BugAnnotations.getReplacement("org.apache.poi.ss.formula.FormulaParseException: The column doesn't exist in table ProjectTimelineData");
        assertEquals("o.a.p.ss.formula.FormulaParseException: The column doesn't exist in table *",
                replacement);

        replacement = BugAnnotations.getReplacement("o.a.p.ss.formula.FormulaParseException: The column doesn't exist in table PAYE");
        assertEquals("o.a.p.ss.formula.FormulaParseException: The column doesn't exist in table *",
                replacement);

        replacement = BugAnnotations.getReplacement("o.a.p.ss.formula.FormulaParseException: Illegal table name: 'tblRenda'");
        assertEquals("o.a.p.ss.formula.FormulaParseException: Illegal table name: *",
                replacement);

        replacement = BugAnnotations.getReplacement("org.apache.poi.ss.formula.FormulaParseException: Illegal table name: 'tblRenda'");
        assertEquals("o.a.p.ss.formula.FormulaParseException: Illegal table name: *",
                replacement);

        replacement = BugAnnotations.getReplacement("java.lang.IllegalArgumentException: Cannot access 0-based index 12 in point-array with 12 items");
        assertEquals("java.lang.IllegalArgumentException: Cannot access 0-based index * in point-array with * items",
                replacement);

        replacement = BugAnnotations.getReplacement("org.opentest4j.TestAbortedException: Assumption failed: File roadmap2050.eu_attachments_files_georgzachmann_ecf-carbonpricing_5b1_5d.pptx excluded because the Zip file is incomplete");
        assertEquals("org.opentest4j.TestAbortedException: Assumption failed: File * excluded because the Zip file is incomplete",
                replacement);

        replacement = BugAnnotations.getReplacement("java.io.IOException: The text piece table is corrupted, expected byte value 2 but had 0");
        assertEquals("java.io.IOException: The text piece table is corrupted, expected byte value * but had *",
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
