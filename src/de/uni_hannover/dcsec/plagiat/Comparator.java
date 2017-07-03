package de.uni_hannover.dcsec.plagiat;

import org.simmetrics.StringMetric;
import org.simmetrics.StringMetrics;

public class Comparator {

	private static StringMetric metric = StringMetrics.cosineSimilarity();
	
	public static float getSimilarity(String a, String b) {
		if(a.equalsIgnoreCase(b))
			return 1;
		return metric.compare(a, b);
	}
}
