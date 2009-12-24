package com.blogspot.nurkiewicz.logback.feed.config;

import com.blogspot.nurkiewicz.logback.LoggingEvent;

/**
 * @author Tomasz Nurkiewicz
 * @since 2009-12-24, 13:22:07
 */
public class FixedLinkGenerator implements EntryLinkGenerator {

	private final String link;

	public FixedLinkGenerator(String link) {
		this.link = link;
	}

	@Override
	public String generateLinkForEvent(LoggingEvent event) {
		return link;
	}
}
