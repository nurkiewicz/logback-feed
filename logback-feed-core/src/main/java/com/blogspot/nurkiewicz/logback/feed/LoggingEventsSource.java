package com.blogspot.nurkiewicz.logback.feed;

import java.util.List;
import java.util.Calendar;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7309, 6.0.10, 2009-12-20, 18:52:27
 */
public interface LoggingEventsSource {

	List<ILoggingEvent> getLoggingEventsAfter(Calendar date);

}
