package main;

import gestore_immagini.JPanelImmagine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import objects.CommunicationWithJS;
import objects.Floor;

public class Editor {

	// STRUTTURE GRAFICHE: ///////////////////////////////////////////////////

	// Sfondo principale
	private JPanel fondo = new JPanel();

	// Sfondo centrale
	private JScrollPane scrollImage;
	private JPanelImmagine immagine;
	
	/////////////////////////////////////////////////////////////////////////

	private CommunicationWithJS cwjs;
	
	
	public Editor(Container cp, Floor[] floors, CommunicationWithJS cwjs) {

		this.cwjs = cwjs;
		
		// imposto il layout del contenitore principale
		fondo.setLayout(new BorderLayout());
		
		// immagine (centro)
		scrollImage = buildImagePanel(floors);

		fondo.add(scrollImage, BorderLayout.CENTER);

	}
	
	public JPanel getPanel() {
		return fondo;
	}

	
	// Creo il pannello di disegno dell'immagine
	private JScrollPane buildImagePanel(Floor[] floors) {

		immagine = new JPanelImmagine(floors, cwjs);

		scrollImage = new JScrollPane(immagine,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//scrollImage.setPreferredSize(new Dimension(200, 200));
		scrollImage.setBackground(Color.BLACK);
		immagine.addResizeListener(scrollImage);

		return scrollImage;
	}

	
	
	// OPERAZIONI DAL JS ESTERNO: ///////////////////////////////////////////////////////////////////
	public void setFloor(int numero) {
		immagine.setSelectedFloor(numero);
		immagine.resetSelections();
	}

	public void setOperationType(String type) {
		immagine.setDrawOperationType(type);
		immagine.resetSelections();
	}
	
	public void operationComplete(boolean saved, int id, String type, boolean access) {
		System.out.println(saved+" "+id+" "+type+" "+access);
		if (saved) 
			
			immagine.validateMarker(id, access);
		
		else 
			immagine.delete(id, type);
			
		
		immagine.stopAll(false);
		immagine.resetSelections();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
}