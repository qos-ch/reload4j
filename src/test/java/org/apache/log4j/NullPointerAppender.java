package org.apache.log4j;

class NullPointerAppender extends AppenderSkeleton {
    public NullPointerAppender() {
    }

    /**
     * This method is called by the {@link org.apache.log4j.AppenderSkeleton#doAppend} method.
     */
    public void append(org.apache.log4j.spi.LoggingEvent event) {
        throw new NullPointerException();
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }
}