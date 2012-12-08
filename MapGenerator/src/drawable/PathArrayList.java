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
	private Path selectedPath = null;

	private MouseListener ml = null;
	private MouseMotionListener mml = null;

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
		if (drawingPath.isCorrect())
			this.add(drawingPath);
		drawingPath = null;
		
		validate();
	
	}

	// disegno delle path
	public void draw(Graphics2D g2) {

		if (drawingPath != null)
			drawingPath.draw(g2, null);

		for(Path p : this) 
			p.draw(g2, selectedPath);
	}

	// funzione di validazione delle path
	private void validate() {

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

				// individuo la path più vicina cliccata
				Point point = arg0.getPoint();
				selectedPath = CustomPoint.findNearestPath(point, pal, zoom);	
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// inizio il disegno di una path
				if (drawingPath == null)
					addPath(arg0.getPoint(), markers);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// termino il disegno di una path
				if (drawingPath != null)
					saveNewPath(arg0.getPoint(), markers);
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
				if (drawingPath != null)
					drawingPath(arg0.getPoint());
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



	/*
	private JPanelImmagine jpi;

	private Path drawingPath = null;
	private Path selectedPath = null;

	private Floor floor;

	private MouseListener ml;
	private MouseMotionListener mml;

	// costruttore
	public PathArrayList(Floor floor) {
		super();
		this.floor = floor;
	}

	public void setJpanelImmagine(JPanelImmagine jpi) {
		this.jpi = jpi;
	}

	public void draw() {

		Graphics2D g2 = (Graphics2D) jpi.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);


		// antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (drawingPath != null) {
			g2.setColor(DRAWING_COLOR);
			g2.setStroke(new BasicStroke(SPESSORE));
			g2.draw(drawingPath.getLine());
		}

		for (Path p: this) {

			if (p == selectedPath)
				g2.setColor(SELECTED_COLOR);
			else
				g2.setColor(DEFAULT_COLOR);

			g2.setStroke(new BasicStroke(SPESSORE));

			g2.draw(p.getLine());
		}


	}

	public void addListeners() {
		final PathArrayList pal = this;

		jpi.addMouseListener(ml = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// testo se è su di una path, in caso propongo la cancellazione

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				jpi.updatePanel();
				System.out.println("new Path? "+arg0.getPoint().toString());
				if (jpi.zoom.isPointOnImage(arg0.getPoint())) {
					Path out = testStartPoint(arg0.getPoint());
					if (out != null)
						drawingPath = out;
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (jpi.zoom.isPointOnImage(arg0.getPoint()) && drawingPath != null) {
					testArrivePoint(arg0.getPoint());
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
				System.out.println("drag");
				jpi.updatePanel();
				if (jpi.zoom.isPointOnImage(arg0.getPoint()) && drawingPath != null)
					testArrivePoint(arg0.getPoint());
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {System.out.println("mov");}
		});
	}

	public void removeListeners() {
		jpi.removeMouseListener(ml);
		jpi.removeMouseMotionListener(mml);
	}

	public void addPathPfromMarker(Marker m, ZoomManager zoom) {
		drawingPath = new Path(m, zoom);
		System.out.println("d: "+drawingPath);
	}

	private Path testStartPoint(Point p) {
		System.out.println(p.toString());
		// verifico se il click è stato fatto su di un marker
		for(Map.Entry<Integer, Marker> m : floor.markers.entrySet()) 
			if (Path.distance(p, m.getValue().getScaledMarkerPosition(jpi.zoom)) < MERGE_DISTANCE) 
				return new Path(m.getValue(), jpi.zoom);

		for(Path pt : this) {
			Point new_p = pt.testifNear(p, MERGE_DISTANCE);
			if (new_p != null) 
				return new Path(new_p, jpi.zoom);
		}

		return null;
	}

public void testArrivePoint(Point p) {
		System.out.println("test");
		// verifico se il click è stato fatto su di un marker
	/*	for(Map.Entry<Integer, Marker> m : floor.markers.entrySet()) 
			if (Path.distance(p, m.getValue().getScaledMarkerPosition(jpi.zoom)) < MERGE_DISTANCE) 
				drawingPath.setArrivePoint(m.getValue());
	 */	
	/*
		for(Path pt : this) {
			Point new_p = pt.testifNear(p, MERGE_DISTANCE);
			if (new_p != null) 
				drawingPath.setArrivePoint(new_p);
		}

		drawingPath.setArrivePoint(p);
		this.add(drawingPath);
		drawingPath = null;
	}







	/*

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// trovo la path che è stata cliccata
	protected Path findClicked(Point p, ZoomManager zoom) {

		for (Path i : this)
			if (i.testCollision(p, zoom))
				return i;

		return null;
	}

	// aggiungo una path (con test delle collisioni)
	public boolean add(Path path, ZoomManager zoom) {

		path.testMarkers(floor.markers, zoom);

		for (Path p : this) {

			Path[] out = path.testPathsCollision(p, MERGE_DISTANCE, zoom);

			System.out.println("OUT == NULL:" + (out == null));

			// out[0] => nuova path modificata
			// out[1] => path già presente nell'array modificata
			if (out != null) {
				System.out.println("OUT[0] == NULL:" + (out[0] == null));
				System.out.println("OUT[1] == NULL:" + (out[1] == null));
				if (out[0] != null)
					path = out[0];

				if (out[1] != null) {
					int position = this.indexOf(p);
					this.set(position, out[1]);
				}

			}
		}
		System.out.println("test");


		return this.add(path);
	}


	// cancello una path dall'array
	public void delete(MouseEvent arg0, JPanelImmagine jpi) {

		Point p = arg0.getPoint();
		Path pathDesignated = null;

		for (Path i : this)
			if (i.testMouseClickPosition(p, jpi.zoom)) {
				pathDesignated = i;
				break;
			}

		if (pathDesignated != null) {
			arg0.consume();
			//boolean test = jpi.createConfirmDialog(CONFIRM_DELETE_MESSAGE);
			//if (test) {
				this.remove(pathDesignated);
				jpi.updatePanel();
			//}
		}

	}
	 */
}
