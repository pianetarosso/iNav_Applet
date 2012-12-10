package gestore_immagini;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import objects.CommunicationWithJS;
import objects.Floor;

public class JPanelImmagine extends JPanel implements MouseListener, MouseMotionListener {

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// COSTANTI
	// //////////////////////////////////////////////////////////////////////////////////

	private static final long serialVersionUID = 1L;

	// colore dello sfondo
	private static final Color BACKGROUND = Color.black;

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// VARIABILI
	// /////////////////////////////////////////////////////////////////////////////////


	// piano selezionato
	private Floor selected_floor = null;
	private Floor[] floors;

	public static final String TYPE_MARKER = "marker";
	public static final String TYPE_PATH = "path";

	// tipo di operazione che si effettua con il click (o trascinamento) del mouse
	public String type = "";

	private String temp_type;

	// contenitore dei percorsi da disegnare
	//public PathArrayList path = new PathArrayList();

	// variabili per la gestione dei zoom e spostamento
	public ZoomManager zoom;
	private MoveManager move;

	public boolean debug = false;


	//////////////////////////////////////////////////////////////////////
	// METODI DI CLASSE e BASE
	// ///////////////////////////////////////////////////////////////////

	// COSTRUTTORE
	public JPanelImmagine(Floor[] floors, CommunicationWithJS cwjs) {

		this.floors = floors;
		this.debug = cwjs.debug;

		// elimino il layout, per impostare gli oggetti con le coordinate
		setLayout(null);

		// imposto il background
		setBackground(BACKGROUND);

		// creo uno ZoomManager
		zoom = new ZoomManager(this);

		// aggiungo i listeners
		addMouseListener(this);
		addMouseMotionListener(this);

		for(Floor f : floors)
			f.initializePathsAndMarkers(this, zoom, cwjs);
	}

	// rendo evidente il costruttore del listener per il ridimensionamento
	public void addResizeListener(Container cp) {
		zoom.changeSizeListener(cp);
	}


	// Disegno del JPanel
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		if (selected_floor != null) {
			// disegno l'immagine scalata secondo il fattore di zoom
			g2.drawRenderedImage(selected_floor.getImage(), zoom.scaleBufferedImage());

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			selected_floor.paths.draw(g2);

			updatePanel();
		}
	}

	// funzione per il redraw della finestra
	public void updatePanel() {
		this.revalidate();
		this.repaint();
	}

	// blocco movimento e creazione di punti, path, oltre allo zoom e al movimento
	public void stopAll(boolean stop) {

		if (!stop && !debug) {
			zoom.enableZoom(true);
			move.disableMovement(false);
			type = temp_type;
			temp_type = "";
		}
		else if (!debug){
			zoom.enableZoom(false);
			move.disableMovement(true);
			temp_type = type;
			type = "";
		}
		selected_floor.stopAllMarkers(stop);
		selected_floor.paths.stop = stop;
		
		if (!stop)
			selected_floor.paths.validate();
	}

	
	
	// COMUNICAZIONE //////////////////////////////////////////////////////////////


	// Imposto una nuova immagine nel panel, e calcolo lo zoom per visualizzare
	// l'immagine a pieno schermo 
	public void setSelectedFloor(int piano) {

		// trovo l'immagine corrispondente al piano
		for (Floor f : floors) 
			if (f.numero_di_piano == piano) {
				this.selected_floor = f;
				break;
			}

		// imposto lo zoom e il movimento
		zoom.setImage(selected_floor.getImage());
		move = new MoveManager(this);

		// imposto la visibilità dei markers a seconda di quale piano viene selezionato
		for (Floor f:floors) 
			f.setVisibleMarkers(selected_floor.numero_di_piano);

		// aggiorno il panel
		updatePanel();

		if (debug)
			setDrawOperationType(TYPE_MARKER);
	}


	// imposto il "metodo" di disegno selezionato
	public void setDrawOperationType(String type) {
		this.type = type;
		System.out.println(type);
		if (type == TYPE_MARKER) {
			selected_floor.markers.addMarkersListener();
			selected_floor.paths.removeMouseListeners();
		}

		if (type == TYPE_PATH) {
			selected_floor.markers.removeMarkersListener();
			selected_floor.paths.addListeners();
		}
	}


	// Cancellazione di un marker o path (funzione chiamata dal JS)
	public void delete(int id, String type) {
		if (type.contains("marker"))
			selected_floor.markers.deleteMarker(id);
		else
			selected_floor.paths.delete();
		
		selected_floor.paths.validate();
		
		updatePanel();
	}

	// elimino le selezioni dei marker e delle path (chiamata dal JS)
	public void resetSelections() {
		selected_floor.setMarkerSelected(Integer.MIN_VALUE);
		selected_floor.paths.selectedPath = null;
		updatePanel();
	}
	
	// funzione che restituisce se i piani sono "validi" o no
	public boolean updateValidation() {

		if (floors.length < 1)
			return false;
		
		for (Floor f : floors) 
			if (!(f.paths.isValid() && f.markers.isValid()))
				return false;
			
		
		return true;
	}

	/////////////////////////////////////////////////////////////////////////////////////



	// stabilisco su che cosa sto operando
	public boolean isMarkerType() {
		return type == TYPE_MARKER;
	}

	public boolean isPathType() {
		return type == TYPE_PATH;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (zoom.isPointOnImage(arg0.getPoint()))
			move.moveImage(arg0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// disabilito l'uso dello zoom
		zoom.enableZoom(false);
		if (zoom.isPointOnImage(arg0.getPoint())) 
			move.setOriginPoint(arg0);
		arg0.consume();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// abilito nuovamente l'uso dello zoom
		zoom.enableZoom(true);
		if (zoom.isPointOnImage(arg0.getPoint())) {
			move.setOriginPoint(null);
			arg0.consume();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}

}
