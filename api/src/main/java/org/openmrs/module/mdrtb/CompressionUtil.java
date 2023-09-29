/**
 * 
 */
package org.openmrs.module.mdrtb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.core.util.Base64Encoder;

public class CompressionUtil {
	
	public static String compressCode(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return (new Base64Encoder().encode(out.toByteArray()));
	}
	
	public static String decompressCode(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		byte[] bytes = new Base64Encoder().decode(str);
		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
		InputStreamReader reader = new InputStreamReader(gis, "UTF-8"); // Specify UTF-8 here
		return IOUtils.toString(reader);
	}
}
