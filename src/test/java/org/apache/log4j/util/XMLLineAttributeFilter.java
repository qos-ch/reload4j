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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLLineAttributeFilter implements Filter {

    Pattern PATTERN_NUMBER = Pattern.compile("line=\"\\d{1,3}\"");
    Pattern PATTERN_UKNOWN = Pattern.compile("line=\"?\"");

    public String filter(String in) {
        Matcher matcherNumber = PATTERN_NUMBER.matcher(in);
        Matcher matcherUnknown = PATTERN_UKNOWN.matcher(in);

        if (matcherNumber.find()) {
            StringBuffer buf = new StringBuffer();
            matcherNumber.appendReplacement(buf, "line=\"X\"");
            matcherNumber.appendTail(buf);
            return buf.toString();
        } else if (matcherUnknown.find()) {
            StringBuffer buf = new StringBuffer();
            matcherUnknown.appendReplacement(buf, "line=\"X\"");
            matcherUnknown.appendTail(buf);
        }
        return in;
        //	if (util.match("/line=\"\\d{1,3}\"/", in)) {
        //	    return util.substitute("s/line=\"\\d{1,3}\"/line=\"X\"/", in);
        //	} else if (util.match("/line=\"?\"/", in)) {
        //	    return util.substitute("s/line=\"?\"/line=\"X\"/", in);
        //	} else {
        //	    return in;
        //	}
    }
}
