/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.sensinact.gateway.datastore.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.sensinact.gateway.datastore.api.DataStoreConnectionProvider;
import org.eclipse.sensinact.gateway.datastore.api.UnableToConnectToDataStoreException;
import org.eclipse.sensinact.gateway.datastore.api.UnableToFindDataStoreException;
import org.eclipse.sensinact.gateway.datastore.api.UnableToLoadDriverClassException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.Function;

/**
 * {@link DataStoreConnectionProvider} implementation dedicated to SQLite
 * databases connections
 */
public class SQLiteConnectionProvider implements DataStoreConnectionProvider<Connection> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SQLiteConnectionProvider.class);
	/**
	 * User defined REGEXP function
	 * 
	 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
	 */
	private static final class SQLiteRegexp extends Function {
		/**
		 * @inheritDoc
		 *
		 * @see org.sqlite.Function#xFunc()
		 */
		@Override
		protected void xFunc() throws SQLException {
			int found = 0;
			int parameters = super.args();
			if (parameters == 2) {
				String patternTxt = super.value_text(0);
				String sequenceTxt = super.value_text(1);
				try {
					Pattern pattern = Pattern.compile(patternTxt);
					if (pattern.matcher(sequenceTxt).matches()) {
						found = 1;
					}
				} catch (PatternSyntaxException e) {
					super.error(e.getMessage());
				}
			}
			super.result(found);
		}
	}

	/**
	 * The data base file
	 */
	private File dbFile;

	/**
	 * the current {@link java.sql.Connection}
	 */
	private Connection connection;

	/**
	 * The number of the current service users
	 */
	private AtomicInteger count;

	/**
	 * Define wether the service has been stopped or not
	 */
	private AtomicBoolean stopped;


	/**
	 * Constructor
	 * 
	 * @throws UnableToFindDataStoreException
	 * 
	 * @throws UnableToLoadDriverClassException
	 */
	public SQLiteConnectionProvider(String dbName)
			throws UnableToConnectToDataStoreException, UnableToFindDataStoreException {
		try {
			// load the sqlite-JDBC driver using the current class loader
			Class.forName("org.sqlite.JDBC");

		} catch (ClassNotFoundException e) {
			throw new UnableToConnectToDataStoreException("'org.sqlite.JDBC' class not found", e);
		}
		connection = null;
		count = new AtomicInteger(0);
		stopped = new AtomicBoolean(false);
		setDataStoreName(dbName);
	}

	/**
	 * Defines the database path
	 * 
	 * @param dbName
	 *            the database path
	 * 
	 * @throws UnableToFindDataStoreException
	 */
	private void setDataStoreName(String dbName) throws UnableToFindDataStoreException {
		// retrieve the data base file
		dbFile = new File(dbName).getAbsoluteFile();
		// if the data base file doesn't exist...
		if (!dbFile.exists()) {
			// ...throw an exception
			throw new UnableToFindDataStoreException("Unable to find data base file : " + dbFile.getAbsolutePath());
		}
	}

	/**
	 * @inheritDoc
	 *
	 * @see DataStoreConnectionProvider#openConnection()
	 */
	public synchronized Connection openConnection() {
		if (stopped.get()) {
			return null;
		}
		// if the connection already exists ...
		if (connection == null) {
			try {
				String connectionString = new StringBuilder("jdbc:sqlite:/").append(dbFile.getAbsolutePath())
						.toString();

				connection = DriverManager.getConnection(connectionString);
				connection.setAutoCommit(false);
				Function.create(connection, "regexp", new SQLiteRegexp());

			} catch (SQLException e) {
				LOG.error("openConnection error :", e);
				return null;
			}
		}
		count.incrementAndGet();
		return connection;
	}

	/**
	 * @inheritDoc
	 *
	 * @see DataStoreConnectionProvider#closeConnection()
	 */
	public synchronized void closeConnection() {
		// if the connection doesn't exists...
		if (connection != null && count.decrementAndGet() == 0) {
			try {
				connection.close();
				connection = null;

			} catch (SQLException e) {
				LOG.error("openConnection error :", e);
			}
		}
	}

	/**
	 * @inheritDoc
	 *
	 * @see DataStoreConnectionProvider# getDataStoreName()
	 */
	public String getDataStoreName() {
		return this.dbFile.getAbsolutePath();
	}

	/**
	 * Stop this {@link DataStoreConnectionProvider}
	 */
	public void stop() {
		stopped.set(true);
	}

	/**
	 * Return the number of users of the current service
	 */
	public int getCount() {
		return count.get();
	}
}
