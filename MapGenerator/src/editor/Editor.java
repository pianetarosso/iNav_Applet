package editor;

import gestore_immagini.JPanelImmagine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import main.Post;

import objects.BaseFloor;
import objects.Floor;
import objects.SimpleMarker;

import com.google.gson.Gson;

import drawable.Marker;
import drawable.MarkerArrayList;
import drawable.Path;
import drawable.PathArrayList;

public class Editor {

	

	private Container cp;

	private Floor selectedFloor = null;

	private static final String ERROR_SAVING = "Errore nel salvataggio! Devi creare mappe per tutti i piani!";

	// STRUTTURE GRAFICHE
	// ///////////////////////////////////////////////////////

	// Sfondo principale
	private JPanel fondo = new JPanel();

	// Sfondo pulsanti sx
	private LeftJPanel piani;

	// Sfondo pulsanti dx
	private RightPanel strumenti;

	// Sfondo centrale
	// private JDisegnaPanel immagine = new JDisegnaPanel();
	private JScrollPane scrollImage;
	private JPanelImmagine immagine;
	// ///////////////////////////////////////////////////////////////////////////

	// ELEMENTI PASSATI DALLA PAGINA
	// ////////////////////////////////////////////

	// array della corrispondenza tra immagini e piani
	private Floor[] floors;

	private int id_edificio;

	// ////////////////////////////////////////////////////////////////////////////

	public Editor(Container cp, Floor[] floors, int id_edificio) {

		this.floors = floors;
		this.cp = cp;
		this.id_edificio = id_edificio;
		

		// imposto il layout del contenitore principale
		fondo.setLayout(new BorderLayout());

		// gestione degli strumenti (lato dx)
		strumenti = new RightPanel(this);
		
		// gestione dei piani (lato sx)
		piani = new LeftJPanel(floors, cp, this);
		
		// immagine (centro)
		scrollImage = buildImagePanel();

		
		// aggiungo tutto al pannello principale
		fondo.add(piani, BorderLayout.WEST);
		fondo.add(strumenti, BorderLayout.EAST);
		fondo.add(scrollImage, BorderLayout.CENTER);

		// click sul piano di default (il più basso)
//		defaultFloorButton.doClick();
//		defaultFloorButton.setSelected(true);
	}

	public void setCurrentFloor(Floor selectedFloor) {
		this.selectedFloor = selectedFloor;
		immagine.setImage(selectedFloor.getImage(), selectedFloor.getFloor());
	}
	
	public void setDrawOperationType(int number) {
		immagine.setDrawOperationType(number);
	}
	
	public JPanel getPanel() {
		return fondo;
	}

	private JScrollPane buildImagePanel() {

		immagine = new JPanelImmagine();

		scrollImage = new JScrollPane(immagine,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollImage.setPreferredSize(new Dimension(200, 200));
		scrollImage.setBackground(Color.BLACK);
		immagine.addResizeListener(scrollImage);

		return scrollImage;
	}

	

	

	

	public void saveButton() {
/*		MarkerArrayList markers = immagine.marker;
		PathArrayList paths = immagine.path;

		boolean[] testMarkers = new boolean[floors.length];
		boolean[] testPaths = new boolean[floors.length];

		for (int i = 0; i < floors.length; i++) {
			testMarkers[i] = false;
			testPaths[i] = false;
		}

		for (int i = 0; i < floors.length; i++) {
			for (Marker m : markers)
				testMarkers[i] = testMarkers[i] || (i == m.floor);
			for (Path p : paths)
				testPaths[i] = testPaths[i] || (i == p.floor);
		}

		boolean test = true;

		for (int i = 0; i < floors.length; i++) {

			if (!(testMarkers[i] || testPaths[i])) {
				displayErrorMessage(ERROR_SAVING);
				floor[i].doClick();
				test = false;
				break;
			}
		}

		if (test)
			saveData(markers, paths);
	}

	private void displayErrorMessage(String message) {

		// Create and set up the window.
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Display the window.
		frame.pack();
		frame.setVisible(true);

		// creo e mostro il markerDialog
		ErrorDialog errorDialog = new ErrorDialog(frame, message);
		errorDialog.pack();
		errorDialog.setVisible(true);

		// elimino la finestra che si viene a creare dopo
		// aver salvato tutti i dati
		frame.dispose();

		// return errorDialog.getValidatedData();
	}

	

	private void saveData(MarkerArrayList markers, PathArrayList paths) {

		URL urlPoint = null;
		URL urlPath = null;

		try {
			urlPoint = new URL("http://127.0.0.1:8000/buildings/generate/point");
			urlPath = new URL("http://127.0.0.1:8000/buildings/generate/path");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		// creo ed invio l'oggetto JSON marker

		ArrayList<SimpleMarker> sm = new ArrayList<SimpleMarker>();

		for (Marker m : markers)
			sm.add(m.toSimpleMarker(id_edificio));

		Gson JMarker = new Gson();
		String data = JMarker.toJson(sm);

		try {
			Post.postData(data, urlPoint, cookie);
		} catch (Exception e) {
			displayErrorMessage("Errore comunicazione con il server!!!");
			e.printStackTrace();
		}
	*/}
	 
}