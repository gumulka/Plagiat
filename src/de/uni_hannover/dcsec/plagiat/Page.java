package de.uni_hannover.dcsec.plagiat;

public class Page{
	
	private String[] originalText;
	private boolean[] useful;
	private String cleanedText;
	private int number;


	public Page(String text, int pageNumber) {
		this.originalText = text.split("\n");
		this.cleanedText = null;
		this.number = pageNumber;
		this.useful = new boolean[originalText.length];
		for(int i = 0;i<originalText.length;i++) {
			useful[i] = true;
		}
	}
	
	public int getNumLines() {
		return originalText.length;
	}
	
	public String getLine(int n) {
		if(originalText.length<n)
			return originalText[n];
		return null;
	}
	
	public void markUseful(int line, boolean useful) {
		if(this.useful.length <line) {
			this.useful[line] = useful;
			cleanedText = null;
		}
	}
	
	public void hideLinesShorterThan(int lenght) {
		for(int i = 0; i<originalText.length;i++) {
			if(originalText[i].length()<lenght)
				useful[i] = false;
		}
	}
	
	public void hideLabels() {
		for(int i = 0; i<originalText.length;i++) {
			String t =originalText[i].toLowerCase(); 
			if(t.startsWith("figure"))
				useful[i] = false;
			if(t.startsWith("table"))
				useful[i] = false;
			if(t.startsWith("figure"))
				useful[i] = false;
		}
	}
	
	public void hideFootnotes() {
		boolean footnoteStarted = false;
		for(int i = 10; i<originalText.length;i++) {
			char x =originalText[i].charAt(0);
			if(x>='0' && x <='9' && originalText[i].length()>60) {
				footnoteStarted = true;
			}
			if(footnoteStarted)
				useful[i] = false;
		}
	}
	
	public void hideHeaderAndFooter(Page other) {
		int a = getNumLines();
		int b = other.getNumLines();
		if(a<3 || b<3)
			return ;
		for(int i = 0;i<3;i++) {
			if(Comparator.getSimilarity(originalText[i], other.originalText[i])>0.75) {
				useful[i] = false;
				other.useful[i] = false;
			}
			if(Comparator.getSimilarity(originalText[a-i-1], other.originalText[b-i-1])>0.8) {
				useful[a-i-1] = false;
				other.useful[b-i-1] = false;
			}
		}
		
		
	}
	
	public String getCleanedText() {
		if(cleanedText==null) {
			cleanedText = "";
			for(int i = 0; i<originalText.length;i++) {
				if(useful[i]) {
					String t =originalText[i];
					if(t.charAt(0)==t.toUpperCase().charAt(0) && !cleanedText.endsWith("\n"))
						cleanedText += "\n";
					if(t.charAt(t.length()-1)=='-')
						cleanedText += t.substring(0, t.length()-1);
					else
						cleanedText += t + " ";
				} else {
					if(!cleanedText.endsWith("\n")) {
						cleanedText+= "\n";
					}
				}
			}
		}
		return cleanedText.trim() + " ";
	}
	
	public int getPageNumber() {
		return number;
	}
	
	
	public String toString() {
		String ret = "";
		for(int i = 0; i<originalText.length;i++) {
			if(!useful[i])
				ret += "---";
			ret += originalText[i] + "\n";
		}
		return ret;
	}
}