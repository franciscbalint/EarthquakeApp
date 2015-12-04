/**
 * 
 */
package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

/**
 * @author Balint I. Francisc
 *
 */
public class Database {

	Database conn = null;
	Statement stmt = null;

	Database() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:8889/oop2-exam-project", "root",
					"root");

			stmt = ((Connection) conn).createStatement();


		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}
	}

	public void debug() {
		try {

			// Execute SQL Query
			ResultSet result = stmt.executeQuery("Select * From users");

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
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}
	}
}
