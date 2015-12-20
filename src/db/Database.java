/**
 * 
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import main.Exceptions;

/**
 * @author Balint I. Francisc
 *
 */
public class Database {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:8889/oop2-exam-project";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";

	protected Connection connect;

	// Store the single instance.
	protected Statement statement = null;

	protected PreparedStatement preStatement = null;
	private ResultSet resultSet = null;

	public Database() {
		try {
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to a selected database...");
			Connection connect = DriverManager.getConnection(DB_URL, USER,
					PASS);
			System.out.println("Connected database successfully...");
			statement = connect.createStatement();
		} catch (Exception e) {
			new Exceptions(e);
		}
	}

	public void importFeed() {
		try {
			// PreparedStatements can use variables and are more efficient
			preStatement = connect.prepareStatement(
					"INSERT INTO eearthquakes values (default, ?, ?, ?, ? , ?, ?)");
			// "myuser, webpage, datum, summery, COMMENTS from
			// feedback.comments");
			// Parameters start with 1
			preStatement.setString(1, "Test");
			preStatement.setString(2, "TestEmail");
			preStatement.setString(3, "TestWebpage");
			preStatement.setDate(4, java.sql.Date.valueOf("2013-09-04"));
			preStatement.setString(5, "TestSummary");
			preStatement.setString(6, "TestComment");
			preStatement.executeUpdate();

		} catch (Exception e) {
			new Exceptions(e);
		} finally {
			close();
		}

	}

	// You need to close the resultSet
	protected void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			new Exceptions(e);
		}
	}

	public Connection getConnection() {
		return connect;
	}

	public Statement getStatement() {
		return statement;
	}

	public ResultSet getResultSet(){
		return resultSet;
	}


	public void debug() {
		if (statement == null)
			return;
		try {
			// Execute SQL Query
			ResultSet result = statement.executeQuery("Select * From users");

			// Get the number of columns
			int columCount = result.getMetaData().getColumnCount();

			// Print every value of every column of the row
			while (result.next()) {
				for (int i = 1; i <= columCount; i++) {
					System.out.println(result.getMetaData().getColumnLabel(i)
							+ ": "
							+ result.getString(i));
				}

			}
		} catch (Exception e) {
			new Exceptions(e);
		}
	}
}
