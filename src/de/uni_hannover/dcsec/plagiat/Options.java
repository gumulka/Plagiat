package de.uni_hannover.dcsec.plagiat;

import java.io.File;
import java.util.Locale;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/**
 * Class for global options which can be send via the command line. It also
 * handles the parsing of the command line options.
 * 
 * @author pflug
 *
 */
public class Options {
	/**
	 * First page to parse from the source PDF.
	 */
	private static int startpage;
	/**
	 * Last page to parse from the source PDF. 0 for all pages.
	 */
	private static int endpage;
	/**
	 * Link to the file to be read and checked for duplicates.
	 */
	private static File pdfFile;
	/**
	 * The language of the source file.
	 */
	private static Locale from;
	/**
	 * The language in which the file should be translated and checked against.
	 */
	private static Locale to;
	/**
	 * Variable to indicate whether to use Google or not.
	 */
	private static boolean google;
	/**
	 * Variable to indicate whether to use MetaGer or not.
	 */
	private static boolean metager;
	/**
	 * Level for debug messages.
	 */
	private static int debuglevel;
	/**
	 * Variable to indicate whether to reduce memory. When set. URL's might be
	 * visited more often and downloaded and parsed every time.
	 */
	private static boolean reduceMemory;

	/**
	 * Variable which shows if the help message has been printed yet.
	 */
	private static boolean helpPrinted = false;

	/**
	 * A help message with all options is printed out to System.out
	 * 
	 */
	public static void printHelp() {
		if (helpPrinted)
			return;
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
		System.out.println("  -r  --reduceMemory");
		System.out.println("     Reduce memory usage.");
	}

	/**
	 * Resets all values to default values and parses the command line options
	 * into arguments.
	 * 
	 * @param argv
	 *            Command line Options.
	 */
	public static void parse(String[] argv) {
		startpage = 1;
		endpage = 0;
		pdfFile = null;
		from = null;
		to = null;
		google = false;
		metager = false;
		debuglevel = 0;
		reduceMemory = false;
		int c;
		String arg;
		LongOpt[] longopts = new LongOpt[10];
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("begin", LongOpt.REQUIRED_ARGUMENT, null, 'b');
		longopts[2] = new LongOpt("end", LongOpt.REQUIRED_ARGUMENT, null, 'e');
		longopts[3] = new LongOpt("source", LongOpt.REQUIRED_ARGUMENT, null, 's');
		longopts[4] = new LongOpt("translage", LongOpt.REQUIRED_ARGUMENT, null, 't');
		longopts[5] = new LongOpt("file", LongOpt.REQUIRED_ARGUMENT, null, 'f');
		longopts[6] = new LongOpt("google", LongOpt.NO_ARGUMENT, null, 'g');
		longopts[7] = new LongOpt("metager", LongOpt.NO_ARGUMENT, null, 'm');
		longopts[8] = new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v');
		longopts[9] = new LongOpt("reduceMemory", LongOpt.NO_ARGUMENT, null, 'r');
		Getopt g = new Getopt("plagiat", argv, "hb:e:s:t:f:gmvr", longopts);
		g.setOpterr(false); // We'll do our own error handling

		while ((c = g.getopt()) != -1)
			switch (c) {
			case 'r':
				reduceMemory = true;
				break;
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
				printHelp();
				pdfFile = null;
			}
		}
	}

	/**
	 * Checks if the given command line options are sane and the input file can
	 * be read.
	 * 
	 * @return true if everything is fine, false otherwise.
	 */
	public static boolean isSane() {
		if (to != null && from == null) {
			System.err.println("Translation does not work if no source language is specified.");
			printHelp();
			return false;
		}
		if (pdfFile != null) {
			if (!pdfFile.canRead()) {
				System.err.println("Could not read file: " + pdfFile);
				pdfFile = null;
				return false;
			}
		} else {
			System.err.println("No PDF defined.");
			printHelp();
			return false;
		}
		if (helpPrinted)
			return false;
		if (!(google || metager)) {
			System.err.println("No search engine defined");
			printHelp();
			return false;
		}
		return true;
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

	public static boolean isReduceMemory() {
		return reduceMemory;
	}

}
