package org.dstadler.commoncrawl;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class TikaTest {
    @Ignore("Only used for verification, test depends on local files")
    @Test
    public void testFileTypeDetectionCC() {
        //runFiles(new File("../download"), true);
    }

    @Ignore("Only used for verification, test depends on local files")
    @Test
    public void testFileTypeDetectionTimVM() {
        //runFiles(new File("../TimVM"), false);
    }

    @Ignore("Only used for verification, test depends on local files")
    @Test
    public void testFileTypeDetectionPOI() {
        //runFiles(new File("/opt/poi"), true);
    }

    /*void runFiles(File rootDir, boolean checkType) throws IOException {
        TikaFileTypeDetector detector = new TikaFileTypeDetector();

        MappedCounter<String> types = new MappedCounterImpl<>();

        Collection<Pair<String,FileHandler>> files = POIFileScanner.scan(rootDir);
        System.out.println("Found " + files.size() + " files");
        int idx = 1;
        for (final Pair<String,FileHandler> pair : files) {
            String fileName = pair.getLeft();
            String type = detector.probeContentType(Paths.get(rootDir.getAbsolutePath(), fileName));
            //System.out.println("Type for file " + fileName + ": " + type + ": " + MimeTypes.toExtension(type));
            assertTrue(idx + "/" + files.size() + ": Had: " + fileName + ": " + type + " and " + MimeTypes.toExtension(type),
                    !checkType || type == null || !MimeTypes.toExtension(type).equals(""));

            if(type == null &&
                    // some file-types that seem to have no mimetype yet
                    !fileName.endsWith(".xsb") &&
                    !fileName.endsWith(".exec")) {
                System.out.println(idx + "/" + files.size() + ": Unknown type of file: " + fileName);
            }

            types.addInt(type, 1);
            idx++;
        }

        System.out.println("Had: " + types.sortedMap().toString());
    }*/

    @Ignore("Only used for verification, test depends on local files")
    @Test
    public void testMimeTypeToFileHandlerPOI() {
        //runMimeTypeToFileHandler(new File("/opt/poi/test-data"), true);
    }

    @Ignore("Only used for verification, test depends on local files")
    @Test
    public void testMimeTypeToFileHandlerTimVM() {
        //runMimeTypeToFileHandler(new File("../TimVM"), false);
    }

    @Ignore("Only used for verification, test depends on local files")
    @Test
    public void testMimeTypeToFileHandlerCC() {
        //runMimeTypeToFileHandler(new File("../download"), false);
    }

    /*private void runMimeTypeToFileHandler(File rootDir, boolean requireType) throws IOException {
        MappedCounter<String> types = new MappedCounterImpl<>();

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(rootDir);

        scanner.setExcludes(TestAllFiles.SCAN_EXCLUDES);

        System.out.println("Scanning for files in " + rootDir);

        scanner.scan();

        // getIncludedFiles() is a costly operation, don't run it inside the loop!
        String[] includedFiles = scanner.getIncludedFiles();

        System.out.println("Handling " + includedFiles.length + " files");

        TikaFileTypeDetector detector = new TikaFileTypeDetector();
        int idx = 1;
        for(String file : includedFiles) {
            // some cannot be detected
            if(file.equals("ddf/47143.dat") || file.equals("ddf/Container.dat")) {
                types.addInt("unknown-dat", 1);
                idx++;
                continue;
            }

            String type = detector.probeContentType(Paths.get(rootDir.getAbsolutePath(), file));

            if(requireType) {
                assertNotNull(idx + "/" + includedFiles.length + ": No mimetype found for " + file, type);
            } else if(type == null) {
                System.out.println("No type found for " + file);
            }

            if(type != null &&
                    FileHandlerFactory.getHandler(type) == null &&
                    !IGNORED_MIMETYPES.contains(type)) {
//                assertNotNull(idx + "/" + includedFiles.length + ": No handler found for " + file + " and type " + type,
//                        FileHandlerFactory.getHandler(type));
                System.out.println(idx + "/" + scanner.getIncludedFiles().length + ": No handler found for " + file + " and type " + type);
            }

            types.addInt(type, 1);
            idx++;

            if(idx % 1000 == 0) {
                System.out.print(".");
                if(idx % 100000 == 0) {
                    System.out.println();
                }
            }
        }

        System.out.println("Had: " + types.sortedMap().toString());
    }*/

    private static Set<String> IGNORED_MIMETYPES = new HashSet<>();
    static {
        IGNORED_MIMETYPES.add("text/plain");
        IGNORED_MIMETYPES.add("image/png");
        IGNORED_MIMETYPES.add("application/xml");
        IGNORED_MIMETYPES.add("application/x-pkcs12");
        IGNORED_MIMETYPES.add("application/x-msaccess");
        IGNORED_MIMETYPES.add("image/jpeg");
        IGNORED_MIMETYPES.add("audio/x-wav");
        IGNORED_MIMETYPES.add("application/x-msmetafile");
        IGNORED_MIMETYPES.add("image/x-ms-bmp");
        IGNORED_MIMETYPES.add("application/vnd.ms-xpsdocument");
        IGNORED_MIMETYPES.add("application/x-emf");
        IGNORED_MIMETYPES.add("application/pdf");
        IGNORED_MIMETYPES.add("text/html");
        IGNORED_MIMETYPES.add("application/rtf");
        IGNORED_MIMETYPES.add("application/x-corelpresentations");
        IGNORED_MIMETYPES.add("application/vnd.ms-project");
        IGNORED_MIMETYPES.add("image/gif");
        IGNORED_MIMETYPES.add("application/sldworks");
        IGNORED_MIMETYPES.add("application/zip");
        IGNORED_MIMETYPES.add("application/x-quattro-pro");
        IGNORED_MIMETYPES.add("application/vnd.ms-works");
        IGNORED_MIMETYPES.add("image/x-pict");
        IGNORED_MIMETYPES.add("image/svg+xml");
        IGNORED_MIMETYPES.add("text/x-matlab");
        IGNORED_MIMETYPES.add("application/epub+zip");
        IGNORED_MIMETYPES.add("image/vnd.dwg");
        IGNORED_MIMETYPES.add("application/xhtml+xml");
        IGNORED_MIMETYPES.add("application/x-bibtex-text-file");
        IGNORED_MIMETYPES.add("image/tiff");
        IGNORED_MIMETYPES.add("message/rfc822");
        IGNORED_MIMETYPES.add("application/x-debian-package");
        IGNORED_MIMETYPES.add("text/x-diff");
        IGNORED_MIMETYPES.add("application/x-123");
        IGNORED_MIMETYPES.add("image/jp2");
        IGNORED_MIMETYPES.add("image/vnd.microsoft.icon");
        IGNORED_MIMETYPES.add("audio/mpeg");
        IGNORED_MIMETYPES.add("application/vnd.google-earth.kmz");
        IGNORED_MIMETYPES.add("text/x-vcard");
        IGNORED_MIMETYPES.add("image/vnd.dxf; format=ascii");
        IGNORED_MIMETYPES.add("text/x-python");
        IGNORED_MIMETYPES.add("application/rss+xml");
        IGNORED_MIMETYPES.add("text/x-perl");
        IGNORED_MIMETYPES.add("application/x-executable");
        IGNORED_MIMETYPES.add("audio/x-aiff");
        IGNORED_MIMETYPES.add("application/x-lha");
        IGNORED_MIMETYPES.add("application/gzip");
        IGNORED_MIMETYPES.add("application/vnd.oasis.opendocument.graphics");
        IGNORED_MIMETYPES.add("application/x-sh");
        IGNORED_MIMETYPES.add("application/x-tex");
        IGNORED_MIMETYPES.add("text/x-csrc");
        IGNORED_MIMETYPES.add("application/x-dvi");
        IGNORED_MIMETYPES.add("application/vnd.google-earth.kml+xml");
    }

    @Ignore("Only a local test")
    @Test
    public void testOneFile() {
        /*TikaFileTypeDetector detector = new TikaFileTypeDetector();

        String type = detector.probeContentType(Paths.get("../download/download.oldindex/ca.vancouver_commsvcs_cultural_gasp_grants_documents_CNADPappform2012.doc"));
        assertNotNull("No mimetype found", type);*/
    }
}
