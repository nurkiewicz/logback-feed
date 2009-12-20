package com.blogspot.nurkiewicz.logback;

import java.util.Map;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.Level;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7304, 6.0.10, 2009-12-06, 21:16:08
 */
public class LoggingEvent implements ILoggingEvent {
	private final long id;
	private final long timestamp;
	private final String formattedMessage;
	private final String loggerName;
	private final String levelString;
	private final String threadName;
	private final short referenceFlag;
	private final Map<String, String> mdc;
	private final StackTraceElement[] stackTraceElement = new StackTraceElement[1];

	public LoggingEvent(long id, long timestamp, String formattedMessage, String loggerName, String levelString, String threadName, short referenceFlag, String callerFilename, String callerClass, String callerMethod, int callerLine, Map<String, String> mdc) {
		this.id = id;
		this.timestamp = timestamp;
		this.formattedMessage = formattedMessage;
		this.loggerName = loggerName;
		this.levelString = levelString;
		this.threadName = threadName;
		this.referenceFlag = referenceFlag;
		this.mdc = mdc;
		stackTraceElement[0] = new StackTraceElement(callerClass, callerMethod, callerFilename, callerLine);
	}

	public String getThreadName() {
		return threadName;
	}

	public Level getLevel() {
		return Level.toLevel(levelString);
	}

	public String getMessage() {
		return formattedMessage;
	}

	public Object[] getArgumentArray() {
		return null;
	}

	public String getFormattedMessage() {
		return formattedMessage;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public LoggerContextVO getLoggerContextVO() {
		return null;
	}

	public IThrowableProxy getThrowableProxy() {
		return null;
	}

	public StackTraceElement[] getCallerData() {
		return stackTraceElement;
	}

	public boolean hasCallerData() {
		return true;
	}

	public Marker getMarker() {
		return null;
	}

	public Map<String, String> getMDCPropertyMap() {
		return mdc;
	}

	public long getTimeStamp() {
		return timestamp;
	}

	public void prepareForDeferredProcessing() {
		//NOP
	}

	public short getReferenceFlag() {
		return referenceFlag;
	}

	public long getId() {
		return id;
	}
}
