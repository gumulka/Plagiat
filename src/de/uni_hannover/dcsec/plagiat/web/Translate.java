package de.uni_hannover.dcsec.plagiat.web;

import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.uni_hannover.dcsec.plagiat.Options;

/**
 * Class for translating sentences using web services.
 * 
 * @author pflug
 *
 */
public class Translate {

	/**
	 * Translates a sentence into another language define by the options class.
	 * 
	 * @param original
	 *            The original sentence
	 * @return The translation.
	 */
	public static String getTranslation(String original) {
		if (Options.getFrom() == Options.getTo())
			return null;
		if (Options.getFrom() == Locale.GERMAN)
			return getTranslation(original, "german");
		else if (Options.getFrom() == Locale.ENGLISH)
			return getTranslation(original, "english");
		else
			return null;
	}

	private static String getTranslation(String original, String originLanguage) {
		if (Options.getTo() == Locale.ENGLISH)
			return getTranslation(original, originLanguage, "english");
		else if (Options.getTo() == Locale.GERMAN)
			return getTranslation(original, originLanguage, "german");
		else
			return null;
	}

	/**
	 * Translates a sentence into another language define by the options class.
	 * 
	 * @param original
	 *            The original sentence
	 * @param originLanguage
	 *            The language of the source sentence
	 * @param targetLanguage
	 *            The language to translate into.
	 * @return The translation.
	 */
	public static String getTranslation(String original, String originLanguage, String targetLanguage) {
		if (Options.getDebuglevel() > 1)
			System.out.println("Translating: " + original);
		try {
			Document doc = Jsoup
					.connect("http://translate.reference.com/" + originLanguage + "/" + targetLanguage + "/" + original)
					.userAgent("PlagTest").get();
			Element e = doc.select("textarea[Placeholder=Translation]").first();
			String text = e.text().trim();
			if (text.equalsIgnoreCase(original.trim()))
				return null;
			return text;
		} catch (Exception e1) {
		}
		return null;
	}

}
