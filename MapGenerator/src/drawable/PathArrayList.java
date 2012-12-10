package drawable;

import gestore_immagini.JPanelImmagine;
import gestore_immagini.ZoomManager;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Map;

import objects.CommunicationWithJS;
import objects.CustomPoint;

public class PathArrayList extends ArrayList<Path> {

	private static final long serialVersionUID = 3932536512289067368L;

	private JPanelImmagine jpi;
	public ZoomManager zoom;
	private Map<Integer, Marker> markers;

	private Path drawingPath = null;
	public Path selectedPath = null;

	private MouseListener ml = null;
	private MouseMotionListener mml = null;

	public boolean stop = false;

	private CommunicationWithJS cwjs;



	// costruttore
	public PathArrayList(JPanelImmagine jpi, Map<Integer, Marker> markers, CommunicationWithJS cwjs) {
		super();

		this.jpi = jpi;
		this.zoom = jpi.zoom;
		this.markers = markers;

		this.cwjs = cwjs;
	}


	// creo una nuova Path
	public void addPath(Point p, Map<Integer, Marker> markers) {
		CustomPoint P = CustomPoint.FindPoint(p.x, p.y, this, markers);

		drawingPath = new Path(P, zoom); 

		validate();
	}

	// restituisco la validità delle path
	public boolean isValid() {
		validate();

		for (Path p : this)
			if (!p.validated)
				return false;

		return true;
	}

	// continuo il disegno di una nuova path
	public void drawingPath(Point a) {
		CustomPoint A = new CustomPoint(a.x, a.y, zoom);
		drawingPath.setArrivePoint(A);
	}

	// salvo una nuova path dopo aver testato che sia collegata ad almeno un marker
	// o una path
	public void saveNewPath(Point a, Map<Integer, Marker> markers) {
		CustomPoint A = CustomPoint.FindPoint(a.x, a.y, this, markers);

		drawingPath.setArrivePoint(A);

		// test dei collegamenti
		if (drawingPath.isCorrect() && drawingPath.isLengthEnougth())
			this.add(drawingPath);
		drawingPath = null;

		validate();
		
		jpi.isValid();
	}

	// disegno delle path
	public void draw(Graphics2D g2) {

		if (drawingPath != null)
			drawingPath.draw(g2, drawingPath);

		for(Path p : this) 
			p.draw(g2, selectedPath);
	}

	// funzione di validazione delle path
	public void validate() {

		for(Path p : this) 
			p.resetValidation();

		int counter = 0, old_counter =0;
		do {
			old_counter = counter;
			for(Path p: this) 
				counter = pathValidation(p, counter);

			if (drawingPath != null) 
				counter = pathValidation(drawingPath, counter);

		} while (old_counter != counter);

		jpi.updatePanel();
	}

	// routine di propagazione della validazione
	private int pathValidation(Path p, int counter) {

		// considero i seguenti casi:
		// # La path NON è valida, ma uno dei suoi CustomPoint si
		// # La path E' valida, ma non entrambi i suoi CustomPoint (particolare attenzione
		// 		in questo caso ai CustomPoint che NON possono essere validati, cioé quelli 
		// 		senza Marker o Path)
		if  ( ((p.P.isValid() || p.A.isValid()) && !p.validated) ||
				((!p.P.isValid() || !p.A.isValid()) && p.validated && p.isCorrect()) ) {

			if (!p.validated) {
				p.validated = true;
				counter++;
			}

			if (!p.P.isValid() && p.P.isMarkerOrPath()) {
				p.P.validate();
				counter++;
			}

			if (!p.A.isValid() && p.A.isMarkerOrPath()) {
				p.A.validate();
				counter++;
			}
		}

		return counter;
	}

	// LISTENERS ////

	public void addListeners() {

		final PathArrayList pal = this;

		jpi.addMouseListener(ml = new MouseListener() {


			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (!stop) {
					// individuo la path più vicina cliccata
					Point point = arg0.getPoint();
					drawingPath = null;
					selectedPath = CustomPoint.findNearestPath(point, pal, zoom);

					if (selectedPath != null) {
						cwjs.deletePath();
						jpi.stopAll(true);
					}
					jpi.updatePanel();
					arg0.consume();
				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// inizio il disegno di una path
				if ((drawingPath == null) && !stop) {
					addPath(arg0.getPoint(), markers);
					selectedPath = null;
					arg0.consume();
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// termino il disegno di una path
				if ((drawingPath != null) && !stop) {
					saveNewPath(arg0.getPoint(), markers);
					arg0.consume();
				}
			}


			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
		});


		jpi.addMouseMotionListener(mml = new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {

				// continuo a disegnare una path
				if ((drawingPath != null) && (zoom.isPointOnImage(arg0.getPoint()) && !stop))
					drawingPath(arg0.getPoint());
				else
					drawingPath = null;
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {}
		});
	}

	// rimuovo i listeners
	public void removeMouseListeners() {
		if (ml != null)
			jpi.removeMouseListener(ml);

		if (mml != null)
			jpi.removeMouseMotionListener(mml);
	}


	// cancello una path dall'array
	public void delete() {

		this.remove(selectedPath);
		selectedPath = null;
		validate();
		
		jpi.isValid();
	}

}
