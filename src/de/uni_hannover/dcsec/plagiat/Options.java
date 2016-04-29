package de.uni_hannover.dcsec.plagiat;

import java.io.File;
import java.util.Locale;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class Options {
	private static int startpage;
	private static int endpage;
	private static File pdfFile;
	private static Locale from;
	private static Locale to;
	private static boolean google;
	private static boolean metager;
	private static int debuglevel;

	private static boolean helpPrinted = false;

	public static void printHelp() {
		helpPrinted = true;
		System.out.println("Usage: ");
		System.out.println("java -jar plagiat.jar [Options] pdf-File");
		System.out.println("");
		System.out.println(" Options:");
		System.out.println("  -h  --help");
		System.out.println("     Print this help message");
		System.out.println("  -v  --verbose");
		System.out.println("     Increase the verbose level");
		System.out.println("  -m  --metager");
		System.out.println("     Use Metager as a search engine");
		System.out.println("  -g  --google");
		System.out.println("     Use Google as a search enging");
		System.out.println("  -b  --begin=[page]");
		System.out.println("     The first page to check [default=1]");
		System.out.println("  -e  --end=[page]");
		System.out.println("     The last page the check [default=last page]");
		System.out.println("  -s  --source=[language]");
		System.out.println("     The language of the source file");
		System.out.println("  -t  --translate=[language]");
		System.out.println("     Translate the text into [language] and check this language to.");
		System.out.println("  -f  --file=[file]");
		System.out.println("     The file to check.");
	}

	public static void parse(String[] argv) {
		startpage = 1;
		endpage = 0;
		pdfFile = null;
		from = null;
		to = null;
		google = false;
		metager = false;
		debuglevel = 0;
		int c;
		String arg;
		LongOpt[] longopts = new LongOpt[9];
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("begin", LongOpt.REQUIRED_ARGUMENT, null, 'b');
		longopts[2] = new LongOpt("end", LongOpt.REQUIRED_ARGUMENT, null, 'e');
		longopts[3] = new LongOpt("source", LongOpt.REQUIRED_ARGUMENT, null, 's');
		longopts[4] = new LongOpt("translage", LongOpt.REQUIRED_ARGUMENT, null, 't');
		longopts[5] = new LongOpt("file", LongOpt.REQUIRED_ARGUMENT, null, 'f');
		longopts[6] = new LongOpt("google", LongOpt.NO_ARGUMENT, null, 'g');
		longopts[7] = new LongOpt("metager", LongOpt.NO_ARGUMENT, null, 'm');
		longopts[8] = new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v');
		Getopt g = new Getopt("plagiat", argv, "hb:e:s:t:f:gmv", longopts);
		g.setOpterr(false); // We'll do our own error handling

		while ((c = g.getopt()) != -1)
			switch (c) {
			case 'v':
				debuglevel++;
				break;
			case 'g':
				google = true;
				break;
			case 'm':
				metager = true;
				break;
			case 'f':
				arg = g.getOptarg();
				pdfFile = new File(arg);
				if (!pdfFile.canRead()) {
					System.err.println("Could not read file: " + arg);
					pdfFile = null;
				}
				break;
			case 'b':
				arg = g.getOptarg();
				try {
					startpage = Integer.parseInt(arg);
				} catch (Exception e) {
					System.err.println(arg + " is not a number!");
				}
				break;
			case 'e':
				arg = g.getOptarg();
				try {
					endpage = Integer.parseInt(arg);
				} catch (Exception e) {
					System.err.println(arg + " is not a number!");
				}
				break;
			case 's':
				arg = g.getOptarg();
				if (arg.equalsIgnoreCase("englisch") || arg.equalsIgnoreCase("english"))
					from = Locale.ENGLISH;
				else if (arg.equalsIgnoreCase("deutsch") || arg.equalsIgnoreCase("german"))
					from = Locale.GERMAN;
				else
					System.err.println("Language " + arg + " not supported");
				break;
			case 't':
				arg = g.getOptarg();
				if (arg.equalsIgnoreCase("englisch") || arg.equalsIgnoreCase("english"))
					to = Locale.ENGLISH;
				else if (arg.equalsIgnoreCase("deutsch") || arg.equalsIgnoreCase("german"))
					to = Locale.GERMAN;
				else
					System.err.println("Language " + arg + " not supported");
				break;
			case 'h':
				printHelp();
				break;
			case '?':
				System.err.println("The option '" + (char) g.getOptopt() + "' is not valid");
				break;
			default:
				System.err.println("getopt() returned " + c);
				break;
			}

		for (int i = g.getOptind(); i < argv.length; i++) {
			pdfFile = new File(argv[i]);
			if (!pdfFile.canRead()) {
				System.err.println("Could not parse option: " + argv[i]);
				pdfFile = null;
			}
		}
	}

	public static int getStartpage() {
		return startpage;
	}

	public static int getEndpage() {
		return endpage;
	}

	public static File getPdfFile() {
		return pdfFile;
	}

	public static Locale getFrom() {
		return from;
	}

	public static Locale getTo() {
		return to;
	}

	public static boolean isSane() {
		if (!(google || metager)) {
			System.err.println("No search engine defined");
			printHelp();
			return false;
		}
		return (!helpPrinted);
	}

	public static boolean isGoogle() {
		return google;
	}

	public static boolean isMetager() {
		return metager;
	}

	public static int getDebuglevel() {
		return debuglevel;
	}

	public static boolean isHelpPrinted() {
		return helpPrinted;
	}

}
