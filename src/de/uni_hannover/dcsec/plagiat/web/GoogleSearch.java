package de.uni_hannover.dcsec.plagiat.web;

import java.util.Map;
import java.util.Vector;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.uni_hannover.dcsec.plagiat.Options;

/**
 * Class for making use of the Google search engine.
 * 
 * @author pflug
 *
 */
public class GoogleSearch {

	/**
	 * Temporary storage of the cookies set by google.
	 */
	private static Map<String, String> cookies = null;

	/**
	 * Searches for a String on Google and parses the results from the first
	 * page. The links are returned.
	 * 
	 * @param text
	 *            The search string.
	 * @return Vector with links to results.
	 */
	public static Vector<String> search(String text) {
		Document doc = null;
		Vector<String> ret = new Vector<String>();
		try {
			Connection con = Jsoup.connect("https://www.google.de/search?q=" + text).userAgent("PlagSearch");
			if (cookies != null)
				con.cookies(cookies);
			Response res = con.execute();
			cookies = res.cookies();
			doc = res.parse();
			Element e = doc.select("div[id=res]").first();
			for (Element el : e.select("div[class=g]")) {
				String s = el.select("a").first().attr("href");
				s = cleanURL(s);
				if (s != null)
					ret.add(s);
				if (ret.size() >= Options.getMaxlinks())
					return ret;
			}
		} catch (Exception e) {
			if (Options.getDebuglevel() > 0)
				e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Cleans google URL from unnecessary garbage.
	 * 
	 * @param url
	 *            A garbage URL
	 * @return cleaned version of the given URL.
	 */
	private static String cleanURL(String url) {
		if (url.startsWith("/url?q="))
			url = url.substring(7);
		else
			return null;
		if (url.contains("&"))
			url = url.substring(0, url.indexOf('&'));
		return url;
	}
}
