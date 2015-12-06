/**
 * 
 */
package main;

import javax.swing.JOptionPane;

/**
 * @author Balint I. Francisc
 *
 */
public class Exceptions extends Exception {

	private static final long serialVersionUID = 1L;

	public Exceptions()
	{ 
		
	}

	public Exceptions(String message)
	{
		super(message);
	}

	/**
	 * @param e
	 */
	public Exceptions(Exception e) {
		String mesg = e.getMessage();
		if (mesg.contains("Communications link failure")) {
			mesg = "Server not responding.";
		}
		System.err.println(mesg);
		JOptionPane.showMessageDialog(null, mesg);
		e.printStackTrace();
	}
}
