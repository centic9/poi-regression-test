package org.dstadler.commoncrawl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Object used when processing results via {@link ProcessResults}
 */
public class ResultItem {
	private static final JsonFactory FACTORY = new JsonFactory();

	private String fileName;
	private String exceptionText;
	private String exceptionStacktrace;
	private boolean timeout;
	private long duration;

	public static ResultItem parse(String json) throws IOException {
		/*
{"fileName":"02318700039446654763.googlegroups.com_attach_271ef2beadc342e8_winmail.dat_part\u003d2\u0026vt\u003danajvreft8mr2ydcfn9lomtld20npuksgy02uhwzq0f1mj7hp8bzo3vo6lzq-kaumdofid8qlk3ov50o4khr8h78mg2thvkkh_hdyyfwpzq66l7gfuvfym8.msg",
"exceptionText":"org.junit.AssumptionViolatedException: File 02318700039446654763.googlegroups.com_attach_271ef2beadc342e8_winmail.dat_part\u003d2\u0026vt\u003danajvreft8mr2ydcfn9lomtld20npuksgy02uhwzq0f1mj7hp8bzo3vo6lzq-kaumdofid8qlk3ov50o4khr8h78mg2thvkkh_hdyyfwpzq66l7gfuvfym8.msg is excluded because of known error Invalid header signature; read 0x06010F2E223E9F78, expected 0xE11AB1A1E011CFD0 - Your file appears not to be a valid OLE2 document: got: \u003corg.apache.poi.poifs.filesystem.NotOLE2FileException: Invalid header signature; read 0x06010F2E223E9F78, expected 0xE11AB1A1E011CFD0 - Your file appears not to be a valid OLE2 document\u003e, expected: null",
"exceptionStacktrace":"org.junit.AssumptionViolatedException: File 02318700039446654763.googlegroups.com_attach_271ef2beadc342e8_winmail.dat_part\u003d2\u0026vt\u003danajvreft8mr2ydcfn9lomtld20npuksgy02uhwzq0f1mj7hp8bzo3vo6lzq-kaumdofid8qlk3ov50o4khr8h78mg2thvkkh_hdyyfwpzq66l7gfuvfym8.msg is excluded because of known error Invalid header signature; read 0x06010F2E223E9F78, expected 0xE11AB1A1E011CFD0 - Your file appears not to be a valid OLE2 document: got: \u003corg.apache.poi.poifs.filesystem.NotOLE2FileException: Invalid header signature; read 0x06010F2E223E9F78, expected 0xE11AB1A1E011CFD0 - Your file appears not to be a valid OLE2 document\u003e, expected: null\n\tat org.junit.Assume.assumeThat(Assume.java:118)\n\tat org.junit.Assume.assumeNoException(Assume.java:156)\n\tat org.apache.poi.BaseIntegrationTest.test(BaseIntegrationTest.java:154)\n\tat org.dstadler.commoncrawl.ProcessFiles$FileHandlingRunnable.run(ProcessFiles.java:146)\n\tat org.dstadler.commoncrawl.ProcessFiles.main(ProcessFiles.java:63)\nCaused by: org.apache.poi.poifs.filesystem.NotOLE2FileException: Invalid header signature; read 0x06010F2E223E9F78, expected 0xE11AB1A1E011CFD0 - Your file appears not to be a valid OLE2 document\n\tat org.apache.poi.poifs.storage.HeaderBlock.\u003cinit\u003e(HeaderBlock.java:162)\n\tat org.apache.poi.poifs.storage.HeaderBlock.\u003cinit\u003e(HeaderBlock.java:112)\n\tat org.apache.poi.poifs.filesystem.NPOIFSFileSystem.\u003cinit\u003e(NPOIFSFileSystem.java:300)\n\tat org.apache.poi.hsmf.MAPIMessage.\u003cinit\u003e(MAPIMessage.java:103)\n\tat org.apache.poi.stress.HSMFFileHandler.handleFile(HSMFFileHandler.java:32)\n\tat org.apache.poi.BaseIntegrationTest.handleFile(BaseIntegrationTest.java:168)\n\tat org.apache.poi.BaseIntegrationTest.test(BaseIntegrationTest.java:103)\n\t... 2 more\n"}
		 */
		ResultItem item = new ResultItem();

    	try (JsonParser jp = FACTORY.createParser(json)) {
	    	while(jp.nextToken() != JsonToken.END_OBJECT) {
	    		if(jp.getCurrentToken() == JsonToken.VALUE_STRING) {
	    			String name = jp.getCurrentName();
					if("fileName".equals(name)) {
	    				item.fileName = jp.getValueAsString();
	    			} else if ("exceptionText".equals(name)) {
	    				item.exceptionText = jp.getValueAsString();
	    			} else if ("exceptionStacktrace".equals(name)) {
	    				item.exceptionStacktrace = jp.getValueAsString();
	    			} else if ("duration".equals(name)) {
	    				item.duration = jp.getValueAsLong();
	    			} else {
	    				throw new IllegalStateException("Unknown field found: " + name);
	    			}
	    		} else if(jp.getCurrentToken() == JsonToken.VALUE_TRUE) {
	    			String name = jp.getCurrentName();
					if ("timeout".equals(name)) {
	    				item.timeout = jp.getValueAsBoolean();
	    			} else {
	    				throw new IllegalStateException("Unknown field found: " + name);
	    			}
	    		}
	    	}
    	}

    	return item;
	}

	public String getFileName() {
		return fileName;
	}

	public String getExceptionText() {
		return exceptionText;
	}

	public String getExceptionStacktrace() {
		return exceptionStacktrace;
	}

	public boolean isTimeout() {
		return timeout;
	}

	public long getDuration() {
		return duration;
	}
}
