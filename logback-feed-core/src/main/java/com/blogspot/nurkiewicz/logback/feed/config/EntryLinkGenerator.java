package com.blogspot.nurkiewicz.logback.feed.config;

import java.util.Collection;

import com.blogspot.nurkiewicz.logback.LoggingEvent;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-20, 15:14:21
 */
public interface EntryLinkGenerator {

	String generateLinkForEvent(LoggingEvent event);

	Collection<String> generateLinksForEvent(LoggingEvent event);

}
