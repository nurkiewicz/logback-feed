package com.blogspot.nurkiewicz.logback.dao;

import java.util.Calendar;
import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7304, 6.0.10, 2009-12-06, 21:09:23
 */
public interface LoggingEventDao {

	List<ILoggingEvent> getEventsAfter(Calendar date);

	void clearEvents();

	void clearEventsOlderThan(Calendar date);

}
