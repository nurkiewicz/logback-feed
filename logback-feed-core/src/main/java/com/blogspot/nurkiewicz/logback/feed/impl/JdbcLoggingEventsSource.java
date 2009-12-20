package com.blogspot.nurkiewicz.logback.feed.impl;

import java.util.List;
import java.util.Calendar;

import com.blogspot.nurkiewicz.logback.feed.LoggingEventsSource;
import com.blogspot.nurkiewicz.logback.dao.LoggingEventDao;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Adapter decoupling DAO layer from feed generation.
 *
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7309, 6.0.10, 2009-12-20, 18:55:25
 */
public class JdbcLoggingEventsSource implements LoggingEventsSource {

	private final LoggingEventDao loggingEventDao;

	public JdbcLoggingEventsSource(LoggingEventDao loggingEventDao) {
		this.loggingEventDao = loggingEventDao;
	}

	public List<ILoggingEvent> getLoggingEventsAfter(Calendar date) {
		return loggingEventDao.getEventsAfter(date);
	}

}
