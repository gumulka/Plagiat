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

/**
 * Downloads and extracts content from a given URL.
 */
public class ContentExtractor {

	/**
	 * Map of all downladed URL's and the corresponding parsed texts.
	 */
	private static Map<String, Source> extracted = new HashMap<String, Source>();
	/**
	 * Vector of all visited URLs.
	 */
	private static Vector<String> IDs = new Vector<String>();

	/**
	 * Counter for how many bytes have been downloaded by the program.
	 */
	private static double pdfSize = 0, webSize = 0;

	private static final String[] name = { "Bytes", "KB", "MB", "GB", "TB", "PB" };

	/**
	 * Prints out statistics about how much has been downloaded.
	 */
	public static void printStatistics() {
		System.out.println("Downloaded " + IDs.size() + " URL's");
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

	/**
	 * Returns the URL to a given ID.
	 * 
	 * Since it is more memory efficient to store an integer instead of a
	 * String. Every Sentence of a source only stores an ID for its source URL
	 * and not the complete URL. This process can be reverted using this method.
	 * 
	 * @param id
	 *            the ID of a URL
	 * @return The corresponding URL.
	 */
	public static String getURL(int id) {
		if (id == -1 || id > IDs.size())
			return null;
		return IDs.get(id);
	}

	/**
	 * Downloads an URL and parses the content into a Source for comparing.
	 * 
	 * @param url
	 *            The URL to download.
	 * @return The content of the URL as concatenated sources.
	 */
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
		int ID = IDs.size();
		IDs.add(url);
		for (String c : vec) {
			Source s = new Source(c, ID);
			s.setPrevious(prev);
			if (prev != null)
				prev.setNext(s);
			else
				first = s;
			prev = s;
		}
		return first;
	}
}
