/**
 * 
 */
package main;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import db.DBQuakes;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import markers.CityMarker;
import markers.CommonMarker;
import markers.EarthquakeMarker;
import markers.LandQuakeMarker;
import markers.OceanQuakeMarker;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * @author Balint I. Francisc
 *
 */
public class app extends PApplet {

	/**
	 * Something to make eclipse happy
	 */
	private static final long serialVersionUID = 1L;

	// The map
	UnfoldingMap map;

	// Save this link: http://earthquake.usgs.gov/earthquakes/feed/v1.0/atom.php
	// Feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";

	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;

	// Used when mouse hovers a marker
	private CommonMarker lastSelected;

	// Used when mouse clicks a marker
	private CommonMarker lastClicked;

	// One time setup the PApplet
	public void setup() {
		
		setTitle("Earthquake App");
		setIcon();

		// // Update&Import database with lathes earthquakes from feed.
		// new ImportEarthquakes(this, earthquakesURL);
		// //
		// // Update&Import into database cities from json.
		// new ImportCities(this, cityFile);

		// (1) Initializing canvas and map tiles
		size(700, 600);
		map = new UnfoldingMap(this, new Google.GoogleTerrainProvider());

		// Add mouse and keyboard interactions
		MapUtils.createDefaultEventDispatcher(this, map);

		map.setZoomRange(1, 3);

		// (2) Reading in earthquake data and geometric properties
		// STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);

		// STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for (Feature city : cities) {
			cityMarkers.add(new CityMarker(city));
		}
		
		 // STEP 3: read in earthquake RSS feed
		  DBQuakes dbquakes = new DBQuakes();
		  List<PointFeature> earthquakes = dbquakes.get();
		 quakeMarkers = new ArrayList<Marker>();
		
		 for (PointFeature feature : earthquakes) {
			// check if LandQuake
			if (isLand(feature)) {
				quakeMarkers.add(new LandQuakeMarker(feature));
			}
			// OceanQuakes
			else {
				quakeMarkers.add(new OceanQuakeMarker(feature));
			}
		 }

		// This is used for debugging
		// printQuakes();
		// System.out
		// .println("Output using file test2.atom with sortAndPrint(5)\n");
		// sortAndPrint(6);
		// System.out.println(
		// "\nOutput using file test2.atom with sortAndPrint(20)\n");
		// sortAndPrint(20);
		// printMatrixBrowse();

