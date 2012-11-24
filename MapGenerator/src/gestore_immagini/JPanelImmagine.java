package gestore_immagini;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dialogs.ConfirmDialog;
import dialogs.MarkerDialog;
import drawable.DrawableMarker;
import drawable.Marker;
import drawable.MarkerArrayList;
import drawable.Path;
import drawable.PathArrayList;

public class JPanelImmagine extends JPanel {

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// COSTANTI
	// //////////////////////////////////////////////////////////////////////////////////

	private static final long serialVersionUID = 1L;

	// colore dello sfondo
	private static final Color BACKGROUND = Color.black;

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// VARIABILI
	// /////////////////////////////////////////////////////////////////////////////////

	// immagine visualizzata
	private BufferedImage image = null;

	// piano selezionato
	private int selected_floor;

	public static final int TYPE_MARKER = 4;
	public static final int TYPE_PATH = 3;
	public static final int TYPE_MOVE = 2;
	public static final int TYPE_DELETE = 1;
	// SAVE = 0

	// tipo di operazione che si effettua con il click (o trascinamento) del
	// mouse
	public int type = TYPE_MOVE;

	// contenitore dei marker e degli oggetti da disegnare
	public MarkerArrayList marker = new MarkerArrayList();

	// contenitore dei percorsi da disegnare
	public PathArrayList path = new PathArrayList();

	// variabili per la gestione dei zoom e spostamento
	public ZoomManager zoom;
	private MoveManager move;

	private Path currentPath = null;
	private Point mouseMovedOnPath = null;

	// ////////////////////////////////////////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// METODI DI CLASSE e BASE
	// ///////////////////////////////////////////////////////////////////

	// Costruttore
	public JPanelImmagine() {

		// elimino il layout, per impostare gli oggetti con le coordinate
		setLayout(null);

		// imposto il background
		setBackground(BACKGROUND);

		// creo uno ZoomManager
		zoom = new ZoomManager(this);

		// aggiungo i listeners
		addMouseListeners();

	}

	// rendo evidente il costruttore del listener per il ridimensionamento
	public void addResizeListener(Container cp) {
		zoom.changeSizeListener(cp);
	}

	// Imposto una nuova immagine nel panel, e calcolo lo zoom per visualizzare
	// l'immagine
	// a pieno schermo con lo zoom minimo
	public void setImage(BufferedImage image, int floor) {

		this.image = image;
		this.selected_floor = floor;

		zoom.setImage(image);
		move = new MoveManager(this);

		marker.setVisibleMarkers(floor);

		updatePanel();
	}

	// usato per impostare il "metodo" di disegno selezionato
	public void setDrawOperationType(int type) {
		this.type = type;
	}

	// Disegno del JPanel
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		if (image != null) {
			// disegno l'immagine scalata secondo il fattore di zoom
			g2.drawRenderedImage(image, zoom.scaleBufferedImage());
		}

		// antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (currentPath != null) {
			g2.setColor(Path.DRAWING_COLOR);
			g2.setStroke(new BasicStroke(Path.SPESSORE));
			g2.draw(currentPath.getLine(zoom));
		}

		for (Path p : path)
			if (p.floor == selected_floor) {
				g2.setStroke(new BasicStroke(Path.SPESSORE));
				if ((mouseMovedOnPath != null)
						&& p.testMouseClickPosition(mouseMovedOnPath, zoom))
					g2.setColor(Path.SELECTED_COLOR);
				else
					g2.setColor(Path.DEFAULT_COLOR);
				g2.draw(p.getLine(zoom));
			}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// METODI COMUNI
	// /////////////////////////////////////////////////////////////////////////////

	// funzione per il redraw della finestra
	public void updatePanel() {

		this.revalidate();
		this.repaint();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// GESTIONE DEL DISEGNO
	// ///////////////////////////////////////////////////////////////////////

	private void addMouseListeners() {

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				switch (type) {

				case TYPE_MOVE:
					move.moveImage(arg0);
				case TYPE_PATH:
					if (zoom.isPointOnImage(arg0.getPoint()))
						setPath(arg0.getPoint(), false);

				}
				mouseMovedOnPath = null;

			}

			@Override
			public void mouseMoved(MouseEvent arg0) {

			}
		});
		final JPanelImmagine jpi = this;

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (zoom.isPointOnImage(arg0.getPoint())) {
					switch (type) {

					case TYPE_MARKER:
						setMarker(arg0.getPoint());
						arg0.consume();
					case TYPE_DELETE:
						path.delete(arg0, jpi);

					}
					currentPath = null;
					mouseMovedOnPath = arg0.getPoint();
					updatePanel();
				}

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {

				// disabilito l'uso dello zoom
				zoom.enableZoom(false);
				if (zoom.isPointOnImage(arg0.getPoint())) {

					switch (type) {

					case TYPE_MOVE:
						move.setOriginPoint(arg0);

					}
				}
				arg0.consume();
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {

				// abilito nuovamente l'uso dello zoom
				zoom.enableZoom(true);
				if (zoom.isPointOnImage(arg0.getPoint())) {
					switch (type) {

					case TYPE_MOVE:
						move.setOriginPoint(null);
					case TYPE_PATH:
						setPath(arg0.getPoint(), true);

					}
				} else if (type == TYPE_PATH)
					setPath(null, true);

				arg0.consume();
			}
		});
	}

	// MARKER //
	private void setMarker(Point p) {

		Object[] output = createMarkerDialog(null);

		if (output != null) {
			Marker new_m = new Marker(p, zoom);

			if (!isTooNear(new_m)) {

				new_m.setProperties(output);
				new_m.floor = selected_floor;

				marker.add(new_m);

				// aggiungo l'oggetto al JPanel principale
				add(new_m);

				// imposto la posizione dell'oggetto sul JPanel
				new_m.setBounds(zoom);

				new_m.setVisible(true);
				new_m.setEnabled(true);

				updatePanel();
			}
		}
	}

	// Path //
	private void setPath(Point p, boolean last_point) {
		if (TYPE_PATH == type) {
			if (currentPath == null) {

				System.out.println("new path: " + p.toString());
				currentPath = new Path(p, selected_floor, zoom);

			} else if (!last_point)
				currentPath.setArrivePoint(p, zoom);

			else if (p != null) {
				currentPath.setArrivePoint(p, zoom);
				path.add(currentPath, zoom);
				currentPath = null;
			} else
				currentPath = null;

			updatePanel();
		} else
			currentPath = null;

	}

	/*
	 * // INGRESSO // private void setIngresso(Point p) {
	 * 
	 * Ingresso new_i = new Ingresso(p, zoom);
	 * 
	 * if (!isTooNear(new_i)) {
	 * 
	 * ingresso.add(new_i);
	 * 
	 * // aggiungo l'oggetto al JPanel principale add(new_i);
	 * 
	 * // imposto la posizione dell'oggetto sul JPanel new_i.setBounds(zoom);
	 * 
	 * new_i.setVisible(true); new_i.setEnabled(true);
	 * 
	 * updatePanel(); } }
	 */
	private boolean isTooNear(DrawableMarker dos) {

		for (Marker m : marker) {
			if (dos.testNear(m))
				return true;
		}

		/*
		 * for (Ingresso i:ingresso) { if (dos.testNear(i)) return true; }
		 */return false;
	}

	// costruzione e presentazione del dialog per generare un marker
	public Object[] createMarkerDialog(Marker m) {

		// Create and set up the window.
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Display the window.
		frame.pack();
		frame.setVisible(true);

		// carico i parametri per i test dei dati
		Object[] testParameters = marker.getTestParameters(selected_floor);

		// creo e mostro il markerDialog
		MarkerDialog markerDialog = new MarkerDialog(frame,
				(Object[]) testParameters[0], (Object[]) testParameters[1], m);
		markerDialog.pack();
		markerDialog.setVisible(true);

		// elimino la finestra che si viene a creare dopo
		// aver salvato tutti i dati
		frame.dispose();

		return markerDialog.getValidatedData();
	}

	// dialogo di conferma
	public boolean createConfirmDialog(String message) {

		String title = "Conferma cancellazione";

		// Create and set up the window.
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // this lauche
																	// security
																	// exception.EXIT_ON_CLOSE);

		// Display the window.
		frame.pack();
		frame.setVisible(true);

		// creo e mostro il markerDialog
		ConfirmDialog confirmDialog = new ConfirmDialog(frame, title, message);
		confirmDialog.pack();
		confirmDialog.setVisible(true);

		// elimino la finestra che si viene a creare dopo
		// aver salvato tutti i dati
		frame.dispose();

		return confirmDialog.getValidatedData();
	}

}
