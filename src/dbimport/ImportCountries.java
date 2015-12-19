/**
 * 
 */
package dbimport;

import java.sql.ResultSet;
import java.util.List;

import db.Database;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.geo.Location;
import main.Exceptions;
import markers.CityMarker;
import processing.core.PApplet;

/**
 * @author Balint I. Francisc
 *
 */
public class ImportCountries extends Database {

	/*
	 * This method is to parse a GeoRSS feed corresponding to earthquakes around
	 * the globe.
	 * 
	 * @param p - PApplet being used
	 * 
	 * @param fileName - file name or URL for data source
	 */

	public ImportCountries(PApplet p, String fileName) {
		super();

		try {
			
			List<Feature> countries = GeoJSONReader.loadData(p, fileName);


			for (Feature country : countries) {

				String entry_name = (String) country.getProperty("name");
				String entry_population = (String) country
						.getProperty("population");
				String entry_country = (String) country.getProperty("country");
				String entry_coastal = (String) country.getProperty("coastal");
				CityMarker city_m = new CityMarker(country);
				Location entry_location = city_m.getLocation();
				float entry_lat = entry_location.getLat();
				float entry_lon = entry_location.getLon();

				System.out.println("Check for duplicate.");
				 if (checkForDuplicates(entry_name, entry_country)) {
					System.err.println("Duplicate found. \n");
					continue;
				 } else {
					System.out.println("No duplicate found.");
				 }
				
				 System.out.println("Inserting records into the table...");
				
				String sql = "INSERT INTO cities " + "VALUES (default,\""
						+ entry_name + "\",\"" + entry_population + "\",\""
						+ entry_country + "\",\"" + entry_coastal + "\",\""
						+ entry_lat + "\",\"" + entry_lon + "\")";
				 System.out.println(sql);
				 getStatement().executeUpdate(sql);
				
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
	private boolean checkForDuplicates(String name, String country) {
		try {
			String sql = "SELECT * FROM cities";
			ResultSet rs = getStatement().executeQuery(sql);
			// STEP 5: Extract data from result set
			while (rs.next()) {
				if (name.equals(rs.getString("name"))) {
					if (country.equals(rs.getString("country"))) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			new Exceptions(e);
		}
		return false;
	}
}