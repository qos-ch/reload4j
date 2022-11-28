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
package org.apache.log4j.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

// === Copied from the logback project with permission ==
public class HardenedLoggingEventInputStream extends HardenedObjectInputStream {

    static final String ARRAY_PREFIX = "[L";

    static public List<String> getWhilelist() {

        List<String> whitelist = new ArrayList<String>();
        whitelist.add(LoggingEvent.class.getName());
        whitelist.add(Level.class.getName());
        whitelist.add(Priority.class.getName());
        whitelist.add(ThrowableInformation.class.getName());
        whitelist.add(LocationInfo.class.getName());

        return whitelist;
    }

    public HardenedLoggingEventInputStream(InputStream is) throws IOException {
        super(is, getWhilelist());
    }

    public HardenedLoggingEventInputStream(InputStream is, List<String> additionalAuthorizedClasses)
            throws IOException {
        this(is);
        super.addToWhitelist(additionalAuthorizedClasses);
    }
}
