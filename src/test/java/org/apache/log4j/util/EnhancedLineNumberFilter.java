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

package org.apache.log4j.util;

import java.util.regex.Pattern;

public class EnhancedLineNumberFilter implements Filter {
    static Pattern LINE_PATTERN;
    static Pattern NATIVE_PATTERN;

    public EnhancedLineNumberFilter() {
	LINE_PATTERN = Pattern.compile("\\(.*:\\d{1,4}\\)");
	NATIVE_PATTERN = Pattern.compile("\\(Native Method\\)");
    }

    public String filter(final String in) {

	if (LINE_PATTERN.matcher(in).find()) {
	    return LINE_PATTERN.matcher(in).replaceAll("(X)");
	} else if (NATIVE_PATTERN.matcher(in).find()) {
	    return NATIVE_PATTERN.matcher(in).replaceAll("(X)");
	} else {
	    return in;
	}
    }
}
