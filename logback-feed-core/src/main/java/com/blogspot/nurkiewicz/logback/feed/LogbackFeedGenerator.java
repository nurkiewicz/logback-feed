package com.blogspot.nurkiewicz.logback.feed;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-12, 21:15:21
 */
public class LogbackFeedGenerator {

	private static final Logger log = LoggerFactory.getLogger(LogbackFeedGenerator.class);

	private LoggingEventsSource loggingEventsSource;
	private FeedConfig feedConfig;

	public LogbackFeedGenerator() {
		feedConfig = new FeedConfig();
	}

	public LogbackFeedGenerator(String titlePattern, String contentPattern) {
		feedConfig = new FeedConfig(titlePattern, contentPattern);
	}

	public SyndFeed createFeedForLogsAfter(Calendar date) {
		return createFeedForLogsAfter(date, feedConfig.getFeedType());
	}

	public SyndFeed createFeedForLogsAfter(Calendar date, String feedType) {
		Validate.notEmpty(feedConfig.getUri(), "Feed URI must not be empty");
		final List<ILoggingEvent> events = loggingEventsSource.getLoggingEventsAfter(date);
		return createFeedForEvents(events, feedType);
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
			throw new LogbackFeedException("Rome framework exception", e);
		}
	}

	private SyndFeed createFeedForEvents(List<ILoggingEvent> events, String feedType) {
		SyndFeed feed = createEmptyFeed(feedType);

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
		entry.setTitle(feedConfig.layoutTitle(event));
		entry.setDescription(createEntryDescription(event));
		entry.setAuthor(feedConfig.getAuthor());
		entry.setPublishedDate(new Date(event.getTimeStamp()));
		entry.setUri(feedConfig.getUriForEvent(event));
		entry.setLink(feedConfig.getEntryLinkGenerator().generateLinkForEvent(event));
		entry.setCategories(getCategories(event));
		return entry;
	}

	private List<SyndCategory> getCategories(LoggingEvent event) {
		List<SyndCategory> categories = new ArrayList<SyndCategory>(2);

		if (feedConfig.isIncludeLoggerNameInCategories()) {
			final SyndCategory loggerCategory = new SyndCategoryImpl();
			final String loggerName = event.getLoggerName();
			loggerCategory.setName(StringUtils.substringAfterLast(loggerName, "."));
			categories.add(loggerCategory);
		}

		if (feedConfig.isIncludeLoggerLevelInCategories()) {
			final SyndCategory levelCategory = new SyndCategoryImpl();
			levelCategory.setName(event.getLevel().toString());
			categories.add(levelCategory);
		}
		return categories;
	}

	private SyndContent createEntryDescription(ILoggingEvent event) {
		SyndContent content = new SyndContentImpl();
		content.setValue(feedConfig.layoutContent(event));
		content.setMode(Content.HTML);
		content.setType("text/html");
		return content;
	}

	private SyndFeed createEmptyFeed(String feedType) {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(feedType);
		feed.setTitle(feedConfig.getFeedTitle());
		feed.setDescription(feedConfig.getFeedDescription());
		feed.setAuthor(feedConfig.getAuthor());
		feed.setPublishedDate(new Date());
		feed.setUri(feedConfig.getUri());
		return feed;
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
