/**
 * 
 */
package db;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import main.Exceptions;

/**
 * @author Balint I. Francisc
 *
 */
public class DBQuakes extends Database {
	List<PointFeature> quakes = new ArrayList<PointFeature>();
	/**
	* 
	*/
	public DBQuakes() {
		super();
		if (statement == null)
			return;
		try {
			// Execute SQL Query
			ResultSet result = statement
					.executeQuery("Select * From earthquakes");

			// Print every value of every column of the row
			while (result.next()) {

				// convert cordonates to location object
				Location cordo = new Location(
						Float.parseFloat(result.getString(5)),
						Float.parseFloat(result.getString(6))
				);

				// store location
				PointFeature quake = new PointFeature(cordo);
				
				String titleStr = result.getString(4);

				quake.putProperty("title", titleStr);
				// get magnitude from title
				quake.putProperty("magnitude",
						Float.parseFloat(result.getString(7)));

				quake.putProperty("depth", result.getString(8));
				// System.out.println(Math.abs(depthVal));


				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				Date quakeDate = dateFormat.parse(result.getString(3));

				String ageStr = "";
				if (System.currentTimeMillis() - quakeDate.getTime() > 30 * 24
						* 60 * 60 * 1000) {
					ageStr = "Past Month";
				} else if (System.currentTimeMillis() - quakeDate.getTime() > 7
						* 24 * 60 * 60 * 1000) {
					ageStr = "Past Week";
				} else if (System.currentTimeMillis() - quakeDate.getTime() > 24
						* 60 * 60 * 1000) {
					ageStr = "Past Day";
				} else if (System.currentTimeMillis() - quakeDate.getTime() > 60
						* 60 * 1000) {
					ageStr = "Past Hour";
				}

				quake.putProperty("age", ageStr);

				quakes.add(quake);
			}

		} catch (Exception e) {
			new Exceptions(e);
		}
	}

	public List<PointFeature> get() {
		return quakes;
	}
}
