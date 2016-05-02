package de.uni_hannover.dcsec.plagiat.web;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.uni_hannover.dcsec.plagiat.Options;
import de.uni_hannover.dcsec.plagiat.Source;
import de.uni_hannover.dcsec.plagiat.file.Cleaner;
import de.uni_hannover.dcsec.plagiat.file.PDFReader;

public class ContentExtractor {

	private static Map<String, Source> extracted = new HashMap<String, Source>();
	private static Map<Integer, String> IDs = new HashMap<Integer, String>();
	private static int ID = 0;

	private static double pdfSize = 0, webSize = 0;

	private static final String[] name = { "Bytes", "KB", "MB", "GB", "TB", "PB" };

	public static void printStatistics() {
		System.out.println("Downloaded " + extracted.size() + " URL's");
		int i = 0;
		double temp = pdfSize;
		while (temp > 1024) {
			temp /= 1024;
			i++;
		}
		System.out.printf("PDF: %6.2f %s\n", temp, name[i]);
		i = 0;
		temp = webSize;
		while (temp > 1024) {
			temp /= 1024;
			i++;
		}
		System.out.printf("Web: %6.2f %s\n", temp, name[i]);
	}

	public static String getURL(int id) {
		if (id == -1)
			return null;
		return IDs.get(id);
	}

	public static Source getContent(String url) {
		// keep track of all visited sites to not download results twice.
		Source ret = extracted.get(url);
		if (ret != null) {
			if (Options.getDebuglevel() > 1)
				System.out.println("Resending " + url);
			return ret;
		}
		ret = null;
		if (Options.getDebuglevel() > 1)
			System.out.println("Fetching " + url);
		if (url.toLowerCase().endsWith("pdf"))
			ret = getPDFContent(url);
		else
			ret = getHTMLContent(url);
		if (ret != null && !Options.isReduceMemory())
			extracted.put(url, ret);
		return ret;
	}

	private static Source getPDFContent(String url) {
		File temp = new File("tmp.pdf");
		try {
			URL link = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(link.openStream());
			FileOutputStream fos = new FileOutputStream(temp);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (Exception e) {
			if (temp.exists())
				temp.delete();
			return null;
		}

		String text = PDFReader.getText(temp);
		text = Cleaner.clean(text);

		if (temp.exists()) {
			pdfSize += temp.length();
			temp.delete();
		}
		return stringToSource(text, url);
	}

	private static Source getHTMLContent(String url) {
		try {
			Response res = Jsoup.connect(url).userAgent("PlagTest").timeout(10000).execute();
			webSize += res.bodyAsBytes().length;
			Document doc = res.parse();
			return stringToSource(doc.text(), url);
		} catch (org.jsoup.HttpStatusException hse) {
			return null;
		} catch (org.jsoup.UnsupportedMimeTypeException mte) {
			if (mte.getMimeType().startsWith("application/pdf"))
				return getPDFContent(url);
			if (Options.getDebuglevel() > 0)
				System.err.println("Not a web page. It is " + mte.getMimeType());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Source stringToSource(String text, String url) {
		Vector<String> vec = Cleaner.toSentence(text);
		if (vec == null)
			return null;
		Source prev = null;
		Source first = null;
		for (String c : vec) {
			Source s = new Source(c, ID);
			s.setPrevious(prev);
			if (prev != null)
				prev.setNext(s);
			else
				first = s;
			prev = s;
		}
		IDs.put(ID++, url);
		return first;
	}
}
