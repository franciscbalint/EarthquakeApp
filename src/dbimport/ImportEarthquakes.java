/**
 * 
 */
package dbimport;

import java.sql.ResultSet;

import db.Database;
import main.Exceptions;
import processing.core.PApplet;
import processing.data.XML;

/**
 * @author Balint I. Francisc
 *
 */
public class ImportEarthquakes extends Database {

	/*
	 * This method is to parse a GeoRSS feed corresponding to earthquakes around
	 * the globe.
	 * 
	 * @param p - PApplet being used
	 * 
	 * @param fileName - file name or URL for data source
	 */

	public ImportEarthquakes(PApplet p, String fileName) {

		// Init Super Class
		super();

		try {

			XML rss = p.loadXML(fileName);

			// Get all items
			XML[] itemXML = rss.getChildren("entry");

			for (XML element : itemXML) {

				String entry_title = "";
				String entry_age = "";
				float entry_lat = 0;
				float entry_lon = 0;
				float entry_magnitude = 0;
				float entry_depth = 0;

				String entry_id = getStringVal(element, "id");
				if (entry_id == null) {
					entry_id = "";
				} else {
					String[] entry_id_split = entry_id.split(":");
					entry_id = entry_id_split[3];
				}

				String entry_updated = getStringVal(element, "updated");
				String delims = "[^0-9]+";
				String[] tokens = entry_updated.split(delims);
				entry_updated = tokens[0] + "-" + tokens[1]
						+ "-" + tokens[2] + " " + tokens[3] + ":" + tokens[4]
 + ":" + tokens[5];

				// Get location
				XML pointXML = element.getChild("georss:point");

				// Import location if existing
				if (pointXML != null && pointXML.getContent() != null) {
					String pointStr = pointXML.getContent();
					String[] latLon = pointStr.split(" ");
					entry_lat = Float.valueOf(latLon[0]);
					entry_lon = Float.valueOf(latLon[1]);
				}

				// Import title if existing
				String titleStr = getStringVal(element, "title");
				if (titleStr != null) {
					// import
					entry_title = titleStr;
					// get magnitude from title and import
					entry_magnitude = Float
							.parseFloat(titleStr.substring(2, 5));
				}

				// Sets depth(elevation) if existing
				float depthVal = getFloatVal(element, "georss:elev");

				// NOT SURE ABOUT CHECKING ERR CONDITION BECAUSE 0 COULD BE
				// VALID?
				// get one decimal place when converting to km
				int interVal = (int) (depthVal / 100);
				depthVal = (float) interVal / 10;
				entry_depth = Math.abs((depthVal));



				System.out.println("Check for duplicate.");
				if (checkForDuplicates(entry_id, entry_updated)) {
					System.err.println("Duplicate found. \n");
					continue;
				} else {
					System.out.println("No duplicate found.");
				}

				System.out.println("Inserting records into the table...");

				String sql = "INSERT INTO earthquakes " + "VALUES (default,\""
						+ entry_id + "\",\"" + entry_updated + "\",\""
						+ entry_title + "\"," + entry_lat + "," + entry_lon
						+ "," + entry_magnitude + "," + entry_depth + ",\""
						+ entry_age + "\")";
				System.out.println(sql);
				getStatement().executeUpdate(sql);

				// PreparedStatements can use variables and are more efficient

				// preStatement = connect.prepareStatement("insert into
				// oop2-exam-project.earthquakes values (default, ?, ?, ?, ?, ?,
				// ?, ?, ?)");

				// // Parameters start with 1
				// preStatement.setString(1, entry_id);
				// preStatement.setString(2, entry_updated);
				// preStatement.setString(3, entry_title);
				// preStatement.setFloat(4, entry_lat);
				// preStatement.setFloat(5, entry_lon);
				// preStatement.setFloat(6, entry_magnitude);
				// preStatement.setFloat(7, entry_depth);
				// preStatement.setString(8, entry_age);
				// preStatement.executeUpdate();
				System.out.println("Inserted records into the table...");
				// break;

			}

		} catch (Exception e) {
			new Exceptions(e);
		} finally {
			close();
		}
	}
	/**
	 * @param element
	 *            void
	 */
	private boolean checkForDuplicates(String entry_id, String entry_updated) {
		try {
			String sql = "SELECT * FROM earthquakes";
			ResultSet rs = getStatement().executeQuery(sql);
			// STEP 5: Extract data from result set
			while (rs.next()) {
				if (entry_id.equals(rs.getString("entry_id"))) {
					if (entry_updated.equals(rs.getString("updated"))) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			new Exceptions(e);
		}
		return false;
	}
	/*
	 * Get String content from child node.
	 */
	private static String getStringVal(XML itemXML, String tagName) {
		// Sets title if existing
		String str = null;
		XML strXML = itemXML.getChild(tagName);

		// check if node exists and has content
		if (strXML != null && strXML.getContent() != null) {
			str = strXML.getContent();
		}

		return str;
	}

	/*
	 * Get float value from child node
	 */
	private static float getFloatVal(XML itemXML, String tagName) {
		return Float.parseFloat(getStringVal(itemXML, tagName));
	}
}
