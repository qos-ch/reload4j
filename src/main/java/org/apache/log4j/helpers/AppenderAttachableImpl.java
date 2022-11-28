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

package org.apache.log4j.helpers;

import java.util.Collections;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A straightforward implementation of the {@link AppenderAttachable} interface.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since version 0.9.1
 */
public class AppenderAttachableImpl implements AppenderAttachable {

    final private COWArrayList<Appender> appenderList = new COWArrayList<Appender>(new Appender[0]);

    /**
     * Attach an appender. If the appender is already in the list in won't be added again.
     */
    public void addAppender(Appender newAppender) {
        // Null values for newAppender parameter are strictly forbidden.
        if (newAppender == null)
            return;

        appenderList.addIfAbsent(newAppender);
    }

    /**
     * Call the <code>doAppend</code> method on all attached appenders.
     */
    public int appendLoopOnAppenders(LoggingEvent event) {
        int size = 0;
        final Appender[] appenderArray = appenderList.asTypedArray();
        final int len = appenderArray.length;
        for (int i = 0; i < len; i++) {
            appenderArray[i].doAppend(event);
            size++;
        }
        return size;
    }

    /**
     * Get all attached appenders as an Enumeration. If there are no attached appenders <code>null</code> is returned.
     *
     * @return Enumeration An enumeration of attached appenders.
     */
    public Enumeration getAllAppenders() {
        if (appenderList == null)
            return null;
        else
            return Collections.enumeration(appenderList);
    }

    /**
     * Look for an attached appender named as <code>name</code>.
     *
     * <p>
     * Return the appender with that name if in the list. Return null otherwise.
     */
    public Appender getAppender(String name) {
        if (appenderList == null || name == null)
            return null;

        for (Appender appender : appenderList) {
            if (name.equals(appender.getName())) {
                return appender;
            }
        }
        return null;
    }

    /**
     * Returns <code>true</code> if the specified appender is in the list of attached appenders, <code>false</code>
     * otherwise.
     *
     * @since 1.2
     */
    public boolean isAttached(Appender appender) {
        if (appenderList == null || appender == null)
            return false;

        for (Appender a : appenderList) {
            if (a == appender)
                return true;
        }
        return false;
    }

    /**
     * Remove and close all previously attached appenders.
     */
    public void removeAllAppenders() {
        for (Appender a : appenderList) {
            a.close();
        }
        appenderList.clear();
    }

    /**
     * Remove the appender passed as parameter form the list of attached appenders.
     */
    public void removeAppender(Appender appender) {
        if (appender == null || appenderList == null)
            return;
        appenderList.remove(appender);
    }

    /**
     * Remove the appender with the name passed as parameter form the list of appenders.
     */
    public void removeAppender(String name) {
        if (name == null || appenderList == null)
            return;

        for (Appender a : appenderList.asTypedArray()) {
            if (name.equals((a).getName())) {
                appenderList.remove(a);
                break;
            }
        }
    }

}
