/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.jdbc;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JdbcPatternParser {
    private static final String QUESTION_MARK = "?";

    private static final char PERCENT_CHAR = '%';

    // private final static Pattern STRING_LITERAL_PATTERN =
    // Pattern.compile("'((?>[^']|'')+)'");
    private final static Pattern STRING_LITERAL_PATTERN = Pattern.compile("'(([^']|'')+)'");

    private String parameterizedSql;
    private List<String> patternStringRepresentationList = new ArrayList<String>();
    private List<PatternConverter> args = new ArrayList<PatternConverter>();

    JdbcPatternParser(String insertString) {
	init(insertString);
    }

    public String getParameterizedSql() {
	return parameterizedSql;
    }

    public List<String> getCopyOfpatternStringRepresentationList() {
	return new ArrayList<String>(patternStringRepresentationList);
    }

    @Override
    public String toString() {
	return "JdbcPatternParser{sql=" + parameterizedSql + ",args=" + patternStringRepresentationList + "}";
    }

    /**
     * Converts '....' literals into bind variables in JDBC.
     */
    private void init(String insertString) {
	if (insertString == null) {
	    throw new IllegalArgumentException("Null pattern");
	}

	Matcher m = STRING_LITERAL_PATTERN.matcher(insertString);
	StringBuffer sb = new StringBuffer();
	while (m.find()) {
	    String matchedStr = m.group(1);
	    if (matchedStr.indexOf(PERCENT_CHAR) == -1) {
		replaceWithMatchedStr(m, sb);
	    } else {
		// Replace with bind
		replaceWithBind(m, sb, matchedStr);
	    }
	}
	m.appendTail(sb);
	this.parameterizedSql = sb.toString();
    }

    private void replaceWithMatchedStr(Matcher m, StringBuffer sb) {
	// Just literal, append it as is
	m.appendReplacement(sb, "'$1'");
    }

    private void replaceWithBind(Matcher m, StringBuffer sb, String matchedStr) {
	m.appendReplacement(sb, QUESTION_MARK);
	// We will use prepared statements, so we don't need to escape quotes.
	// And we assume the users had 'That''s a string with quotes' in their configs.
	matchedStr = matchedStr.replaceAll("''", "'");
	patternStringRepresentationList.add(matchedStr);
	args.add(new PatternParser(matchedStr).parse());
    }

    public void setParameters(PreparedStatement ps, LoggingEvent logEvent) throws SQLException {
	for (int i = 0; i < args.size(); i++) {
	    final PatternConverter head = args.get(i);
	    String value = buildValueStr(logEvent, head);
	    ps.setString(i + 1, value);
	}
    }

    private String buildValueStr(LoggingEvent logEvent, final PatternConverter head) {
	StringBuffer buffer = new StringBuffer();
	PatternConverter c = head;
	while (c != null) {
	    c.format(buffer, logEvent);
	    c = c.next;
	}
	return buffer.toString();
    }
}
