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
    private final static Pattern STRING_LITERAL_PATTERN = Pattern.compile("'((?>[^']|'')+)'");

    private String parameterizedSql;
    private StringBuffer buffer = new StringBuffer();
    private List<String> patternStringRepresentationList = new ArrayList<String>();
    private List<PatternConverter> args = new ArrayList<PatternConverter>();

    JdbcPatternParser(String pattern) {
	init(pattern);
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
    private void init(String pattern) {
	if (pattern == null) {
	    throw new IllegalArgumentException("Null pattern");
	}
	    
	Matcher m = STRING_LITERAL_PATTERN.matcher(pattern);
	StringBuffer sb = new StringBuffer();
	while (m.find()) {
	    String literal = m.group(1);
	    if (literal.indexOf('%') == -1) {
		// Just literal, append it as is
		// It can't contain user-provided parts like %m, etc.
		m.appendReplacement(sb, "'$1'");
		continue;
	    }

	    // Replace with bind
	    m.appendReplacement(sb, "?");
	    // We will use prepared statements, so we don't need to escape quotes.
	    // And we assume the users had 'That''s a string with quotes' in their configs.
	    literal = literal.replaceAll("''", "'");
	    patternStringRepresentationList.add(literal);
	    args.add(new PatternParser(literal).parse());
	}
	m.appendTail(sb);
	this.parameterizedSql = sb.toString();
    }

    public void setParameters(PreparedStatement ps, LoggingEvent logEvent) throws SQLException {
	for (int i = 0; i < args.size(); i++) {
	    buffer.setLength(0);
	    PatternConverter c = args.get(i);
	    while (c != null) {
		c.format(buffer, logEvent);
		c = c.next;
	    }
	    ps.setString(i + 1, buffer.toString());
	}
	// This clears "toString cache"
	buffer.setLength(0);
	if (buffer.capacity() > 100000) {
	    // Avoid leaking too much memory if we discover long parameter
	    buffer = new StringBuffer();
	}
    }
}
