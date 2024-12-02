package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;

/**
 * Vector appender that can be explicitly blocked.
 */
final class BlockableVectorAppender extends VectorAppender {
    /**
     * Monitor object used to block appender.
     */
    private final Object monitor = new Object();

    /**
     * Create new instance.
     */
    public BlockableVectorAppender() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void append(final LoggingEvent event) {
        synchronized (monitor) {
            super.append(event);
            //
            // if fatal, echo messages for testLoggingInDispatcher
            //
            if (event.getLevel() == Level.FATAL) {
                Logger logger = Logger.getLogger(event.getLoggerName());
                logger.error(event.getMessage().toString());
                logger.warn(event.getMessage().toString());
                logger.info(event.getMessage().toString());
                logger.debug(event.getMessage().toString());
            }
        }
    }

    /**
     * Get monitor object.
     *
     * @return monitor.
     */
    public Object getMonitor() {
        return monitor;
    }

}