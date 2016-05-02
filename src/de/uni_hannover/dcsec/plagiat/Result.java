package de.uni_hannover.dcsec.plagiat;

import java.util.Vector;

import org.simmetrics.StringMetric;
import org.simmetrics.StringMetrics;

import de.uni_hannover.dcsec.plagiat.web.ContentExtractor;
import de.uni_hannover.dcsec.plagiat.web.Translate;
import de.uni_hannover.dcsec.plagiat.web.WebSearch;

public class Result {

	private Result previous, next;
	private String original, translate;
	private Vector<Source> possibilities;
	private Source source;
	private float indicator;
	private static StringMetric metric = StringMetrics.cosineSimilarity();

	private static final int MAXFAILMATCH = 10;
	private static final int MAXFAILSENTENCE = 20;

	public Result(String s) {
		original = s;
		possibilities = new Vector<Source>();
		indicator = (float) 0.5;
		source = null;
		previous = null;
		next = null;
		translate = null;
	}

	public Source getSource() {
		return source;
	}

	public float checkAgainst(Source s, boolean trans) {
		return checkAgainst(s, Integer.MAX_VALUE, trans);
	}

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
		int i = 0;
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
			// TODO delete this break!
			if (++i > 5)
				break;
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

}
