package main;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JApplet;

import objects.Floor;
import editor.Editor;


public class MapGenerator extends JApplet {

	private static final long serialVersionUID = 1674700860875805629L;

	
	// VARIABILI
	private Editor ed;
	private Floor[] floors;

	public void start() {

		floors = Floor.parse(this, this.getCodeBase());
		
		ed = new Editor(this.getContentPane(), floors);

		// Rendo "fondo" visibile
		this.add(ed.getPanel());
		
		Toolkit kit = this.getToolkit();
		Dimension dim = kit.getScreenSize();
		
		this.setBounds(dim.width / 4, dim.height / 4, dim.width / 4, dim.height / 4);
		this.setVisible(true);
		this.repaint();
	}
	
	// funzione chiamata dal JS per cancellare un marker
	public void deleteMarker(int id, int piano) {
		for(Floor f : floors) 
			if (f.numero_di_piano == piano) {
				f.deleteMarker(id);
				break;
			}
	}
	
	// funzione chiamata dal JS, per abilitare di nuovo le azioni sulla mappa
	// (disabilitate fino alla fine dell'editing)
	public void markerSaved() {
		
	}
}
