package de.uni_hannover.dcsec.plagiat;

import de.uni_hannover.dcsec.plagiat.web.Translate;

public class Sentence {
	
	private String original;
	private String translated;
	
	private Page p;
	
	
	public Sentence(String text, Page page) {
		this.p = page;
		this.original = text;
		this.translated = null;
	}
	
	public String getSentence() {
		return original;
	}
	
	public Page getPage() {
		return p;
	}
	
	public String getTranslation() {
		if(translated == null) {
			translated = Translate.getTranslation(original);
		}
		return translated;
	}

	
	public String toString() {
		return original;
	}	
}
