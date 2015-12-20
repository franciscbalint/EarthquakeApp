/**
 * 
 */
package main;

/**
 * @author Balint I. Francisc
 *
 */
public class App {


	public static void main(String[] args) {

		new Thread() {
			@Override
			public void run() {
				javafx.application.Application.launch(Login.class);
			}
		}.start();

		Login login = Login.waitForLogin();
		login.printSomething();
		
	}
	public static void lunchMap() {
		Map.main(new String[]{});
	}
}
