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

package org.apache.log4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Utility class used in testing to capture errors dispatched by appenders.
 *
 * @author Curt Arnold
 */
public final class ListErrorHandler implements ErrorHandler {

    static class ErrorTuple {
        final String message;
        final Exception e;
        final int errorCode;
        final LoggingEvent event;

        public ErrorTuple(String message, Exception e, int errorCode, LoggingEvent event) {
            super();
            this.message = message;
            this.e = e;
            this.errorCode = errorCode;
            this.event = event;
        }
    }

    /**
     * Logger.
     */
    private Logger logger;

    /**
     * Appender.
     */
    private Appender appender;

    /**
     * Backup appender.
     */
    private Appender backupAppender;

    /**
     * Array of processed errors.
     */
    private final List<ErrorTuple> errors = new ArrayList<ErrorTuple>();

    /**
     * Default constructor.
     */
    public ListErrorHandler() {
    }

    /**
     * {@inheritDoc}
     */
    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Gets last logger specified by setLogger.
     *
     * @return logger.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * {@inheritDoc}
     */
    public void activateOptions() {
    }

    /**
     * {@inheritDoc}
     */
    public void error(final String message, final Exception e, final int errorCode) {
        error(message, e, errorCode, null);
    }

    /**
     * {@inheritDoc}
     */
    public void error(final String message) {
        error(message, null, -1, null);
    }

    /**
     * {@inheritDoc}
     */
    public void error(final String message, final Exception e, final int errorCode, final LoggingEvent event) {
        errors.add(new ErrorTuple(message, e, errorCode, event));
    }

    /**
     * Gets message from specified error.
     *
     * @param index index.
     * @return message, may be null.
     */
    public String getMessage(final int index) {
        return errors.get(index).message;
    }

    /**
     * Gets exception from specified error.
     *
     * @param index index.
     * @return exception.
     */
    public Exception getException(final int index) {
        return errors.get(index).e;
    }

    public ErrorTuple getErrorTuple(final int index) {
        return errors.get(index);
    }

    /**
     * Gets logging event from specified error.
     *
     * @param index index.
     * @return exception.
     */
    public LoggingEvent getEvent(final int index) {
        return errors.get(index).event;
    }

    /**
     * Gets number of errors captured.
     *
     * @return number of errors captured.
     */
    public int size() {
        return errors.size();
    }

    /**
     * {@inheritDoc}
     */
    public void setAppender(final Appender appender) {
        this.appender = appender;
    }

    /**
     * Get appender.
     *
     * @return appender, may be null.
     */
    public Appender getAppender() {
        return appender;
    }

    /**
     * {@inheritDoc}
     */
    public void setBackupAppender(final Appender appender) {
        this.backupAppender = appender;
    }

    /**
     * Get backup appender.
     *
     * @return backup appender, may be null.
     */
    public Appender getBackupAppender() {
        return backupAppender;
    }
}
