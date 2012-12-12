package drawable;

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
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import objects.CustomPoint;

public abstract class DrawableMarker extends JComponent implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -1934599507737914205L;


	// COLORI //////////////////////////////////////////////////////////////
	
	// Sfondo
	private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

	// Colore bordo durante passaggio sopra con il mouse
	private static final Color SELECTED_COLOR = Color.white;

	// Colore bordo se il marker non è selezionato
	private static final Color NOT_SELECTED_COLOR = Color.black;
	
	// colore del marker se la validatzione non è corretta
	private static final Color VALIDATED_COLOR = Color.green;
	
	// colore del marker se la validazione non è valida
	private static final Color NOT_VALIDATED_COLOR = Color.red;
	
	// valore di trasparenza da applicare quando trascinato
	private static final int ALPHA = 50;
	
	/////////////////////////////////////////////////////////////////////////////
	

	// DIMENSIONI //////////////////////////////////////////////////////////////
	
	// diametro dell'oggetto
	protected static final int DIAMETER = 10;
	
	// distanza minima tra due markers
	private static final int MIN_DISTANCE_BETWEEN_OBJECTS = 17;

	////////////////////////////////////////////////////////////////////////////
	
	
	// GESTORI DEL MOUSE ///////////////////////////////////////////////////////
	
	// variabile per sapere se sull'oggetto è passato il mouse o meno
		private boolean mouseEntered = false;

		// variabile per il trascinamento del marker
		protected boolean moveMarker = false;
		
		// variabile per il "click"
		protected boolean clicked = false;
		
		// variabile che abilita il click e il trascinamento di un marker
		protected boolean stopped = false;
	
	///////////////////////////////////////////////////////////////////////////
		
		
	// posizione dell'oggetto
	public int x, y;
	public ZoomManager zoom;
	
	
	// VALIDAZIONE ///////////////////////////////////////////////////////
	
	// è valido SEMPRE => E' un ascensore, una scala o un ingresso
	public boolean valido = false;
	
	// valore acquisito con la connessione di più path
	public boolean validated = false;

	///////////////////////////////////////////////////////////////////////
	
	

	// COSTRUTTORE //
	protected DrawableMarker(double x, double y, ZoomManager zoom) {
		
		super();

		this.zoom = zoom;
		
		// imposto le coordinate
		int[] coordinates = zoom.getRealPosition(x, y);
		this.x = coordinates[0];
		this.y = coordinates[1];
		
		// abilito i metodi di input
		enableInputMethods(true);

		// abilito i mouselistener
		addMouseListener(this);

		// imposto la dimensione predefinita
		setPreferredSize(new Dimension(DIAMETER, DIAMETER));

		// imposto il colore di background
		setBackground(TRANSPARENT_COLOR);

		// elimino il bordo predefinito
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}
	
	

	// funzione per impostare la POSIZIONE SULL'IMMAGINE
	public void setBounds() {
		
		int[] coordinates = zoom.getPanelPosition(x, y);
		setBounds(coordinates[0] - DIAMETER / 2, coordinates[1] - DIAMETER / 2,
				DIAMETER, DIAMETER);
	}

	// METODI VARI //

	
	
	public Point getScaledMarkerPosition() {
		int[] out = zoom.getPanelPosition(x, y);
		return new Point(out[0], out[1]);
	}
	
	public Point getRealMarkerPosition() {
		return new Point(x, y);
	}

	// testo se i punti sono troppo vicini tra loro (nel contesto scalato, NON reale)
	public boolean testNear(Point p) {

		// se TRUE sono troppo vicini
		return CustomPoint.distance(getScaledMarkerPosition(), p) < MIN_DISTANCE_BETWEEN_OBJECTS;
	}
	
	public void setCoordinates(Point p) {
		int[] position = zoom.getRealPosition(p.x, p.y);
		this.x = position[0];
		this.y = position[1];
		
		this.repaint();
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
		
		
		Color designedColor = NOT_VALIDATED_COLOR;
		
		if (valido || validated)
			designedColor = VALIDATED_COLOR;
		
		g.setColor(designedColor);
		
		// disegno il cerchio della dimensione predisposta
		if (moveMarker && !stopped) {
			
			// "costuisco" un colore uguale a quello designato, ma più trasparente
			Color trans = new Color(
					designedColor.getRed(),
					designedColor.getGreen(),
					designedColor.getBlue(), 
					ALPHA);
			
			g.setColor(trans);
		}
			
		// disegno il marker
		g.fillOval(0, 0, DIAMETER, DIAMETER);

		
		// gestisco la selezione
		if (clicked || (mouseEntered && !stopped))
			g.setColor(SELECTED_COLOR);
		else
			g.setColor(NOT_SELECTED_COLOR);
		g.drawOval(0, 0, DIAMETER - 1, DIAMETER - 1);

		// aggiorno la posizione dell'oggetto sulla mappa
		this.setBounds();
		
		this.repaint();
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
		arg0.consume();
	}
}
