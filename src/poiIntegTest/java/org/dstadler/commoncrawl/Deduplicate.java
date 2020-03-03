package org.dstadler.commoncrawl;

import com.google.common.collect.TreeMultimap;
import org.apache.commons.io.FileUtils;
import org.apache.poi.POIFileScanner;
import org.apache.poi.stress.FileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;

import static org.dstadler.commoncrawl.ProcessFiles.BACKUP_DIR;
import static org.dstadler.commoncrawl.ProcessFiles.ROOT_DIR;

/**
 * Find duplicates in the test-corpus and copy them to a
 * backup-directory to reduce the run-time of the regression-tests
 * by not testing duplicate files multiple times.
 */
public class Deduplicate {
    public static void main(String[] args) throws IOException {
        Collection<Map.Entry<String, FileHandler>> files =
                POIFileScanner.scan(ROOT_DIR);

        System.out.println("Handling " + files.size() + " files");
        TreeMultimap<Long, String> sizes = TreeMultimap.create();

        for (Map.Entry<String, FileHandler> file : files) {
            String fileName = file.getKey();
            sizes.put(new File(ROOT_DIR, fileName).length(), fileName);
        }

        //System.out.println("Having files with 2 bytes: " + sizes.get(2L));
        NavigableSet<Long> sizesKeys = sizes.keySet();
        System.out.println("Having " + sizesKeys.size() + " different sizes between " + sizesKeys.first() + " and " + sizesKeys.last());

        int duplicates = 0;
        int count = 0;
        for (Long sizesKey : sizesKeys) {
            NavigableSet<String> sizeFiles = sizes.get(sizesKey);
            if(sizeFiles.size() == 1) {
                System.out.println("Only having " + sizeFiles.size() + " files with size " + sizesKey + ", " + (sizes.size() - count) + " files left");
                continue;
            }
            System.out.println("Looking at " + sizeFiles.size() + " files with size " + sizesKey + ", " + (sizes.size() - count) + " files left");

            Map<String, String> hashes = new HashMap<>();
            for (String file : sizeFiles) {
                count++;
                String hash = hash(new File(ROOT_DIR, file));
                if(hashes.containsKey(hash)) {
                    duplicates++;
                    System.out.println(duplicates + "/" + count + "/" + sizesKey + ": File " + file + " is the same as " + hashes.get(hash));

                    FileUtils.moveFile(new File(ROOT_DIR, file), new File(BACKUP_DIR, file));
                }

                hashes.put(hash, file);
            }
        }
        System.out.println("Found " + duplicates + " duplicate files");
    }

    private static String hash(File file) throws IOException {
        try (InputStream fis = new FileInputStream(file)) {
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        }
    }
}
