package com.blogspot.nurkiewicz.logback.feed.config;

import com.blogspot.nurkiewicz.logback.LoggingEvent;

/**
 * @author Tomasz Nurkiewicz
 * @since 2009-12-24, 13:21:45
 */
public class NoLinkGenerator implements EntryLinkGenerator {

	@Override
	public String generateLinkForEvent(LoggingEvent event) {
		return null;
	}
}
