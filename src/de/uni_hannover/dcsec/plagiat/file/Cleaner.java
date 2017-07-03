package de.uni_hannover.dcsec.plagiat.file;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.Vector;

import de.uni_hannover.dcsec.plagiat.Options;
import de.uni_hannover.dcsec.plagiat.Page;
import de.uni_hannover.dcsec.plagiat.Sentence;

/**
 * Class to clean a string from unnecessary garbage and page numbers.
 * 
 * @author pflug
 *
 */
public class Cleaner {

	public static void clean(Vector<Page> pages) {
		int numPages = pages.size();
		for(int i = 0; i<numPages;i++) {
			Page p = pages.get(i);
			p.hideLinesShorterThan(20);
			p.hideLabels();
			p.hideFootnotes();
			if(i+1<numPages)
				p.hideHeaderAndFooter(pages.get(i+1));
			if(i+2<numPages)
				p.hideHeaderAndFooter(pages.get(i+2));
		}
	}

	public static Vector<Sentence> toSentences(Vector<Page> pages, Locale local) {
		Vector<Sentence> sentences = new Vector<Sentence>();
		String lastPart = "";
		BreakIterator iterator = BreakIterator.getSentenceInstance(local);
		for(Page p : pages) {
			boolean newpage=true;
			String text = lastPart + p.getCleanedText();
			iterator.setText(text);
			int start = iterator.first();
			for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
				if(newpage) {
					newpage = false;
				} else {
					sentences.add(new Sentence(lastPart, p));
				}
				lastPart = text.substring(start, end);
			}
		}
		//TODO the last sentence of the document is missing.
		return sentences;
	}

	/**
	 * Splits a text into sentences.
	 * 
	 * @param text
	 *            The text to split.
	 * @return Text splitted into sentences.
	 */
	public static Vector<String> toSentence(String text) {
		Locale l = Options.getFrom();
		if (l == null)
			return toSentence(text, Locale.ENGLISH);
		else
			return toSentence(text, l);
	}

	/**
	 * Splits a text into sentences.
	 * 
	 * @param text
	 *            The text to split.
	 * @param local
	 *            The local for the source file.
	 * @return Text splitted into sentences.
	 */
	public static Vector<String> toSentence(String text, Locale local) {
		if (text == null || text == "")
			return new Vector<String>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(local);
		iterator.setText(text);
		int start = iterator.first();
		Vector<String> ret = new Vector<String>();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			ret.add(text.substring(start, end));
		}
		return ret;
	}

	/**
	 * Cleans the text from all line breaks and tries to remove Headers.
	 * 
	 * @param text
	 *            Source text.
	 * @return Source text without line Breaks.
	 */
	public static String clean(String text) {
		if (text == null)
			return null;
		return removeLineBreaks(removeHeaders(text));
	}

	private static String removeLineBreaks(String text) {
		String[] splittet = text.split("\n");
		String ret = "";
		for (String s : splittet) {
			if (s.endsWith("-")) {
				ret += s.substring(0, s.length() - 1);
			} else
				ret += s + " ";
		}
		return ret;
	}

	private static String removeHeaders(String text) {
		String[] splittet = text.split("\n");
		String ret = "";
		for (String s : splittet) {
			try {
				String b = s;
				if (b.contains(" "))
					b = b.substring(0, b.indexOf(' '));
				if (s.contains("."))
					b = b.substring(0, b.indexOf('.'));
				Integer.parseInt(b);
			} catch (Exception e) {
				ret += s + "\n";
			}
		}
		return ret;
	}

	private static String removePageNumbers(String text) {
		String[] splittet = text.split("\n");
		String ret = "";
		for (String s : splittet) {
			try {
				Integer.parseInt(s);
			} catch (Exception e) {
				ret += s + "\n";
			}
		}
		return ret;
	}

}
