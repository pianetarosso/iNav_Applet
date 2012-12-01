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
		commonMethods(id, floor, this);
	}

	public Marker(Point p, ZoomManager zoom, int id, Floor floor) {
		super(p.x, p.y, zoom, MARKER_COLOR, MARKER_RADIUS);
		commonMethods(id, floor, this);
	}

	public void commonMethods(int id, Floor floor, Marker marker) {
		createMouseMotionListener(marker);
		this.id = id;
		this.floor = floor;
		stopped = true;
	}

	public void setSelected(boolean selected) {
		this.clicked = selected;
		this.repaint();
	}

	public void setStop(boolean stop) {
		this.stopped = stop;
	}
	
	// MOUSE LISTENERS
	// /////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////

	// MOUSE CLICK LISTENER per visualizzare un marker o cancellarlo
	@Override
	public void mouseClicked(MouseEvent arg0) {
		arg0.consume();
		//JPanelImmagine jpi1 = (JPanelImmagine) this.getParent();
		//jpi1.delete(id, "marker");
		if (!stopped) {
			JPanelImmagine jpi = (JPanelImmagine) this.getParent();
			jpi.stopAll(true);
			jpi.editMarker(id);
			floor.setMarkerSelected(id);
		}
	}

	public Point correctPosition(Point p, ZoomManager zoom) {
		int new_x = this.getScaledX(zoom) + p.x;
		int new_y = this.getScaledY(zoom) + p.y;
		System.out.println(new_x+", "+new_y);
		return new Point(new_x, new_y);
	}

	// MOUSE DRAGGED LISTENER
	// implementa il trascinamento dell'oggetto
	// gestisce anche le collisioni
	private void createMouseMotionListener(final Marker m) {

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {

				if (!stopped) {
					JPanelImmagine jpi = (JPanelImmagine) m.getParent();
					ZoomManager zoom = jpi.zoom;
					Point p = arg0.getPoint();
					System.out.println("moveM:"+moveMarker);
					System.out.println("zoom:"+zoom.isPointOnImage(p));
					System.out.println("p:"+p);

					if (moveMarker && zoom.isPointOnImage(correctPosition(p, zoom))) {
						System.out.println("I'm in!");
						int old_x = m.x;
						int old_y = m.y;

						int current_scaled_X = m.getScaledX(zoom);
						int current_scaled_Y = m.getScaledY(zoom);

						int p_x, p_y;

						p_x = current_scaled_X + p.x - MARKER_RADIUS / 2;
						p_y = current_scaled_Y + p.y - MARKER_RADIUS / 2;

						m.setCoordinates(p_x, p_y, zoom);

						System.out.println("new_m:"+m);
						System.out.println(floor.testNear(m));
						if (floor.testNear(m)) {
							m.x = old_x;
							m.y = old_y;
							moveMarker = false;
						}
						else { 
							System.out.println("Update!");
							m.setCoordinates(p_x, p_y, jpi.zoom);
							jpi.updateMarkerLocation(id, p_x, p_y);
						}

						m.repaint();
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

		});
	}


}
