package com.blogspot.nurkiewicz.logback.feed.config;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-20, 15:11:06
 */
public class FeedConfig {

	private EntryLinkGenerator entryLinkGenerator;

	public EntryLinkGenerator getEntryLinkGenerator() {
		return entryLinkGenerator;
	}

	public void setEntryLinkGenerator(EntryLinkGenerator entryLinkGenerator) {
		this.entryLinkGenerator = entryLinkGenerator;
	}
}
