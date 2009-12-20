package com.blogspot.nurkiewicz.logback.feed;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.blogspot.nurkiewicz.logback.LoggingEvent;
import com.blogspot.nurkiewicz.logback.feed.config.FeedConfig;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7304, 6.0.10, 2009-12-12, 21:15:21
 */
public class LogbackFeedGenerator {

	private static final Logger log = LoggerFactory.getLogger(LogbackFeedGenerator.class);

	private LoggingEventsSource loggingEventsSource;
	private FeedConfig feedConfig = new FeedConfig();

	private LoggerContext loggerContext;

	private PatternLayout titleLayout;
	private PatternLayout contentLayout;
	private String feedTitle;
	private String feedDescription = "Feed generated using Logback logging framework and logback-feed library. See http://nurkiewicz.blogspot.com";
	private String author;
	private String uri;
	private String feedType = "atom_1.0";

	public LogbackFeedGenerator() {
		try {
			final String titlePattern = IOUtils.toString(LogbackFeedGenerator.class.getResourceAsStream("title.pattern"));
			final String contentPattern = IOUtils.toString(LogbackFeedGenerator.class.getResourceAsStream("content.pattern.html"));
			init(titlePattern, contentPattern);
		} catch (IOException e) {
			throw new LogbackFeedException(e);
		}
	}

	public LogbackFeedGenerator(String titlePattern, String contentPattern) {
		init(titlePattern, contentPattern);
	}

	private void init(String titlePattern, String contentPattern) {
		loggerContext = ((ch.qos.logback.classic.Logger) log).getLoggerContext();
		setTitlePattern(titlePattern);
		setContentPattern(contentPattern);
	}

	public SyndFeed createFeedForLogsAfter(Calendar date) {
		Validate.notEmpty(uri, "Feed URI must not be empty");
		final List<ILoggingEvent> events = loggingEventsSource.getLoggingEventsAfter(date);
		return createFeedForEvents(events);
	}

	public void createAndOutputFeed(Calendar date, Writer outputWriter) {
		try {
			log.debug("Creating feed with logs after: {}", date.getTime());
			final SyndFeed feed = createFeedForLogsAfter(date);
			new SyndFeedOutput().output(feed, outputWriter);
			log.debug("Feed created and sent to the client");
		} catch (IOException e) {
			throw new LogbackFeedException("IO error when writing feed", e);
		} catch (FeedException e) {
			throw new LogbackFeedException("Rome exception", e);
		}
	}

	private SyndFeed createFeedForEvents(List<ILoggingEvent> events) {
		SyndFeed feed = createEmptyFeed();

		for (ILoggingEvent event : events) {
			if (event instanceof LoggingEvent)
				feed.getEntries().add(createEntryFromEvent((LoggingEvent) event));
			else
				log.warn("Event '{}' of type {} cannot be casted to {}", new Object[]{event, event.getClass().getName(), LoggingEvent.class.getName()});
		}
		return feed;
	}

	private SyndEntry createEntryFromEvent(LoggingEvent event) {
		final SyndEntry entry = new SyndEntryImpl();
		entry.setTitle(titleLayout.doLayout(event));
		final SyndContent content = createEntryDescription(event);
		entry.setDescription(content);
		entry.setContents(Collections.singletonList(content));
		entry.setAuthor(author);
		entry.setPublishedDate(new Date(event.getTimeStamp()));
		entry.setUri(uri + "/logback/" + event.getId());
		//TODO [tnurkiewicz] Make link generation pluggable
		//entry.setLink("http://www.example.com");
		entry.setCategories(getCategories(event));
		return entry;
	}

	private List<SyndCategory> getCategories(LoggingEvent event) {
		final SyndCategory levelCategory = new SyndCategoryImpl();
		levelCategory.setName(event.getLevel().toString());

		final SyndCategory loggerCategory = new SyndCategoryImpl();
		final String loggerName = event.getLoggerName();
		loggerCategory.setName(StringUtils.substringAfterLast(loggerName, "."));

		return Arrays.asList(levelCategory, loggerCategory);
	}

	private SyndContent createEntryDescription(ILoggingEvent event) {
		SyndContent content = new SyndContentImpl();
		content.setValue(contentLayout.doLayout(event));
		content.setMode(Content.HTML);
		content.setType("text/html");
		return content;
	}

	private SyndFeed createEmptyFeed() {
		SyndFeed feed = new SyndFeedImpl();
		feedType = "atom_1.0";
		feed.setFeedType(feedType);
		feed.setTitle(feedTitle);
		feed.setDescription(feedDescription);
		feed.setAuthor(author);
		feed.setPublishedDate(new Date());
		feed.setUri(uri);
		return feed;
	}

	private void initLayout(PatternLayout layout, String pattern) {
		layout.setPattern(pattern);
		layout.setContext(loggerContext);
		layout.start();
	}

	public void setTitlePattern(String pattern) {
		titleLayout = new PatternLayout();
		initLayout(titleLayout, pattern);

	}

	public void setContentPattern(String pattern) {
		contentLayout = new PatternLayout();
		initLayout(contentLayout, pattern);
	}

	public void setFeedTitle(String title) {
		this.feedTitle = title;
	}

	public void setFeedDescription(String description) {
		this.feedDescription = description;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public FeedConfig getFeedConfig() {
		return feedConfig;
	}

	public void setFeedConfig(FeedConfig feedConfig) {
		this.feedConfig = feedConfig;
	}

	public void setLoggingEventsSource(LoggingEventsSource loggingEventsSource) {
		this.loggingEventsSource = loggingEventsSource;
	}
}
