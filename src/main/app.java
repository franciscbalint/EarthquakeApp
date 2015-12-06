/**
 * 
 */
package main;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

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

	// feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	// One time setup the PApplet
	public void setup() {
		setTitle("Earthquake App");
		setIcon();
		
		new ImportFeed(this, earthquakesURL);
		
		  
		size(700, 600);
		map = new UnfoldingMap(this, new Google.GoogleTerrainProvider());

		// Add mouse and keyboard interactions
		MapUtils.createDefaultEventDispatcher(this, map);
		map.setZoomRange(1, 3);
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
		map.draw();
	}

	public static void main(String[] args) {
		PApplet.main(new String[]{"--present", "main.app"});
	}

}
