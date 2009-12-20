package com.blogspot.nurkiewicz.logback.feed;

import java.util.Calendar;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.blogspot.nurkiewicz.logback.LoggingEvent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7304, 6.0.10, 2009-12-12, 21:06:06
 */
public class LogbackFeedGeneratorTest {

	private LoggingEventsSource loggingEventsSourceMock;
	private LogbackFeedGenerator feedGenerator;

	@Before
	public void setup() {
		loggingEventsSourceMock = mock(LoggingEventsSource.class);
		feedGenerator = new LogbackFeedGenerator();
		feedGenerator.setLoggingEventsSource(loggingEventsSourceMock);
		feedGenerator.setUri("http://nurkiewicz.blogspot.com");
	}

	@Test
	public void shouldReturnEmptyFeedWhenNoEvents() throws Exception {
		//given
		when(loggingEventsSourceMock.getLoggingEventsAfter((Calendar) notNull())).thenReturn(Collections.<ILoggingEvent>emptyList());

		//when
		final SyndFeed feed = feedGenerator.createFeedForLogsAfter(Calendar.getInstance());

		//then
		assertNotNull(feed);
		assertEquals(0, feed.getEntries().size());
	}

	@Test
	public void shouldCreateSingleFeedEntry() throws Exception {
		//given
		LoggingEvent event = new LoggingEvent(1, Calendar.getInstance().getTimeInMillis(), "Saving", "com.example.CustomerDao", "INFO", "PoolThread-17", (short) 0, "CustomerDao.java", "com.example.CustomerDao", "saveCustomer", 2356, null);
		when(loggingEventsSourceMock.getLoggingEventsAfter((Calendar) notNull())).thenReturn(Collections.<ILoggingEvent>singletonList(event));

		//when
		final SyndFeed feed = feedGenerator.createFeedForLogsAfter(Calendar.getInstance());

		//then
		assertEquals(1, feed.getEntries().size());
		SyndEntry entry = (SyndEntry) feed.getEntries().get(0);
		assertEquals("INFO: [CustomerDao] Saving", entry.getTitle());
		assertEquals("http://nurkiewicz.blogspot.com/logback/1", entry.getUri());
	}

}
