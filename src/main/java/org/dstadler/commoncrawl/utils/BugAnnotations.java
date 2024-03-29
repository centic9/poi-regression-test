package org.dstadler.commoncrawl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class which replaces some known exception-texts so we can combine
 * the same exception in reports even if they contain changing parts, e.g. some numbers
 * that are different for each exception.
 *
 * We usually update this with more for every release that is tested to list
 * each type of exception only once in the resulting reports.
 */
public class BugAnnotations {
    // define some annotations that are added to entries in the reports, e.g. bugs that are
    // fixed in the latest version already
    private static final Map<String,String> ANNOTATIONS = new HashMap<>();
    static {
        ANNOTATIONS.put("ReadOnlySharedStringsTable.getEntryAt(ReadOnlySharedStringsTable.java:182)","fixed");
        ANNOTATIONS.put("java.lang.ClassCastException: org.apache.poi.hssf.record.BOFRecord cannot be cast to org.apache.poi.hssf.record.TabIdRecord", "<a href=\"https://bz.apache.org/bugzilla/show_bug.cgi?id=55982\">Bug 55982</a>");
        ANNOTATIONS.put("java.lang.IllegalArgumentException: Date for created could not be parsed: ", "<a href=\"https://bz.apache.org/bugzilla/show_bug.cgi?id=59183\">Bug 59183</a>");
    }

