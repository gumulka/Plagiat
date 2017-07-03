/**
 * 
 */
package de.uni_hannover.dcsec.plagiat.file;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import de.uni_hannover.dcsec.plagiat.Page;

/**
 * Reads in a PDF file and returns it content as a string.
 * 
 * @author pflug
 *
 */
public class PDFReader {

	private PDDocument document;
	private Vector<Page> pages; 

	public PDFReader(File f, int startpage, int lastpage) throws IOException  {
		pages = new Vector<Page>();
		document = PDDocument.load(f);
		document.getClass();
		PDFTextStripper Tstripper = null;
		if (!document.isEncrypted()) {
			Tstripper = new PDFTextStripper();
		} else {
			return ;
		}
		for(int i = startpage;i<=lastpage;i++) {
				Tstripper.setStartPage(i);
				Tstripper.setEndPage(i);
				Page p = new Page(Tstripper.getText(document),i);
				pages.add(p);
		}
		document.close();
	}

	public Vector<Page> getPages() {
		return pages;
	}
}
