/**
 * 
 */
package de.uni_hannover.dcsec.plagiat.file;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * @author pflug
 *
 */
public class PDFReader {

	public static String getText(File f) {
		return getText(f, 1, 0);
	}

	public static String getText(File f, int startpage) {
		return getText(f, startpage, 0);
	}

	public static String getText(File f, int startpage, int lastpage) {
		String st = null;
		try {
			PDDocument document = PDDocument.load(f);
			document.getClass();
			if (!document.isEncrypted()) {
				PDFTextStripper Tstripper = new PDFTextStripper();
				Tstripper.setStartPage(startpage);
				if (lastpage > 0)
					Tstripper.setEndPage(lastpage);
				st = Tstripper.getText(document);
			}
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return st;
	}

}
