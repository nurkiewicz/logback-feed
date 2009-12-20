package com.blogspot.nurkiewicz.logback.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.blogspot.nurkiewicz.logback.LoggingEvent;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-06, 21:09:51
 */
public class DefaultLoggingEventDao implements LoggingEventDao {

	private static final Logger log = LoggerFactory.getLogger(DefaultLoggingEventDao.class);

	private final SimpleJdbcOperations simpleJdbcOperations;
	private final TransactionTemplate transactionTemplate;
	private final LoggingEventRowMapper loggingEventRowMapper;

	private int maxEvents = 100;

	public DefaultLoggingEventDao(DataSource dataSource) {
		simpleJdbcOperations = new SimpleJdbcTemplate(dataSource);
		loggingEventRowMapper = new LoggingEventRowMapper();
		transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
	}

	public List<ILoggingEvent> getEventsAfter(Calendar date) {
		log.trace("Reading logs created after {}, limiting to {}", date.getTime(), maxEvents);
		final List<ILoggingEvent> eventList = simpleJdbcOperations.query(getSqlForEventsAfter(), loggingEventRowMapper, maxEvents, date.getTimeInMillis());
		log.trace("Found {} events", eventList.size());
		return eventList;
	}

	public void clearEvents() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				simpleJdbcOperations.update("DELETE FROM logging_event_exception");
				simpleJdbcOperations.update("DELETE FROM logging_event_property");
				simpleJdbcOperations.update("DELETE FROM logging_event");
			}
		});
	}

	public void clearEventsOlderThan(Calendar date) {

	}

	protected String getSqlForEventsAfter() {
		return "SELECT TOP ? EVENT_ID, TIMESTMP, FORMATTED_MESSAGE, LOGGER_NAME, LEVEL_STRING, THREAD_NAME, REFERENCE_FLAG, CALLER_FILENAME, CALLER_CLASS, CALLER_METHOD, CALLER_LINE, EVENT_ID\n" +
				"FROM LOGGING_EVENT WHERE TIMESTMP > ? ORDER BY TIMESTMP DESC";
	}

	protected String getSqlForEventProperties() {
		return "SELECT MAPPED_KEY, MAPPED_VALUE FROM LOGGING_EVENT_PROPERTY WHERE EVENT_ID = ?";
	}

	protected String getSqlForEventStackTrace() {
		return "SELECT TRACE_LINE FROM LOGGING_EVENT_EXCEPTION WHERE EVENT_ID = ? ORDER BY I";
	}

	private Map<String, String> getEventProperties(long eventId) {
		final Map<String, String> mdc = new HashMap<String, String>();
		simpleJdbcOperations.getJdbcOperations().query(getSqlForEventProperties(), new Object[]{eventId}, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				mdc.put(rs.getString(1), rs.getString(2));
			}
		});
		return mdc;
	}

	public void setMaxEvents(int maxEvents) {
		this.maxEvents = maxEvents;
	}

	private class LoggingEventRowMapper implements ParameterizedRowMapper<ILoggingEvent> {

		public ILoggingEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
			final long id = rs.getLong(1);
			final long timestamp = rs.getLong(2);
			final String formattedMessage = rs.getString(3);
			final String loggerName = rs.getString(4);
			final String levelString = rs.getString(5);
			final String threadName = rs.getString(6);
			final short referenceFlag = rs.getShort(7);
			final String callerFilename = rs.getString(8);
			final String callerClass = rs.getString(9);
			final String callerMethod = rs.getString(10);
			final int callerLine = rs.getInt(11);
			final long eventId = rs.getLong(12);
			final Map<String, String> mdc = getEventProperties(eventId);
			return new LoggingEvent(id, timestamp, formattedMessage, loggerName, levelString, threadName, referenceFlag, callerFilename, callerClass, callerMethod, callerLine, mdc);
		}

	}


}
