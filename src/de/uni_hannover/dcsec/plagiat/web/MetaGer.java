package de.uni_hannover.dcsec.plagiat.web;

import java.util.Vector;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.uni_hannover.dcsec.plagiat.Options;

/**
 * Class for making use of the MetaGer search engine.
 * 
 * @author pflug
 *
 */
public class MetaGer {

	/**
	 * Searches for a String on MetaGer and parses the results from the first
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
			Connection con = Jsoup.connect("https://metager.de/meta/meta.ger3");
			con.data("focus", "wissenschaft");
			con.data("encoding", "utf8");
			con.data("lang", "all");
			con.data("eingabe", text);
			doc = con.get();
			for (Element e : doc.select("div[class=result]")) {
				String s = e.select("a[class=title]").first().attr("href");
				if (s != null)
					ret.add(s);
			}
			if (ret.size() < 5) {
				con.data("focus", "web");
				doc = con.get();
				for (Element e : doc.select("div[class=result]")) {
					String s = e.select("a[class=title]").first().attr("href");
					if (s != null)
						ret.add(s);
				}
			}
		} catch (Exception e) {
			if (Options.getDebuglevel() > 0)
				e.printStackTrace();
		}
		return ret;
	}

}
