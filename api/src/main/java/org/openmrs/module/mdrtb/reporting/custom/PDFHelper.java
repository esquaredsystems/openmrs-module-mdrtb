package org.openmrs.module.mdrtb.reporting.custom;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

@SuppressWarnings({ "deprecation" })
public class PDFHelper {
	
	public String c(String str) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();//1<<10
		OutputStreamWriter osw = null;
		osw = new OutputStreamWriter(baos, "UTF-8");
		osw.write(str);
		osw.flush();
		return baos.toString("UTF-8");
	}
	
	public ByteArrayOutputStream createAndDownloadPdf(String html) throws IOException, DocumentException {
		Document document = new Document(PageSize.A4_LANDSCAPE);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();
		byte[] bytes = html.getBytes("UTF-8");
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		XMLWorkerFontProvider provider = new XMLWorkerFontProvider("resources/fonts/");
		XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
		worker.parseXHtml(writer, document, bais, null, Charset.forName("cp1252"), provider);
		document.close();
		return baos;
	}
	
	public boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean isString(Integer value) {
		try {
			Integer.toString(value);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public List<String> byteToStrArray(String str) {
		String arr[] = (str.replaceAll(" ", "").replace("[", "").replace("]", "")).split(",");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < arr.length; i++) {
			list.add(arr[i]);
		}
		return list;
	}
	
	public byte[] compress(final String str) throws IOException {
		if ((str == null) || (str.length() == 0)) {
			return null;
		}
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.flush();
		gzip.close();
		return obj.toByteArray();
	}
	
	public String decompress(final byte[] compressed) throws IOException {
		final StringBuilder outStr = new StringBuilder();
		if ((compressed == null) || (compressed.length == 0)) {
			return "";
		}
		if (isCompressed(compressed)) {
			final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
			
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				outStr.append(line);
			}
		} else {
			outStr.append(compressed);
		}
		return outStr.toString();
	}
	
	public boolean isCompressed(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
		        && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}
}
