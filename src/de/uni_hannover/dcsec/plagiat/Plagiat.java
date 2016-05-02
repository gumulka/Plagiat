/**
 * 
 */
package de.uni_hannover.dcsec.plagiat;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import de.uni_hannover.dcsec.plagiat.file.Cleaner;
import de.uni_hannover.dcsec.plagiat.file.OutHTML;
import de.uni_hannover.dcsec.plagiat.file.PDFReader;
import de.uni_hannover.dcsec.plagiat.web.ContentExtractor;

/**
 * @author pflug
 *
 */
public class Plagiat {

	private static boolean run = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Long start = new Date().getTime();

		Options.parse(args);

		if (!Options.isSane())
			return;

		File f = Options.getPdfFile();

		if (f == null) {
			System.err.println("No PDF set " + Options.getPdfFile());
			Options.printHelp();
			return;
		}

		String text = PDFReader.getText(f, Options.getStartpage(), Options.getEndpage());

		if (text == null) {
			System.err.println("Could not read PDF " + Options.getPdfFile());
			return;
		}

		// Create the original text.
		text = Cleaner.clean(text);
		Vector<String> sentences = Cleaner.toSentence(text);

		Result prev = null;
		Result first = null;

		for (String c : sentences) {
			Result s = new Result(c);
			s.setPrevious(prev);
			if (prev != null)
				prev.setNext(s);
			else
				first = s;
			prev = s;
		}

		// clear some space
		sentences.clear();

		Result temp = first;

		final Result veryFirst = first;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutdown hook ran!");
				run = false;

				OutHTML out = new OutHTML(Options.getPdfFile() + ".html");
				out.write(veryFirst);
				out.close();
			}
		});

		while (temp != null && run) {
			temp.checkSelf();
			temp = temp.getNext();
		}

		ContentExtractor.printStatistics();
		System.out.println("Runtime: " + (new Date().getTime() - start) / 1000 + " seconds");

		// */
	}

}