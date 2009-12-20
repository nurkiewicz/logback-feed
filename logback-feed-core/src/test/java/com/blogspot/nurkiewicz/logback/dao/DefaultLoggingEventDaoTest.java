package com.blogspot.nurkiewicz.logback.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.sql.DataSource;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-12, 11:59:06
 */
public class DefaultLoggingEventDaoTest {

	private static final Logger log = LoggerFactory.getLogger("com.blogspot.nurkiewicz.logback.dao.DefaultLoggingEventDaoTest.TEST");

	private DefaultLoggingEventDao dao;
	private SimpleJdbcOperations jdbcTemplate;

	@Before
	public void setUp() throws IOException {
		DataSource dataSource = new DbTestSetup().createAndSetupDatabase();
		dao = new DefaultLoggingEventDao(dataSource);
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	@After
	public void clearMdc() {
		MDC.clear();
	}

	@Test
	public void clearEventsWhenNoEvents() throws Exception {
		//given
		assertDatabaseEmpty();

		//when
		dao.clearEvents();

		//then
		assertDatabaseEmpty();
	}

	private void assertDatabaseEmpty() {
		assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM logging_event"));
		assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM logging_event_property"));
		assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM logging_event_exception"));
	}

	@Test
	public void clearEventsWhenEventExists() throws Exception {
		//given
		log.debug("Abc");
		assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM logging_event"));
		assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM logging_event_property"));
		assertEquals(0, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM logging_event_exception"));

		//when
		dao.clearEvents();

		//then
		assertDatabaseEmpty();
	}

	@Test
	public void simpleEventShouldBeAdded() throws Exception {
		//given
		dao.clearEvents();
		log.info("Ala ma kota");

		//when
		final List<ILoggingEvent> events = dao.getEventsAfter(new GregorianCalendar(1900, 0, 0));

		//then
		assertEquals(1, events.size());
		final ILoggingEvent event = events.get(0);
		assertNull(event.getArgumentArray());
		assertEquals("Ala ma kota", event.getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
		assertEquals("com.blogspot.nurkiewicz.logback.dao.DefaultLoggingEventDaoTest.TEST", event.getLoggerName());
		assertEquals(0, event.getMDCPropertyMap().size());
		assertEquals(Thread.currentThread().getName(), event.getThreadName());
		assertNull(event.getThrowableProxy());
	}

	@Test
	public void shouldLoadMdcIfAvailable() throws Exception {
		//given
		MDC.put("user", "Tomek");
		log.error("Opps!");

		//when
		final List<ILoggingEvent> events = dao.getEventsAfter(new GregorianCalendar(1900, 0, 0));

		//then
		assertEquals(1, events.size());
		final ILoggingEvent event = events.get(0);
		assertEquals("Opps!", event.getMessage());
		assertEquals(1, event.getMDCPropertyMap().size());
		assertEquals("Tomek", event.getMDCPropertyMap().get("user"));
	}

	@Test
	public void shouldLoadMdcIfManyProperties() throws Exception {
		//given
		MDC.put("user", "Tomek");
		MDC.put("session", "765654543");
		log.warn("Error?");

		//when
		final List<ILoggingEvent> events = dao.getEventsAfter(new GregorianCalendar(1900, 0, 0));

		//then
		assertEquals(1, events.size());
		final ILoggingEvent event = events.get(0);
		assertEquals("Error?", event.getMessage());
		assertEquals(2, event.getMDCPropertyMap().size());
		assertEquals("Tomek", event.getMDCPropertyMap().get("user"));
		assertEquals("765654543", event.getMDCPropertyMap().get("session"));
	}

	@Test
	public void shouldLoadCallerData() throws Exception {
		//given
		//the following line number is tested few lines later
		log.debug("Debug info...");

		//when
		final List<ILoggingEvent> events = dao.getEventsAfter(new GregorianCalendar(1900, 0, 0));

		//then
		assertEquals(1, events.size());
		final ILoggingEvent event = events.get(0);
		assertEquals("Debug info...", event.getMessage());
		final StackTraceElement callerData = event.getCallerData()[0];
		assertEquals(DefaultLoggingEventDaoTest.class.getName(), callerData.getClassName());
		assertEquals(DefaultLoggingEventDaoTest.class.getSimpleName() + ".java", callerData.getFileName());
		//very verbose test, watch out!
		assertEquals("Wrong line number, see test implementation for details", 144, callerData.getLineNumber());
		assertEquals("shouldLoadCallerData", callerData.getMethodName());
	}

	@Test
	@Ignore("Will pass after LBCLASSIC-170 will be solved")
	public void shouldLoadExceptionData() throws Exception {
		//given
		try {
			throw new FileNotFoundException("temp.dat");
		} catch (FileNotFoundException e) {
			log.error("Can't find file", e);
		}

		//when
		final List<ILoggingEvent> events = dao.getEventsAfter(new GregorianCalendar(1900, 0, 0));

		//then
		assertEquals(1, events.size());
		final ILoggingEvent event = events.get(0);
		assertEquals("Can't find file", event.getMessage());
		final IThrowableProxy throwableProxy = event.getThrowableProxy();
		assertNotNull(throwableProxy);
		assertEquals("temp.dat", throwableProxy.getMessage());
		assertEquals(FileNotFoundException.class.getName(), throwableProxy.getClassName());
	}

	@Test
	public void shouldLoadAllEvents() throws Exception {
		final int COUNT = 5;
		//given
		MDC.put("user", "admin");
		MDC.put("session", "D5F6G7");
		for(int i = 0; i < COUNT; ++i) {
			log.info("Info message: {}", i);
			//slow things down a little bit to have growing, unique timestamps
			Thread.sleep(100);
		}

		//when
		final List<ILoggingEvent> events = dao.getEventsAfter(new GregorianCalendar(1900, 0, 0));

		//then
		assertEquals(COUNT, events.size());

		int idx = COUNT - 1;
		for (ILoggingEvent event : events) {
			assertEquals("Info message: " + idx, event.getMessage());
			assertEquals(2, event.getMDCPropertyMap().size());
			--idx;
		}

	}

	@Test
	public void shouldLoadOnlyEventsUpToDate() throws Exception {
		//given
		log.trace("Old message");
		Thread.sleep(100);
		final long dateInMillis = System.currentTimeMillis();
		Thread.sleep(100);
		log.trace("New message");

		//when
		final Calendar date = Calendar.getInstance();
		date.setTimeInMillis(dateInMillis);

		final List<ILoggingEvent> events = dao.getEventsAfter(date);

		//then
		assertEquals(1, events.size());
		final ILoggingEvent event = events.get(0);
		assertEquals("New message", event.getMessage());
	}

	@Test
	public void shouldLoadLimitedNumberOfEvents() throws Exception {
		//given
		for(int i = 0; i < 20; ++i) {
			log.debug("Detailed message: {}", i);
		}

		//when
		dao.setMaxEvents(10);
		final List<ILoggingEvent> events = dao.getEventsAfter(new GregorianCalendar(1900, 0, 0));

		//then
		assertEquals(10, events.size());
	}

}
