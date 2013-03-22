package com.powroznik.jsplists;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class JSpListsConstants {
	public static final int COMPARISON_EQ = 0;
	public static final int COMPARISON_GE = 3;
	public static final int COMPARISON_GT = 4;
	public static final int COMPARISON_LE = 1;
	public static final int COMPARISON_LT = 2;

	public static final DateFormat DATE_FORMAT_1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	public static final String[] DEFAULT_SUPPORTED_TYPES = { "Boolean",
			"Choice", "Counter", "DateTime", "Lookup", "LookupMulti", "Note",
			"Number", "Text" };
}
