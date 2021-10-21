package org.dstadler.commoncrawl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.stress.FileHandler;
import org.apache.poi.stress.HDGFFileHandler;
import org.apache.poi.stress.HMEFFileHandler;
import org.apache.poi.stress.HPBFFileHandler;
import org.apache.poi.stress.HPSFFileHandler;
import org.apache.poi.stress.HSLFFileHandler;
import org.apache.poi.stress.HSMFFileHandler;
import org.apache.poi.stress.HSSFFileHandler;
import org.apache.poi.stress.HWPFFileHandler;
import org.apache.poi.stress.OPCFileHandler;
import org.apache.poi.stress.POIFSFileHandler;
import org.apache.poi.stress.TestAllFiles;
import org.apache.poi.stress.XDGFFileHandler;
import org.apache.poi.stress.XSLFFileHandler;
import org.apache.poi.stress.XSSFBFileHandler;
import org.apache.poi.stress.XSSFFileHandler;
import org.apache.poi.stress.XWPFFileHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Helper class to scan a folder for files and return a collection of
 * found files together with the matching {@link FileHandler}.
 *
 * Can also be used to get the appropriate FileHandler for a single file.
 */
class POIFileScanner {
    /**
     * Scan a folder for files and return a collection of
     * found files together with the matching {@link FileHandler}.
     *
     * Note: unknown files will be assigned to {@link NullFileHandler}
     *
     * @param rootDir The directory to scan
     * @return A collection with file-FileHandler pairs which can be used for running tests on that file
     * @throws IOException If determining the file-type fails
     */
    public static Collection<Map.Entry<String, FileHandler>> scan(File rootDir) throws IOException {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(rootDir);

        scanner.setExcludes(TestAllFiles.SCAN_EXCLUDES);

        System.out.println("Scanning for files in " + rootDir);

        scanner.scan();

        String[] includedFiles = scanner.getIncludedFiles();
        System.out.println("Handling " + includedFiles.length + " files");

        List<Map.Entry<String, FileHandler>> files = new ArrayList<>();
        for(String file : includedFiles) {
            // breaks files with slash in their name on Linux:
            // file = file.replace('\\', '/'); // ... failures/handlers lookup doesn't work on windows otherwise

            FileHandler fileHandler = getFileHandler(rootDir, file);

            files.add(new AbstractMap.SimpleImmutableEntry<>(file, fileHandler));

            if(files.size() % 100 == 0) {
                System.out.print(".");
                if(files.size() % 100_000 == 0) {
                    System.out.println(file);
                }
            }
        }
        System.out.println();

        return files;
    }

    private static final boolean IGNORE_SCRATCHPAD = false;

    // map file extensions to the actual mappers
    public static final Map<String, FileHandler> HANDLERS = new HashMap<>();

