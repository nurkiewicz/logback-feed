package com.blogspot.nurkiewicz.logback.dao;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7304, 6.0.10, 2009-12-06, 23:20:36
 */
public class ThrowableProxy implements IThrowableProxy{
	private final StackTraceElementProxy[] stackTraceElementsProxy;

	public ThrowableProxy(StackTraceElementProxy[] stackTraceElementsProxy) {
		this.stackTraceElementsProxy = stackTraceElementsProxy;
	}

	public String getMessage() {
		return null;
	}

	public String getClassName() {
		return null;
	}

	public StackTraceElementProxy[] getStackTraceElementProxyArray() {
		return stackTraceElementsProxy;
	}

	public int getCommonFrames() {
		return 0;
	}

	public IThrowableProxy getCause() {
		return null;
	}
}
