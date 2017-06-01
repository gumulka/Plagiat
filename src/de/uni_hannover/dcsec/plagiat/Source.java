package de.uni_hannover.dcsec.plagiat;

/**
 * Class for a potential source file where the plagiarism might be from.
 * 
 * @author pflug
 *
 */
public class Source {

	/**
	 * Previous and next sentence in the source.s
	 */
	private Source prev, next;
	/**
	 * The sentence of the source.
	 */
	private String text;
	/**
	 * ID of the URL where this sentence is from.
	 * 
	 * @see ContentExtractor.getURL(int id)
	 */
	private int urlID;

	public Source(String text, int url) {
		this.text = text;
		this.urlID = url;
	}

	public void setPrevious(Source s) {
		this.prev = s;
	}

	public void setNext(Source s) {
		this.next = s;
	}

	public String getText() {
		return text;
	}

	public int getURL() {
		return urlID;
	}

	public Source getPrevious() {
		return prev;
	}

	public Source getNext() {
		return next;
	}

}
