package com.blogspot.nurkiewicz.logback.feed;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-12, 23:52:51
 */
public class LogbackFeedException extends RuntimeException {

	public LogbackFeedException(Throwable cause) {
		super(cause);
	}

	public LogbackFeedException(String message, Throwable cause) {
		super(message, cause);
	}
}
