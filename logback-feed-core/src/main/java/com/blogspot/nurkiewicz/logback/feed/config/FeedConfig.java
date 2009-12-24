package com.blogspot.nurkiewicz.logback.feed.config;

import java.io.IOException;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.blogspot.nurkiewicz.logback.LoggingEvent;
import com.blogspot.nurkiewicz.logback.feed.LogbackFeedException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-20, 15:11:06
 */
public class FeedConfig {

	private static final Logger log = LoggerFactory.getLogger(FeedConfig.class);
	private LoggerContext loggerContext;

	private EntryLinkGenerator entryLinkGenerator = new NoLinkGenerator();
	private PatternLayout titleLayout;
	private PatternLayout contentLayout;
	private String feedTitle;
	private String feedDescription = "Feed generated using Logback logging framework and logback-feed library. See http://nurkiewicz.blogspot.com";
	private String author;
	private String uri;
	private String feedType = "atom_1.0";

	public FeedConfig() {
		try {
			final String titlePattern = IOUtils.toString(FeedConfig.class.getResourceAsStream("title.pattern"));
			final String contentPattern = IOUtils.toString(FeedConfig.class.getResourceAsStream("content.pattern.html"));
			init(titlePattern, contentPattern);
		} catch (IOException e) {
			throw new LogbackFeedException(e);
		}
	}


	private void init(String titlePattern, String contentPattern) {
		loggerContext = ((ch.qos.logback.classic.Logger) log).getLoggerContext();
		setTitlePattern(titlePattern);
		setContentPattern(contentPattern);
	}

	private void initLayout(PatternLayout layout, String pattern) {
		layout.setPattern(pattern);
		layout.setContext(loggerContext);
		layout.start();
	}


	public EntryLinkGenerator getEntryLinkGenerator() {
		return entryLinkGenerator;
	}

	public void setEntryLinkGenerator(EntryLinkGenerator entryLinkGenerator) {
		this.entryLinkGenerator = entryLinkGenerator;
	}

	public void setTitlePattern(String pattern) {
		titleLayout = new PatternLayout();
		initLayout(titleLayout, pattern);

	}

	public void setContentPattern(String pattern) {
		contentLayout = new PatternLayout();
		initLayout(contentLayout, pattern);
	}

	public String getFeedTitle() {
		return feedTitle;
	}

	public void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}

	public String getFeedDescription() {
		return feedDescription;
	}

	public void setFeedDescription(String feedDescription) {
		this.feedDescription = feedDescription;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public String getUriForEvent(LoggingEvent event) {
		return uri + "/logback/" + event.getId();
	}

	public String layoutContent(ILoggingEvent event) {
		return contentLayout.doLayout(event);
	}
}
