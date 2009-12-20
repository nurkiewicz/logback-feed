package com.blogspot.nurkiewicz.logback.feed.config;

import java.util.Collection;

import com.blogspot.nurkiewicz.logback.LoggingEvent;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7309, 6.0.10, 2009-12-20, 15:14:21
 */
public interface EntryLinkGenerator {

	String generateLinkForEvent(LoggingEvent event);

	Collection<String> generateLinksForEvent(LoggingEvent event);

}
