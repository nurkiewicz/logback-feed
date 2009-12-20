package com.blogspot.nurkiewicz.logback.feed;

import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author Tomasz Nurkiewicz (nurkiewicz)
 * @since 0.0.1, 2009-12-13, 01:04:58
 */
public class LogbackFeedController extends AbstractController {

	private LogbackFeedGenerator logbackFeedGenerator;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		final Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_MONTH, -30);
		logbackFeedGenerator.createAndOutputFeed(date, response.getWriter());
		return null;
	}

	public void setLogbackFeedGenerator(LogbackFeedGenerator logbackFeedGenerator) {
		this.logbackFeedGenerator = logbackFeedGenerator;
	}
}
