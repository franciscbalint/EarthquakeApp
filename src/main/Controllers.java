/**
 * 
 */
package main;

import java.sql.ResultSet;
import java.sql.Statement;

import db.Database;

/**
 * @author Balint I. Francisc
 *
 */
public class Controllers {

	/**
	 * @param text
	 * @param text2
	 *            void
	 */
	public static int LoginSubmit(String user, String pass) {
		if (!passwordValidates(pass))
			return 2;
		else if (!userInDB(user, pass)) {
			return 3;
		}

		return 1;
	}

	/**
	 * @param pass
	 * @param user
	 * @return
	 * 		boolean
	 */
	private static boolean userInDB(String user, String pass) {
		Database db = new Database();
		Statement statement = db.getStatement();
		try {
			// Execute SQL Query
			ResultSet result = statement
.executeQuery("Select * From users");


			while (result.next()) {
				if (user.equals(result.getString(2))) {
					if (pass.equals(result.getString(3))) {
						return true;
					}
				}

			}

		} catch (Exception e) {
			new Exceptions(e);
		}
		return false;
	}

	public static boolean passwordValidates(String pass) {
		int count = 0;

		if (pass.matches(".*\\d.*"))
			count++;
		if (pass.matches(".*[a-z].*"))
			count++;
		if (pass.matches(".*[A-Z].*"))
			count++;
		if (pass.matches(".*[!”#$%&’()*+,./;:=?_@>-].*"))
			count++;

		return count >= 2;
	}
}
