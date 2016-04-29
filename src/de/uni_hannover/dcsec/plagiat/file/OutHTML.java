package de.uni_hannover.dcsec.plagiat.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import de.uni_hannover.dcsec.plagiat.Result;
import de.uni_hannover.dcsec.plagiat.Source;
import de.uni_hannover.dcsec.plagiat.web.ContentExtractor;

public class OutHTML {

	private File f;

	public OutHTML(String filename) {
		f = new File(filename);
	}

	private void writeHeader(BufferedWriter bw) throws IOException {
		bw.write("<html><body><table border=\"1\">");
		bw.newLine();

	}

	private void writeFooter(BufferedWriter bw) throws IOException {
		bw.write("</table></body></html>");
		bw.newLine();

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

	public void write(Result result) {
		try {
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			writeHeader(bw);
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
			writeFooter(bw);
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
