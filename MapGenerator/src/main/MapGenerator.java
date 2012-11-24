package main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;

import editor.Editor;

import objects.BaseFloor;
import objects.Floor;


public class MapGenerator extends JApplet {

	private static final long serialVersionUID = 1674700860875805629L;

	// INPUT DALLA PAGINA WEB

	// valori globali
	private static final String ID_EDIFICIO = "id";
	private static final String PIANI = "piani";

	// VARIABILI
	private int id_edificio;
	private Editor ed;
	private Floor[] floors;

	public void start() {

		try {
			loadParameters();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ed = new Editor(this.getContentPane(), floors, id_edificio);

		// Rendo "fondo" visibile
		this.add(ed.getPanel());
		Toolkit kit = this.getToolkit();
		Dimension dim = kit.getScreenSize();
		this.setBounds(dim.width / 4, dim.height / 4, dim.width / 4,
				dim.height / 4);
		this.setVisible(true);
		this.repaint();
	}

	/*
	 * public String loadCookie() {
	 * 
	 * try { JSObject myBrowser = (JSObject) JSObject.getWindow(this); JSObject
	 * myDocument = (JSObject) myBrowser.getMember("document"); String myCookie
	 * = (String)myDocument.getMember("cookie"); if (myCookie.length() > 0)
	 * return myCookie;
	 * 
	 * myDocument.call("getFloor", null);
	 * 
	 * } catch (Exception e){ e.printStackTrace(); } return "?";
	 * 
	 * // return this.getParameter("cookie"); }
	 */

	private void loadParameters() throws MalformedURLException {

		id_edificio = Integer.parseInt(this.getParameter(ID_EDIFICIO));
		floors = Floor.parse(this.getParameter(PIANI), this.getCodeBase());
		
		//floors = Floor.parse(this.getParameter(PIANI), new URL("http://127.0.0.1:8000"));
		
	}
}
