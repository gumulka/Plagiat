package de.uni_hannover.dcsec.plagiat;

public class Source {

	private Source prev, next;
	private String text;
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
