package org.dstadler.commoncrawl;

import org.apache.poi.stress.FileHandler;

import java.io.File;
import java.io.InputStream;

public class NullFileHandler implements FileHandler {
    public static final FileHandler instance = new NullFileHandler();

    @Override
    public void handleFile(InputStream inputStream, String s) throws Exception {

    }

    @Override
    public void handleExtracting(File file) throws Exception {

    }

    @Override
    public void handleAdditional(File file) throws Exception {

    }
}
