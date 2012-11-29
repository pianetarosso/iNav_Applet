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

import javax.swing.JPanel;

import objects.Floor;
import drawable.Marker;
import drawable.Path;

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

	
	// piano selezionato
	private Floor selected_floor = null;
	private Floor[] floors;

	public static final int TYPE_MARKER = 4;
	public static final int TYPE_PATH = 3;
	public static final int TYPE_MOVE = 2;
	public static final int TYPE_DELETE = 1;
	// SAVE = 0

	// tipo di operazione che si effettua con il click (o trascinamento) del
	// mouse
	public int type = TYPE_MOVE;

	// contenitore dei percorsi da disegnare
	//public PathArrayList path = new PathArrayList();

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
	public JPanelImmagine(Floor[] floors) {

		this.floors = floors;
		
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
	public void setSelectedFloor(Floor floor) {
		
		zoom.setImage(floor.getImage());
		move = new MoveManager(this);

		this.selected_floor = floor;
		
		// imposto la visibilità dei markers a seconda di quale piano viene selezionato
		for (Floor f:floors) 
			f.setVisible(false);
		selected_floor.setVisible(true);
		
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

		if (selected_floor != null) {
			// disegno l'immagine scalata secondo il fattore di zoom
			g2.drawRenderedImage(selected_floor.getImage(), zoom.scaleBufferedImage());
		}

		// antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (currentPath != null) {
			g2.setColor(Path.DRAWING_COLOR);
			g2.setStroke(new BasicStroke(Path.SPESSORE));
			g2.draw(currentPath.getLine(zoom));
		}

		/*for (Path p:selected_floor.getPaths()) {
			
				g2.setStroke(new BasicStroke(Path.SPESSORE));
				if ((mouseMovedOnPath != null)
						&& p.testMouseClickPosition(mouseMovedOnPath, zoom))
					g2.setColor(Path.SELECTED_COLOR);
				else
					g2.setColor(Path.DEFAULT_COLOR);
				g2.draw(p.getLine(zoom));
		}*/
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
					//	path.delete(arg0, jpi);

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

		Marker new_m = selected_floor.addMarker(p, zoom);
		
		// aggiungo l'oggetto al JPanel principale
		add(new_m);

		// imposto la posizione dell'oggetto sul JPanel
		new_m.setBounds(zoom);

		new_m.setVisible(true);
		new_m.setEnabled(true);

		updatePanel();
	}

	
	// Path //
	private void setPath(Point p, boolean last_point) {
		if (TYPE_PATH == type) {
			if (currentPath == null) {

				System.out.println("new path: " + p.toString());
				//currentPath = new Path(p, selected_floor, zoom);

			} else if (!last_point)
				currentPath.setArrivePoint(p, zoom);

			else if (p != null) {
				currentPath.setArrivePoint(p, zoom);
		//		path.add(currentPath, zoom);
				currentPath = null;
			} else
				currentPath = null;

			updatePanel();
		} else
			currentPath = null;

	}

	
	
	

	

}
