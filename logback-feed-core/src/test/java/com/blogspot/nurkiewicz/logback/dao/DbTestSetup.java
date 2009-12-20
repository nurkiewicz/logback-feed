package com.blogspot.nurkiewicz.logback.dao;

import java.io.IOException;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Tomasz Nurkiewicz (tnurkiewicz)
 * @since CR-7304, 6.0.10, 2009-12-13, 14:37:07
 */
public class DbTestSetup {
	private DataSource dataSource;

	public DataSource createAndSetupDatabase() throws IOException {
		dataSource = new SimpleDriverDataSource(new org.h2.Driver(), "jdbc:h2:mem:logback-feed-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
		setupDatabase();
		return dataSource;
	}

	private void setupDatabase() throws IOException {
		final String ddl = IOUtils.toString(getClass().getResourceAsStream("h2.sql"));
		new TransactionTemplate(new DataSourceTransactionManager(dataSource)).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				new JdbcTemplate(dataSource).update(ddl);
			}
		});
	}


}
