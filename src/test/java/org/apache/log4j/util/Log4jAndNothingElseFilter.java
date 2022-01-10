package org.apache.log4j.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Log4jAndNothingElseFilter implements Filter {

	public boolean containsMatch(String regex, String in) {
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(in);
		if (matcher.lookingAt()) {
			return true;
		}

		return false;
	}

	@Override
	public String filter(String in) throws UnexpectedFormatException {
		if (in == null) {
			return null;
		}

		if (!containsMatch("\\sat\\s", in)) {
			return in;
		}
		if (containsMatch("\\sat\\sorg.apache.log4j", in)) {
			return in;
		} else {
			return null;
		}

	}

}