		// (3) Add markers to map
		// NOTE: Country markers are not added to the map. They are used
		// for their geometric properties
		map.addMarkers(quakeMarkers);
		map.addMarkers(cityMarkers);
	}

	public void keyReleased() {

		if (lastClicked == null) {
			System.out.println(
					"First select a marker to use the navigation keys.");
			return;
		}
		unhideMarkers();

		lastClicked.setHidden(true);
		PVector cordo = lastClicked.getScreenPosition(map);
		float sum = cordo.x + cordo.y;

		float dif_x = 0;
		float dif_y = 0;

		EarthquakeMarker closetMarker = null;

		if (cordo.y > cordo.x) {
			dif_x = cordo.y - cordo.x;
		} else if (cordo.x > cordo.y) {
			dif_y = cordo.x - cordo.y;
		}

		if (key == 'w') {
			for (Marker quake : quakeMarkers) {

				PVector ch_cordo = ((EarthquakeMarker) quake)
						.getScreenPosition(map);

				float x = ch_cordo.x + dif_x;
				float y = ch_cordo.y + dif_y;

				float ch_sum = ch_cordo.x + ch_cordo.y;

				if (sum > ch_sum && !(x < y)) {
					if (closetMarker == null) {
						closetMarker = (EarthquakeMarker) quake;
						quake.setHidden(false);
					} else {
						EarthquakeMarker quakeMarker = (EarthquakeMarker) quake;
						if (quakeMarker.getDistanceTo(
								lastClicked.getLocation()) < closetMarker
										.getDistanceTo(
												lastClicked.getLocation())) {
							quakeMarker.setHidden(false);
							closetMarker.setHidden(true);
							closetMarker = (EarthquakeMarker) quake;
							hideCitiesMarkers();
						} else {
							quake.setHidden(true);
						}
					}

				} else {
					quake.setHidden(true);
				}
			}
		} else if (key == 'd') {
			for (Marker quake : quakeMarkers) {

				PVector ch_cordo = ((EarthquakeMarker) quake)
						.getScreenPosition(map);

				float x = ch_cordo.x + dif_x;
				float y = ch_cordo.y + dif_y;

				float ch_sum = ch_cordo.x + ch_cordo.y;

				if (sum < ch_sum && !(x < y)) {
					if (closetMarker == null) {
						closetMarker = (EarthquakeMarker) quake;
						quake.setHidden(false);
					} else {
						EarthquakeMarker quakeMarker = (EarthquakeMarker) quake;
						if (quakeMarker.getDistanceTo(
								lastClicked.getLocation()) < closetMarker
										.getDistanceTo(
												lastClicked.getLocation())) {
							quakeMarker.setHidden(false);
							closetMarker.setHidden(true);
							closetMarker = (EarthquakeMarker) quake;
							hideCitiesMarkers();
						} else {
							quake.setHidden(true);
						}
					}
				} else {
					quake.setHidden(true);
				}
			}
		} else if (key == 's') {
			for (Marker quake : quakeMarkers) {

				PVector ch_cordo = ((EarthquakeMarker) quake)
						.getScreenPosition(map);

				float x = ch_cordo.x + dif_x;
				float y = ch_cordo.y + dif_y;

				float ch_sum = ch_cordo.x + ch_cordo.y;

				if (sum < ch_sum && (x < y)) {
					if (closetMarker == null) {
						closetMarker = (EarthquakeMarker) quake;
						quake.setHidden(false);
					} else {
						EarthquakeMarker quakeMarker = (EarthquakeMarker) quake;
						if (quakeMarker.getDistanceTo(
								lastClicked.getLocation()) < closetMarker
										.getDistanceTo(
												lastClicked.getLocation())) {
							quakeMarker.setHidden(false);
							closetMarker.setHidden(true);
							closetMarker = (EarthquakeMarker) quake;
							hideCitiesMarkers();
						} else {
							quake.setHidden(true);
						}
					}
				} else {
					quake.setHidden(true);
				}
			}
		} else if (key == 'a') {
			for (Marker quake : quakeMarkers) {

				PVector ch_cordo = ((EarthquakeMarker) quake)
						.getScreenPosition(map);

				float x = ch_cordo.x + dif_x;
				float y = ch_cordo.y + dif_y;

				float ch_sum = ch_cordo.x + ch_cordo.y;

				if (sum > ch_sum && (x < y)) {
					if (closetMarker == null) {
						closetMarker = (EarthquakeMarker) quake;
						quake.setHidden(false);
					} else {
						EarthquakeMarker quakeMarker = (EarthquakeMarker) quake;
						if (quakeMarker.getDistanceTo(
								lastClicked.getLocation()) < closetMarker
										.getDistanceTo(
												lastClicked.getLocation())) {
							quakeMarker.setHidden(false);
							closetMarker.setHidden(true);
							closetMarker = (EarthquakeMarker) quake;
							hideCitiesMarkers();
						} else {
							quake.setHidden(true);
						}
					}
				} else {
					quake.setHidden(true);
				}
			}
		} else {
			unhideMarkers();
		}
		if (closetMarker == null) {
			lastClicked.setHidden(false);
		}
	}

	/**
	 * Event handler that gets called automatically when the
	 * mouse moves.
	 */
	@Override
	public void mouseMoved() {

		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;

		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		// loop();
	}

	// If there is a marker selected
	private void selectMarkerIfHover(List<Marker> markers) {
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}

		for (Marker m : markers) {
			CommonMarker marker = (CommonMarker) m;
			if (marker.isInside(map, mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}

	/**
	 * The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked() {
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		} else if (lastClicked == null) {
			checkEarthquakesForClick();
			if (lastClicked == null) {
				checkCitiesForClick();
			}
		}
	}

	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick() {
		if (lastClicked != null)
			return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker) marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : quakeMarkers) {
					EarthquakeMarker quakeMarker = (EarthquakeMarker) mhide;
					if (quakeMarker
							.getDistanceTo(marker.getLocation()) > quakeMarker
									.threatCircle()) {
						quakeMarker.setHidden(true);
					}
				}
				return;
			}
		}
	}

	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick() {
		if (lastClicked != null)
			return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker) m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) > marker
							.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}

	// loop over and unhide all markers
	private void unhideMarkers() {
		for (Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}

		for (Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	// loop over and hide cities markers
	private void hideCitiesMarkers() {
		for (Marker marker : cityMarkers) {
			marker.setHidden(true);
		}
	}

	// helper method to draw key in GUI
	private void addKey() {
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);

		int xbase = 25;
		int ybase = 50;

		rect(xbase, ybase, 150, 250);

		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase + 25, ybase + 25);

		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase - CityMarker.TRI_SIZE,
				tri_xbase - CityMarker.TRI_SIZE,
				tri_ybase + CityMarker.TRI_SIZE,
				tri_xbase + CityMarker.TRI_SIZE,
				tri_ybase + CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);

		text("Land Quake", xbase + 50, ybase + 70);
		text("Ocean Quake", xbase + 50, ybase + 90);
		text("Size ~ Magnitude", xbase + 25, ybase + 110);

		fill(255, 255, 255);
		ellipse(xbase + 35, ybase + 70, 10, 10);
		rect(xbase + 35 - 5, ybase + 90 - 5, 10, 10);

		fill(color(255, 255, 0));
		ellipse(xbase + 35, ybase + 140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase + 35, ybase + 160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase + 35, ybase + 180, 12, 12);

		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase + 50, ybase + 140);
		text("Intermediate", xbase + 50, ybase + 160);
		text("Deep", xbase + 50, ybase + 180);

		text("Past hour", xbase + 50, ybase + 200);

		fill(255, 255, 255);
		int centerx = xbase + 35;
		int centery = ybase + 200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx - 8, centery - 8, centerx + 8, centery + 8);
		line(centerx - 8, centery + 8, centerx + 8, centery - 8);

	}

	// Checks whether this quake occurred on land. If it did, it sets the
	// "country" property of its PointFeature to the country where it occurred
	// and returns true. Notice that the helper method isInCountry will
	// set this "country" property already. Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {

		// IMPLEMENT THIS: loop over all countries to check if location is in
		// any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this
		// country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}

		// not inside any country
		return false;
	}

	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the
	// earthquake feature if
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use
		// isInsideByLoc
		if (country.getClass() == MultiMarker.class) {

			// looping over markers making up MultiMarker
			for (Marker marker : ((MultiMarker) country).getMarkers()) {

				// checking if inside
				if (((AbstractShapeMarker) marker)
						.isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country",
							country.getProperty("name"));

					// return if is inside one
					return true;
				}
			}
		}

		// check if inside country represented by SimplePolygonMarker
		else if (((AbstractShapeMarker) country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));

			return true;
		}
		return false;
	}

	/**
	 * Changes the application title
	 * void
	 */
	private void setTitle(String title) {
		frame.setTitle(title);
	}

	/**
	 * Adds a list of icons to be used on different platforms
	 * void
	 */
	private void setIcon() {

		List<Image> icons = new ArrayList<>();

		File pathIcons = new File(
				System.getProperty("user.dir") + "/data/icons/");
		String[] listIcons = pathIcons.list();
		int len = listIcons.length;
		for (int i = 1; i < len; i++) {
			Image image = new ImageIcon(
					pathIcons.getPath() + "/" + listIcons[i])
					.getImage();
			icons.add(image);
		}

		frame.setIconImages(icons);
	}

	/*
	 * Loop Method
	 * void
	 */
	public void draw() {
		background(0);
		map.draw();
		addKey();
		// draw a big X to test the arrow navigation
		// line(200, 50, 200 + 650, 50 + 600);
		// line(200 + 650, 50, 200, 50 + 600);
	}

	// Run PApplet as java application
	public static void main(String[] args) {
		PApplet.main(new String[]{"--present", "main.app"});
	}
}
