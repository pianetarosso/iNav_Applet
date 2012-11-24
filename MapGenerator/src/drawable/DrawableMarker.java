package drawable;

import gestore_immagini.JPanelImmagine;
import gestore_immagini.ZoomManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

public abstract class DrawableMarker extends JComponent implements
		MouseListener {

	private static final long serialVersionUID = -1934599507737914205L;

	// distanza minima tra due markers
	private static final int MIN_DISTANCE_BETWEEN_OBJECTS = 17;

	// Colori dei markers:

	// Sfondo
	private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

	// Colore bordo durante passaggio sopra con il mouse
	private static final Color SELECTED_COLOR = Color.green;

	// colore semitrasparente quando viene trascinato
	private static final Color DRAGGING_COLOR = new Color(255, 0, 0, 185);

	// Colore bordo se il marker non è selezionato
	private static final Color NOT_SELECTED_COLOR = Color.black;

	// diametro dell'oggetto
	private int radius = 10;

	// colore di default
	private Color default_color;

	// posizione dell'oggetto
	protected int x, y;

	// piano del marker
	public int floor;

	// variabile per sapere se sull'oggetto è passato il mouse o meno
	private boolean mouseEntered = false;

	// variabile per il trascinamento del marker
	protected boolean moveMarker = false;

	// COSTRUTTORE //
	protected DrawableMarker(double x, double y, ZoomManager zoom,
			Color default_color, int radius) {

		super();

		// imposto le coordinate
		setCoordinates(x, y, zoom);

		// "salvo" il raggio e il colore
		this.radius = radius;
		this.default_color = default_color;

		// abilito i metodi di input
		enableInputMethods(true);

		// abilito i mouselistener
		addMouseListener(this);

		// imposto la dimensione predefinita
		setPreferredSize(new Dimension(radius, radius));

		// imposto il colore di background
		setBackground(TRANSPARENT_COLOR);

		// elimino il bordo predefinito
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	// funzione per impostare la POSIZIONE SULL'IMMAGINE
	public void setBounds(ZoomManager zoom) {
		setBounds(getScaledX(zoom) - radius / 2, getScaledY(zoom) - radius / 2,
				radius, radius);
	}

	// METODI VARI //

	// imposto le coordinate in base allo zoom
	protected void setCoordinates(double x, double y, ZoomManager zoom) {

		// calcolo la posizione sull'immagine reale
		int[] output = calculateCoordinates(x, y, zoom);

		// "salvo" la posizione
		this.x = output[0];
		this.y = output[1];
	}

	protected static int[] calculateCoordinates(double x, double y,
			ZoomManager zoom) {
		return zoom.getRealPosition(x, y);
	}

	protected static Point getScaledCoordinates(double x, double y,
			ZoomManager zoom) {
		int[] coordinates = calculateCoordinates(x, y, zoom);
		return new Point(coordinates[0], coordinates[1]);
	}

	protected static Point getPanelCoordinates(double x, double y,
			ZoomManager zoom) {
		int[] coordinates = zoom.getPanelPosition(x, y);
		return new Point(coordinates[0], coordinates[1]);
	}

	// recupero la x scalata secondo il nuovo zoom
	protected int getScaledX(ZoomManager zoom) {
		return zoom.getPanelPosition(x, y)[0];
	}

	// recupero la y scalata secondo il nuovo zoom
	protected int getScaledY(ZoomManager zoom) {
		return zoom.getPanelPosition(x, y)[1];
	}

	// testo se i punti sono troppo vicini tra loro
	public boolean testNear(Marker m) {

		if (m.floor == floor) {
			Point in = new Point(x, y);
			Point out = new Point(m.x, m.y);

			// se TRUE sono troppo vicini
			return (Math.abs(in.distance(out)) < MIN_DISTANCE_BETWEEN_OBJECTS);
		}
		return false;
	}

	// funzione per disegnare l'oggetto
	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		// abilito l'anti-aliasing
		Graphics2D antiAlias = (Graphics2D) g;
		antiAlias.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		antiAlias.setStroke(new BasicStroke(1));

		// disegno il cerchio della dimensione predisposta
		if (moveMarker)
			g.setColor(DRAGGING_COLOR);
		else
			g.setColor(default_color);
		g.fillOval(0, 0, radius, radius);

		// gestisco la selezione
		if (mouseEntered)
			g.setColor(SELECTED_COLOR);
		else
			g.setColor(NOT_SELECTED_COLOR);
		g.drawOval(0, 0, radius - 1, radius - 1);

		// aggiorno la posizione dell'oggetto sulla mappa
		JPanelImmagine ji = (JPanelImmagine) this.getParent();
		this.setBounds(ji.zoom);
	}

	// ascoltatori del mouse
	@Override
	public void mouseEntered(MouseEvent arg0) {
		mouseEntered = true;
		arg0.consume();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		mouseEntered = false;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		moveMarker = true;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		moveMarker = false;
	}

}
