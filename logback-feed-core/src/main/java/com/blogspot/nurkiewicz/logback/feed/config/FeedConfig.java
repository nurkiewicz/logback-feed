package com.blogspot.nurkiewicz.logback.feed.config;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7309, 6.0.10, 2009-12-20, 15:11:06
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