    public static String getAnnotation(String stacktrace) {
        for(Map.Entry<String,String> entry : ANNOTATIONS.entrySet()) {
            if(stacktrace.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "";
    }

    private static final String FILE_PATTERN = ".+";

    // defines a pattern and the replacement string, all replacements are applied
    // on each exception during creating the report so we can combine equal items
    // and thus make the report much shorter and easier to work with
    private static final Map<Pattern,String> REPLACEMENTS = new HashMap<>();
    static {
        REPLACEMENTS.put(Pattern.compile("java.lang.IndexOutOfBoundsException: Block \\d+ not found"),
                "java.lang.IndexOutOfBoundsException: Block * not found");
        REPLACEMENTS.put(Pattern.compile("java.lang.NumberFormatException: For input string: \"\\d+\""),
                "java.lang.NumberFormatException: For input string: *");
        REPLACEMENTS.put(Pattern.compile("org.junit.AssumptionViolatedException: File " + FILE_PATTERN + " excluded because the Zip file is incomplete"),
                "org.junit.AssumptionViolatedException: File * excluded because the Zip file is incomplete");
        REPLACEMENTS.put(Pattern.compile("java.lang.RuntimeException: java.lang.ArrayIndexOutOfBoundsException: [0-9-]+"),
                "java.lang.RuntimeException: java.lang.ArrayIndexOutOfBoundsException: *");
        REPLACEMENTS.put(Pattern.compile("java.lang.ArrayIndexOutOfBoundsException: [0-9-]+"),
                "java.lang.ArrayIndexOutOfBoundsException: *");
        REPLACEMENTS.put(Pattern.compile("org.apache.poi.openxml4j.exceptions.InvalidOperationException: Can't open the specified file: '[A-za-z0-9._/-]+'"),
                "org.apache.poi.openxml4j.exceptions.InvalidOperationException: Can't open the specified file: *");
        REPLACEMENTS.put(Pattern.compile("org.junit.AssumptionViolatedException: File " + FILE_PATTERN + " excluded because it is unsupported old Excel format"),
                "org.junit.AssumptionViolatedException: File * excluded because it is unsupported old Excel format");
        REPLACEMENTS.put(Pattern.compile("org.opentest4j.TestAbortedException: Assumption failed: File " + FILE_PATTERN + " excluded because it is unsupported old Excel format"),
                "org.opentest4j.TestAbortedException: Assumption failed: File * excluded because it is unsupported old Excel format");
        REPLACEMENTS.put(Pattern.compile("org.opentest4j.TestAbortedException: Assumption failed: File " + FILE_PATTERN + " excluded because it is an unsupported old format"),
                "org.opentest4j.TestAbortedException: Assumption failed: File * excluded because it is an unsupported old format");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalStateException: Told we're for characters \\d+ -> \\d+, but actually covers \\d+ characters!"),
                "java.lang.IllegalStateException: Told we're for characters * -> *, but actually covers * characters!");
        REPLACEMENTS.put(Pattern.compile("java.lang.RuntimeException: java.lang.IllegalArgumentException: The end \\(\\d+\\) must not be before the start \\(\\d+\\)"),
                "java.lang.RuntimeException: java.lang.IllegalArgumentException: The end (*) must not be before the start (*)");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalArgumentException: Illegal length \\d+"),
                "java.lang.IllegalArgumentException: Illegal length *");
        REPLACEMENTS.put(Pattern.compile("org.junit.AssumptionViolatedException: File " + FILE_PATTERN + " excluded because it is actually a PDF/RTF file"),
                "org.junit.AssumptionViolatedException: File * excluded because it is actually a PDF/RTF file");
        REPLACEMENTS.put(Pattern.compile("org.junit.AssumptionViolatedException: File " + FILE_PATTERN + " excluded because it is password-encrypted"),
                "org.junit.AssumptionViolatedException: File * excluded because it is password-encrypted");
        REPLACEMENTS.put(Pattern.compile("org.apache.poi.hslf.exceptions.CorruptPowerPointFileException: The Current User stream must be at least \\d+ bytes long, but was only \\d+"),
                "org.apache.poi.hslf.exceptions.CorruptPowerPointFileException: The Current User stream must be at least * bytes long, but was only *");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalArgumentException: Position \\d+ past the end of the file"),
                "java.lang.IllegalArgumentException: Position * past the end of the file");
        REPLACEMENTS.put(Pattern.compile("java.io.IOException: Block count \\d+ is too high. POI maximum is 65535."),
                "java.io.IOException: Block count * is too high. POI maximum is 65535.");
        REPLACEMENTS.put(Pattern.compile("java.lang.AssertionError: Should get some text but had none for file [-a-zA-Z0-9/._]+"),
                "java.lang.AssertionError: Should get some text but had none for file *");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalArgumentException: Keyframe fractions must be increasing: [0-9.]+"),
                "java.lang.IllegalArgumentException: Keyframe fractions must be increasing: *");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalStateException: Invalid file format version number: \\d+"),
                "java.lang.IllegalStateException: Invalid file format version number: *");
        REPLACEMENTS.put(Pattern.compile("org.junit.AssumptionViolatedException: File .* excluded because the Zip file is incomplete"),
                "org.junit.AssumptionViolatedException: File * excluded because the Zip file is incomplete");
        REPLACEMENTS.put(Pattern.compile("java.lang.IndexOutOfBoundsException: Index [-0-9]+ out-of-bounds for length \\d+"),
                "java.lang.IndexOutOfBoundsException: Index * out-of-bounds for length *");
        REPLACEMENTS.put(Pattern.compile("Uncompressed size: \\d+, Raw/compressed size: \\d+, ratio: [\\d.,]+\\s*\n?\\s*Limits: MIN_INFLATE_RATIO: [\\d.,]+, Entry: .*"),
                "Uncompressed size: *, Raw/compressed size: *, ratio: * Limits: MIN_INFLATE_RATIO: *, Entry: *");
        REPLACEMENTS.put(Pattern.compile("org.apache.poi.ooxml.POIXMLException: org.apache.poi.ooxml.POIXMLException: org.apache.xmlbeans.XmlException: "),
                "org.apache.poi.ooxml.POIXMLException: ");
        REPLACEMENTS.put(Pattern.compile("org.apache.poi.ooxml.POIXMLException: org.apache.poi.ooxml.POIXMLException: "),
                "org.apache.poi.ooxml.POIXMLException: ");
        REPLACEMENTS.put(Pattern.compile("org.apache.poi.ooxml.POIXMLException: org.apache.xmlbeans.XmlException: "),
                "org.apache.poi.ooxml.POIXMLException: ");
        REPLACEMENTS.put(Pattern.compile("java\\.io\\.EOFException: unexpected EOF - expected len: \\d+ - actual len: \\d+"),
                "java.io.EOFException: unexpected EOF - expected len: * - actual len: *");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.StringIndexOutOfBoundsException: String index out of range: \\d+"),
                "java.lang.StringIndexOutOfBoundsException: String index out of range: *");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.IllegalArgumentException: Sheet index \\(\\d+\\) is out of range \\(\\d+\\.\\.\\d+\\)"),
                "java.lang.IllegalArgumentException: Sheet index * is out of range *");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.ArrayIndexOutOfBoundsException: Index -?\\d+ out of bounds for length \\d+"),
                "java.lang.ArrayIndexOutOfBoundsException: Index * out of bounds for length *");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.RuntimeException: Invalid built-in function index \\(\\d+\\)"),
                "java.lang.RuntimeException: Invalid built-in function index *");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.RuntimeException: bad function index \\(\\d+, (?:false|true)\\)"),
                "java.lang.RuntimeException: bad function index *");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.RuntimeException: Unexpected base token id \\(\\+\\)"),
                "java.lang.RuntimeException: Unexpected base token id *");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.ArrayIndexOutOfBoundsException: There are only \\d+ font records, but you asked for index \\d+"),
                "java.lang.ArrayIndexOutOfBoundsException: There are only * font records, but you asked for index *");
        REPLACEMENTS.put(Pattern.compile("com\\.sun\\.org\\.apache\\.xerces\\.internal\\.impl\\.io\\.MalformedByteSequenceException: Ung\u00FCltiges Byte \\d+ von \\d+-Byte-UTF-8-Sequenz."),
                "com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException: Ung\u00FCltiges Byte * von *-Byte-UTF-8-Sequenz.");
        REPLACEMENTS.put(Pattern.compile("java\\.io\\.FileNotFoundException: no such entry: \"(WordDocument|VisioDocument|PowerPoint Document)\", had: \\[[-a-zA-Z0-9_ ,.#\u0001-\u0006]*]"),
                "java.io.FileNotFoundException: no such entry: \"$1\", had: [*]");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.IllegalArgumentException: The end \\(\\d+\\) must not be before the start \\(\\d+\\)"),
                "java.lang.IllegalArgumentException: The end * must not be before the start *");
        REPLACEMENTS.put(Pattern.compile("java\\.lang\\.IllegalArgumentException: Invalid cell range, having lastRow < firstRow \\|\\| lastCol < firstCol, had rows \\d+ >= \\d+ or cells \\d+ >= \\d+"),
                "java.lang.IllegalArgumentException: Invalid cell range, having lastRow < firstRow || lastCol < firstCol, had rows * >= * or cells * >= *");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalArgumentException: Invalid char \\(.\\) found at index \\([0-9]+\\) in sheet name '.*'"),
                "java.lang.IllegalArgumentException: Invalid char (*) found at index (*) in sheet name *");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalArgumentException: newLimit > capacity: \\([0-9 >]+\\)"),
                "java.lang.IllegalArgumentException: newLimit > capacity: (*)");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalArgumentException: Width \\(\\d+\\) and height \\(\\d+\\) cannot be <= 0"),
                "java.lang.IllegalArgumentException: Width (*) and height (*) cannot be <= 0");
        REPLACEMENTS.put(Pattern.compile("java.io.IOException: Unsupported blocksize \\(2\\^\\d+\\)\\. Expected 2\\^9 or 2\\^12\\."),
                "java.io.IOException: Unsupported blocksize (*). Expected 2^9 or 2^12.");
        REPLACEMENTS.put(Pattern.compile("org.apache.xmlbeans.SchemaTypeLoaderException: XML-BEANS compiled schema: Could not locate compiled schema resource org/apache/poi/[-a-zA-Z0-9._/]+ \\(o.a.p.[-a-zA-Z0-9._/]+\\) - code 0"),
                "org.apache.xmlbeans.SchemaTypeLoaderException: XML-BEANS compiled schema: Could not locate compiled schema resource org/apache/poi/* (o.a.p.*) - code 0");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalStateException: Category and values must have the same point count, but had \\d+ categories and \\d+ values\\."),
                "java.lang.IllegalStateException: Category and values must have the same point count, but had * categories and * values.");
        REPLACEMENTS.put(Pattern.compile("o.a.p.ss.formula.eval.NotImplementedException: Error evaluating cell .*"),
                "o.a.p.ss.formula.eval.NotImplementedException: Error evaluating cell *");
        REPLACEMENTS.put(Pattern.compile("java.lang.RuntimeException: Could not resolve external workbook name .*\\. The following workbook names are valid: \\(.*\\)"),
                "java.lang.RuntimeException: Could not resolve external workbook name * The following workbook names are valid: *");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalArgumentException: column index may not be negative, but had .*"),
                "java.lang.IllegalArgumentException: column index may not be negative, but had *");
        REPLACEMENTS.put(Pattern.compile("o.a.p.ss.formula.FormulaParseException: The column doesn't exist in table .*"),
                "o.a.p.ss.formula.FormulaParseException: The column doesn't exist in table *");
        REPLACEMENTS.put(Pattern.compile("o.a.p.ss.formula.FormulaParseException: Illegal table name: .*"),
                "o.a.p.ss.formula.FormulaParseException: Illegal table name: *");
        REPLACEMENTS.put(Pattern.compile("org.apache.poi.ss.formula.FormulaParseException: Illegal table name: .*"),
                "org.apache.poi.ss.formula.FormulaParseException: Illegal table name: *");
        REPLACEMENTS.put(Pattern.compile("java.lang.IllegalArgumentException: Cannot access 0-based index \\d+ in point-array with \\d+ items"),
                "java.lang.IllegalArgumentException: Cannot access 0-based index * in point-array with * items");
        REPLACEMENTS.put(Pattern.compile("org.opentest4j.TestAbortedException: Assumption failed: File .* excluded because the Zip file is incomplete"),
                "org.opentest4j.TestAbortedException: Assumption failed: File * excluded because the Zip file is incomplete");
        REPLACEMENTS.put(Pattern.compile("java.io.IOException: The text piece table is corrupted, expected byte value \\d+ but had \\d+"),
                "java.io.IOException: The text piece table is corrupted, expected byte value * but had *");
        REPLACEMENTS.put(Pattern.compile("org.opentest4j.TestAbortedException: Assumption failed: File .* excluded because it is password-encrypted"),
                "org.opentest4j.TestAbortedException: Assumption failed: File * excluded because it is password-encrypted");
    }

    public static String getReplacement(String exception) {
        exception = exception.trim().replace("org.apache.poi", "o.a.p");

        for(Map.Entry<Pattern,String> entry : REPLACEMENTS.entrySet()) {
            Matcher matcher = entry.getKey().matcher(exception);
            if(matcher.find()) {
                return matcher.replaceFirst(entry.getValue());
            }
        }

        return exception;
    }
}
