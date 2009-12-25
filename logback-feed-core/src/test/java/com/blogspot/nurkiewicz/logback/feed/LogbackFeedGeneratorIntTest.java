package com.blogspot.nurkiewicz.logback.feed;

import java.io.IOException;
import java.util.GregorianCalendar;
import javax.sql.DataSource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.junit.matchers.StringContains.containsString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blogspot.nurkiewicz.logback.dao.DbTestSetup;
import com.blogspot.nurkiewicz.logback.dao.DefaultLoggingEventDao;
import com.blogspot.nurkiewicz.logback.feed.impl.JdbcLoggingEventsSource;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-12, 21:06:06
 */
public class LogbackFeedGeneratorIntTest {

	private static final Logger log = LoggerFactory.getLogger(LogbackFeedGeneratorIntTest.class.getName() + ".TEST");

	private LogbackFeedGenerator feedGenerator;

	@Before
	public void setUp() throws IOException {
		DataSource dataSource = new DbTestSetup().createAndSetupDatabase();
		feedGenerator = new LogbackFeedGenerator();
		feedGenerator.setLoggingEventsSource(new JdbcLoggingEventsSource(new DefaultLoggingEventDao(dataSource)));
		feedGenerator.getFeedConfig().setUri("http://nurkiewicz.blogspot.com");
	}

	@Test
	public void shouldNotReturnEntryIfNoExists() throws Exception {
		//given

		//when
		final SyndFeed feed = feedGenerator.createFeedForLogsAfter(new GregorianCalendar(1900, 0, 1));
		final String feedXml = new SyndFeedOutput().outputString(feed);

		//then
		assertThat(feedXml, not(containsString("<entry>")));
		log.info(feedXml);
	}

	@Test
	public void shouldGenerateSingleEntry() throws Exception {
		//given
		final String randomMessage = RandomStringUtils.randomAlphanumeric(30);
		//this will be available in feed
		log.info(randomMessage);

		//when
		final SyndFeed feed = feedGenerator.createFeedForLogsAfter(new GregorianCalendar(1900, 0, 1));
		final String feedXml = new SyndFeedOutput().outputString(feed);

		//then
		log.info(feedXml);
		assertEquals("There should be exactly one entry", 1, StringUtils.countMatches(feedXml, "<entry>"));
		assertThat(feedXml, containsString("<entry>"));
		assertThat(feedXml, containsString(randomMessage));
		assertThat(feedXml, containsString("term=\"INFO\""));
		assertThat(feedXml, containsString("term=\"TEST\""));
	}

}