    static {
        // Excel
        HANDLERS.put(".xls", new HSSFFileHandler());
        HANDLERS.put(".xlsx", new XSSFFileHandler());
        HANDLERS.put(".xlsm", new XSSFFileHandler());
        HANDLERS.put(".xltx", new XSSFFileHandler());
        HANDLERS.put(".xlsb", new XSSFBFileHandler());

        // Word
        HANDLERS.put(".doc", IGNORE_SCRATCHPAD ? new HPSFFileHandler() : new HWPFFileHandler());
        HANDLERS.put(".docx", new XWPFFileHandler());
        HANDLERS.put(".dotx", new XWPFFileHandler());
        HANDLERS.put(".docm", new XWPFFileHandler());

        // OpenXML4J files
        HANDLERS.put(".ooxml", new OPCFileHandler());
        HANDLERS.put(".zip", new OPCFileHandler());

        // Powerpoint
        HANDLERS.put(".ppt", IGNORE_SCRATCHPAD ? new HPSFFileHandler() : new HSLFFileHandler());
        HANDLERS.put(".pot", IGNORE_SCRATCHPAD ? new HPSFFileHandler() : new HSLFFileHandler());
        HANDLERS.put(".pptx", new XSLFFileHandler());
        HANDLERS.put(".pptm", new XSLFFileHandler());
        HANDLERS.put(".ppsm", new XSLFFileHandler());
        HANDLERS.put(".ppsx", new XSLFFileHandler());
        HANDLERS.put(".thmx", new XSLFFileHandler());
        HANDLERS.put(".potx", new XSLFFileHandler());

        // Outlook
        HANDLERS.put(".msg", IGNORE_SCRATCHPAD ? new HPSFFileHandler() : new HSMFFileHandler());

        // Publisher
        HANDLERS.put(".pub", IGNORE_SCRATCHPAD ? new HPSFFileHandler() : new HPBFFileHandler());

        // Visio - binary
        HANDLERS.put(".vsd", IGNORE_SCRATCHPAD ? new HPSFFileHandler() : new HDGFFileHandler());

        // Visio - ooxml
        HANDLERS.put(".vsdm", new XDGFFileHandler());
        HANDLERS.put(".vsdx", new XDGFFileHandler());
        HANDLERS.put(".vssm", new XDGFFileHandler());
        HANDLERS.put(".vssx", new XDGFFileHandler());
        HANDLERS.put(".vstm", new XDGFFileHandler());
        HANDLERS.put(".vstx", new XDGFFileHandler());

        // Visio - not handled yet
        HANDLERS.put(".vst", NullFileHandler.instance);
        HANDLERS.put(".vss", NullFileHandler.instance);

        // POIFS
        HANDLERS.put(".ole2", new POIFSFileHandler());

        // Microsoft Admin Template?
        HANDLERS.put(".adm", new HPSFFileHandler());

        // Microsoft TNEF
        HANDLERS.put(".dat", IGNORE_SCRATCHPAD ? new HPSFFileHandler() : new HMEFFileHandler());

        // TODO: are these readable by some of the formats?
        HANDLERS.put(".wri", NullFileHandler.instance);
        HANDLERS.put(".shw", NullFileHandler.instance);
        HANDLERS.put(".zvi", NullFileHandler.instance);
        HANDLERS.put(".mpp", NullFileHandler.instance);
        HANDLERS.put(".qwp", NullFileHandler.instance);
        HANDLERS.put(".wps", NullFileHandler.instance);
        HANDLERS.put(".bin", NullFileHandler.instance);
        HANDLERS.put(".xps", NullFileHandler.instance);
        HANDLERS.put(".sldprt", NullFileHandler.instance);
        HANDLERS.put(".mdb", NullFileHandler.instance);
        HANDLERS.put(".vml", NullFileHandler.instance);

        // ignore some file types, images, other formats, ...
        HANDLERS.put(".txt", NullFileHandler.instance);
        HANDLERS.put(".pdf", NullFileHandler.instance);
        HANDLERS.put(".rtf", NullFileHandler.instance);
        HANDLERS.put(".gif", NullFileHandler.instance);
        HANDLERS.put(".html", NullFileHandler.instance);
        HANDLERS.put(".png", NullFileHandler.instance);
        HANDLERS.put(".wmf", NullFileHandler.instance);
        HANDLERS.put(".emf", NullFileHandler.instance);
        HANDLERS.put(".dib", NullFileHandler.instance);
        HANDLERS.put(".svg", NullFileHandler.instance);
        HANDLERS.put(".pict", NullFileHandler.instance);
        HANDLERS.put(".jpg", NullFileHandler.instance);
        HANDLERS.put(".jpeg", NullFileHandler.instance);
        HANDLERS.put(".tif", NullFileHandler.instance);
        HANDLERS.put(".tiff", NullFileHandler.instance);
        HANDLERS.put(".wav", NullFileHandler.instance);
        HANDLERS.put(".xml", NullFileHandler.instance);
        HANDLERS.put(".csv", NullFileHandler.instance);
        HANDLERS.put(".ods", NullFileHandler.instance);
        HANDLERS.put(".ttf", NullFileHandler.instance);
        HANDLERS.put(".fntdata", NullFileHandler.instance);
        // VBA source files
        HANDLERS.put(".vba", NullFileHandler.instance);
        HANDLERS.put(".bas", NullFileHandler.instance);
        HANDLERS.put(".frm", NullFileHandler.instance);
        HANDLERS.put(".frx", NullFileHandler.instance); //binary
        HANDLERS.put(".cls", NullFileHandler.instance);

        // map some files without extension
        HANDLERS.put("spreadsheet/BigSSTRecord", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecord2", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecord2CR1", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecord2CR2", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecord2CR3", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecord2CR4", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecord2CR5", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecord2CR6", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecord2CR7", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/BigSSTRecordCR", NullFileHandler.instance);
        HANDLERS.put("spreadsheet/test_properties1", NullFileHandler.instance);

        // keystore files
        HANDLERS.put(".pfx", NullFileHandler.instance);
        HANDLERS.put(".pem", NullFileHandler.instance);
        HANDLERS.put(".jks", NullFileHandler.instance);
        HANDLERS.put(".pkcs12", NullFileHandler.instance);
    }

    /**
     * Get the FileHandler for a single file
     *
     * @param rootDir The directory where the file resides
     * @param file The name of the file without directory
     * @return The matching {@link FileHandler}, A {@link NullFileHandler}
     *          is returned if no match is found
     * @throws IOException If determining the file-type fails
     */
    protected static FileHandler getFileHandler(File rootDir, String file) throws IOException {
        FileHandler fileHandler = HANDLERS.get(getExtension(file));
        if(fileHandler == null) {
            // we could not detect a type of file based on the extension, so we
            // need to take a close look at the file
            fileHandler = detectUnnamedFile(rootDir, file);
        }
        return fileHandler;
    }


    public static String getExtension(String file) {
        int pos = file.lastIndexOf('.');
        if(pos == -1 || pos == file.length()-1) {
            return file;
        }

        return file.substring(pos).toLowerCase(Locale.ROOT);
    }

    static FileHandler detectUnnamedFile(File rootDir, String file) throws IOException {
        File testFile = new File(rootDir, file);

        // find out if it looks like OLE2 (HSSF, HSLF, HWPF, ...) or OOXML (XSSF, XSLF, XWPF, ...)
        // and then determine the file type accordingly
        FileMagic magic = FileMagic.valueOf(testFile);
        switch (magic) {
            case OLE2: {
                try {
                    try (POIFSFileSystem fs = new POIFSFileSystem(testFile, true)) {
                        HSSFWorkbook.getWorkbookDirEntryName(fs.getRoot());
                    }

                    // we did not get an exception, so it seems this is a HSSFWorkbook
                    return HANDLERS.get(".xls");
                } catch (IOException | RuntimeException e) {
                    try {
                        try (FileInputStream istream = new FileInputStream(testFile)) {
                            try (HWPFDocument ignored = new HWPFDocument(istream)) {
                                // seems to be a valid document
                                return HANDLERS.get(".doc");
                            }
                        }
                    } catch (IOException | RuntimeException e2) {
                        System.out.println("Could not open POIFSFileSystem for OLE2 file " + testFile + ": " + e + " and " + e2);
                        return NullFileHandler.instance;
                    }
                }
            }
            case OOXML: {
                try {
                    try (Workbook ignored = WorkbookFactory.create(testFile, null, true)) {
                        // seems to be a valid workbook
                        return HANDLERS.get(".xlsx");
                    }
                } catch (IOException | RuntimeException e) {
                    try {
                        try (FileInputStream is = new FileInputStream(testFile)) {
                            try (XWPFDocument ignored = new XWPFDocument(is)) {
                                // seems to be a valid document
                                return HANDLERS.get(".docx");
                            }
                        }
                    } catch (IOException | RuntimeException e2) {
                        System.out.println("Could not open POIFSFileSystem for OOXML file " + testFile + ": " + e + " and " + e2);
                        return NullFileHandler.instance;
                    }
                }
            }

            // do not warn about a few detected file types
            case RTF:
            case PDF:
            case HTML:
            case XML:
            case JPEG:
            case GIF:
            case TIFF:
            case WMF:
            case EMF:
            case BMP:
                return NullFileHandler.instance;
        }

        System.out.println("Did not get a handler for extension " + getExtension(file) +
                " of file " + file + ": " + magic);
        return NullFileHandler.instance;
    }
}
