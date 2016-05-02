/**
 * 
 */
package de.uni_hannover.dcsec.plagiat.file;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Reads in a PDF file and returns it content as a string.
 * 
 * @author pflug
 *
 */
public class PDFReader {

	/**
	 * Reads in the complete file.
	 * 
	 * @param f
	 *            File link to a PDF file.
	 * @return Content string
	 */
	public static String getText(File f) {
		return getText(f, 1, 0);
	}

	/**
	 * Reads in the complete file.
	 * 
	 * @param f
	 *            File link to a PDF file.
	 * @param startpage
	 *            The first page to extract content from.
	 * @return Content string
	 */
	public static String getText(File f, int startpage) {
		return getText(f, startpage, 0);
	}

	/**
	 * Reads in the complete file.
	 * 
	 * @param f
	 *            File link to a PDF file.
	 * @param startpage
	 *            The first page to extract content from.
	 * @param lastpage
	 *            The last page to extract content from.
	 * @return Content string
	 */
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
