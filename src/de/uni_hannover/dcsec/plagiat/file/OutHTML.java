package de.uni_hannover.dcsec.plagiat.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import de.uni_hannover.dcsec.plagiat.Result;
import de.uni_hannover.dcsec.plagiat.Source;
import de.uni_hannover.dcsec.plagiat.web.ContentExtractor;

/**
 * Class to write out the results of the search into a HTML file.
 * 
 * @author pflug
 *
 */
public class OutHTML {

	/**
	 * Buffered writer to the HTML file.
	 */
	private BufferedWriter bw;

	/**
	 * Writer to the HTML file.
	 */
	private FileWriter fw;

	/**
	 * Creates a new instance to write data to.
	 * 
	 * @param filename
	 *            The name of the file to write out the data to.
	 */
	public OutHTML(String filename) {
		File f = new File(filename);
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			bw.write("<html><body><table border=\"1\">");
			bw.newLine();
		} catch (IOException e) {
			System.err.println("Could not write to HTML file.");
			bw = null;
			e.printStackTrace();
		}
	}

	/**
	 * Writes ending tags and closes the file. Further write are not possible
	 * any more. Multiple calls to close have no effect.
	 */
	public void close() {
		if (bw != null) {
			try {
				bw.write("</table></body></html>");
				bw.newLine();
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bw = null;
		}
	}

	/**
	 * @see close()
	 */
	public void finalize() {
		close();
	}

	private void writeEntry(BufferedWriter bw, String original, String source, float percentage, String url)
			throws IOException {
		if (original.length() == 0)
			return;
		original = StringEscapeUtils.escapeHtml3(original);
		source = StringEscapeUtils.escapeHtml3(source);

		bw.write("  <tr>");
		bw.newLine();
		bw.write("   <td>" + original + "</td>");
		bw.newLine();
		if (source.length() != 0) {
			bw.write("   <td>" + percentage + "</td>");
			bw.newLine();
			bw.write("   <td>" + source + "</td>");
			bw.newLine();
			bw.write("   <td><a href=\"" + url + "\">link</a></td>");
			bw.newLine();
		}
		bw.write("  </tr>");
		bw.newLine();
	}

	/**
	 * Writes the result of the search into a file. Successive matches are
	 * grouped together.
	 * 
	 * @param result
	 *            The first Result to write out.
	 */
	public void write(Result result) {
		if (bw == null) {
			return;
		}

		try {
			String original = "", source = "";
			float percentage = 0;
			int lastSourceID = -1;
			int number = 0;
			while (result != null) {
				Source s = result.getSource();
				int sourceID;
				if (s == null)
					sourceID = -1;
				else
					sourceID = s.getURL();
				if (sourceID != lastSourceID) {
					if (number == 0)
						number = 1;
					writeEntry(bw, original, source, percentage / number, ContentExtractor.getURL(lastSourceID));
					original = "";
					source = "";
					percentage = 0;
					number = 0;
					lastSourceID = sourceID;
				}
				original += result.getOriginal();
				if (s != null) {
					percentage += result.getIndicator();
					number++;
					source += s.getText();
				}
				result = result.getNext();
			}
			if (number == 0)
				number = 1;
			writeEntry(bw, original, source, percentage / number, ContentExtractor.getURL(lastSourceID));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
