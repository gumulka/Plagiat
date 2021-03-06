package de.uni_hannover.dcsec.plagiat;

import java.util.Vector;

import org.simmetrics.StringMetric;
import org.simmetrics.StringMetrics;

import de.uni_hannover.dcsec.plagiat.web.ContentExtractor;
import de.uni_hannover.dcsec.plagiat.web.Translate;
import de.uni_hannover.dcsec.plagiat.web.WebSearch;

/**
 * A class for a single sentence of the source file and the corresponding
 * sentences.
 * 
 * @author pflug
 *
 */
public class Result {

	/**
	 * Previous and next sentence.
	 */
	private Result previous, next;
	/**
	 * The sentence
	 */
	private String original;
	/**
	 * Translation of the original sentence.
	 */
	private String translate;
	/**
	 * Possible sources for the original.
	 */
	private Vector<Source> possibilities;
	/**
	 * The source with the best match.
	 */
	private Source source;
	/**
	 * Indicator how high the match is. Higher is better.
	 */
	private float indicator;
	/**
	 * Metric for comparing the sentences.
	 */
	private static StringMetric metric = StringMetrics.cosineSimilarity();

	/**
	 * Maximum number of neighboring sentences in the source to check if one is
	 * a match.
	 */
	private static final int MAXFAILMATCH = 10;

	/**
	 * Maximum number of neighboring sentences in the target to check if one is
	 * a match.
	 */
	private static final int MAXFAILSENTENCE = 20;

	/**
	 * Initialises all parameters.
	 * 
	 * @param s
	 *            the source sentence
	 */
	public Result(String s) {
		original = s;
		possibilities = new Vector<Source>();
		indicator = (float) 0.5;
		source = null;
		previous = null;
		next = null;
		translate = null;
	}

	/**
	 * checks the sentence against a given Source.
	 * 
	 * @param s
	 *            the Source to check against.
	 * @param trans
	 *            If the translation of the original has to be checked against.
	 * @return indicator on how high the similarity is.
	 */
	public float checkAgainst(Source s, boolean trans) {
		return checkAgainst(s, Integer.MAX_VALUE, trans);
	}

	/**
	 * checks the sentence against a given Source.
	 * 
	 * @param s
	 *            the Source to check against.
	 * @param counter
	 *            The number of neighboring sentences in the source to check.
	 * @param trans
	 *            If the translation of the original has to be checked against.
	 * @return indicator on how high the similarity is.
	 */
	public float checkAgainst(Source s, int counter, boolean trans) {
		String checking;
		if (trans) {
			if (translate == null)
				translate = Translate.getTranslation(original);
			if (translate != null)
				checking = translate;
			else
				return 0;
		} else
			checking = original;
		Source temp = s;
		for (int i = 0; i < counter; i++) {
			if (temp == null)
				break;
			String so = temp.getText();
			if (so.equalsIgnoreCase(checking)) {
				indicator = 1;
				source = temp;
				return indicator;
			}
			float result = metric.compare(checking, so);
			if (result > indicator) {
				source = temp;
				indicator = result;
			}
			temp = temp.getNext();
		}
		temp = s;
		for (int i = 0; i < counter; i++) {
			if (temp == null)
				break;
			String so = temp.getText();
			if (so.equalsIgnoreCase(checking)) {
				indicator = 1;
				source = temp;
				return indicator;
			}
			float result = metric.compare(checking, so);
			if (result > indicator) {
				source = temp;
				indicator = result;
			}
			temp = temp.getPrevious();
		}
		return indicator;
	}

	/**
	 * Check itself against online sources.
	 * 
	 * @return the indicator on how high the similarity to the best match is.
	 */
	public float checkSelf() {
		if (original.length() < 30)
			return 0;
		float origin = checkSelf(false);
		if (Options.getFrom() != null && Options.getTo() != null) {
			float x = checkSelf(true);
			if (x > origin)
				origin = x;
		}
		return origin;
	}

	/**
	 * Check itself against online sources.
	 * 
	 * @param trans
	 *            If to also translate the sentence and check the translation
	 *            to.
	 * @return the indicator on how high the similarity to the best match is.
	 */
	public float checkSelf(boolean trans) {
		String check;
		if (trans) {
			if (translate == null) {
				translate = Translate.getTranslation(original);
			}
			if (translate == null)
				return 0;
			check = translate;
		} else
			check = original;
		if (indicator > 0.6)
			return indicator;
		System.out.println("Doing self check for: " + check);
		for (String res : WebSearch.search(check)) {
			Source cont = ContentExtractor.getContent(res);
			if (cont != null) {
				possibilities.add(cont);
				checkAgainst(cont, trans);
				if (indicator > 0.9)
					break;
			}
		}
		if (indicator > 0.5) {
			if (previous != null) {
				previous.checkPrev(source, MAXFAILMATCH, trans);
			}
			if (next != null)
				next.checkNext(source, MAXFAILMATCH, trans);
		}
		return indicator;
	}

	private void checkPrev(Source s, int n, boolean trans) {
		if (source != null && source.getURL() == s.getURL())
			return;
		System.out.println("Checking previous Sentence");
		float r = checkAgainst(s, MAXFAILSENTENCE, trans);
		if (previous != null && n > 0) {
			if (r > 0.5) {
				previous.checkPrev(source, MAXFAILMATCH, trans);
			} else {
				previous.checkPrev(s, --n, trans);
			}
		}
	}

	private void checkNext(Source s, int n, boolean trans) {
		if (source != null && source.getURL() == s.getURL())
			return;
		System.out.println("Checking next Sentence");
		float r = checkAgainst(s, MAXFAILSENTENCE, trans);
		if (next != null && n > 0) {
			if (r > 0.5) {
				next.checkNext(source, MAXFAILMATCH, trans);
			} else {
				next.checkNext(s, --n, trans);
			}
		}
	}

	public float getIndicator() {
		return indicator;
	}

	public String getOriginal() {
		return original;
	}

	public Result getPrevious() {
		return previous;
	}

	public void setPrevious(Result previous) {
		this.previous = previous;
	}

	public Result getNext() {
		return next;
	}

	public void setNext(Result next) {
		this.next = next;
	}

	public Source getSource() {
		return source;
	}

}
