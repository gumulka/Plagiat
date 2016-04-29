package de.uni_hannover.dcsec.plagiat.web;

import java.util.Vector;

import de.uni_hannover.dcsec.plagiat.Options;

public class WebSearch {

	public static Vector<String> search(String text) {
		Vector<String> result = new Vector<String>();
		String[] all = text.split(" ");
		String next = "";
		String[] remove = { ".", ",", "!", "?", ";", ":" };
		for (String s : all) {
			for (String r : remove)
				if (s.endsWith(r))
					s = s.substring(0, s.length() - 1);
			if (s.length() > 4)
				next += s + " ";
		}
		if (next.length() == 0)
			return result;
		if(Options.isGoogle())
			result.addAll(GoogleSearch.search(next));
		if(Options.isMetager())
			result.addAll(MetaGer.search(next));
		return result;
	}
	
}
