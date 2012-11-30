package drawable;

import gestore_immagini.JPanelImmagine;
import gestore_immagini.ZoomManager;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import objects.Floor;


public class Marker extends DrawableMarker {

	private static final long serialVersionUID = -2609466806655673458L;

	// colore del marker
	private static final Color MARKER_COLOR = Color.red;

	// dimensione del marker
	private static final int MARKER_RADIUS = 10;

	public int id = 0;
	private Floor floor;
	
	// COSTRUTTORI //
	public Marker(double x, double y, ZoomManager zoom, int id, Floor floor) {
		super(x, y, zoom, MARKER_COLOR, MARKER_RADIUS);
		createMouseMotionListener(this);
		this.id = id;
		this.floor = floor;
	}

	public Marker(Point p, ZoomManager zoom, int id, Floor floor) {
		super(p.x, p.y, zoom, MARKER_COLOR, MARKER_RADIUS);
		createMouseMotionListener(this);
		this.id = id;
		this.floor = floor;
	}

	// MOUSE LISTENERS
	// /////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////

	// MOUSE CLICK LISTENER per visualizzare un marker o cancellarlo
	@Override
	public void mouseClicked(MouseEvent arg0) {
		arg0.consume();
		JPanelImmagine jpi = (JPanelImmagine) this.getParent();
		jpi.stopAll(true);
		jpi.editMarker(id);
	}

	// MOUSE DRAGGED LISTENER
	// implementa il trascinamento dell'oggetto
	// gestisce anche le collisioni
	private void createMouseMotionListener(final Marker m) {

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {

				JPanelImmagine jpi = (JPanelImmagine) m.getParent();
				ZoomManager zoom = jpi.zoom;
				Point p = arg0.getPoint();

				if (moveMarker && zoom.isPointOnImage(p)) {

					int old_x = m.x;
					int old_y = m.y;

					int current_scaled_X = m.getScaledX(zoom);
					int current_scaled_Y = m.getScaledY(zoom);

					int p_x, p_y;

					p_x = current_scaled_X + p.x - MARKER_RADIUS / 2;
					p_y = current_scaled_Y + p.y - MARKER_RADIUS / 2;

					if (floor.testNear(m)) {
							m.x = old_x;
							m.y = old_y;
							moveMarker = false;
					}
					else { 
						m.setCoordinates(p_x, p_y, jpi.zoom);
						jpi.updateMarkerLocation(id, p_x, p_y);
					}

					m.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

		});
	}
}
