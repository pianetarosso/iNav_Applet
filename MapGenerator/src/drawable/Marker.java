package drawable;

import gestore_immagini.JPanelImmagine;
import gestore_immagini.ZoomManager;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import objects.SimpleMarker;


public class Marker extends DrawableMarker {

	private static final long serialVersionUID = -2609466806655673458L;

	// colore del marker
	private static final Color MARKER_COLOR = Color.red;

	// dimensione del marker
	private static final int MARKER_RADIUS = 10;

	// titolo e testo della finestra di conferma di cancellazione
	private static final String message = "Sei sicuro di voler cancellare questo marker?";

	// tipo di oggetto
	public boolean RFID = false;

	public boolean elevator = false;

	public boolean stair = false;

	public boolean access = false;

	public boolean room = false;

	// Contenitore generico in base al valore sopra
	public String generic_data = null;

	// Contenitore RFID
	public String RFID_data = null;

	// COSTRUTTORI //
	public Marker(double x, double y, ZoomManager zoom) {
		super(x, y, zoom, MARKER_COLOR, MARKER_RADIUS);
		createMouseMotionListener(this);
	}

	public Marker(Point p, ZoomManager zoom) {
		super(p.x, p.y, zoom, MARKER_COLOR, MARKER_RADIUS);
		createMouseMotionListener(this);
	}

	public void setProperties(Object[] object) {

		if (object[0] != null) {
			RFID = true;
			RFID_data = (String) object[0];
		}

		if (object[1] != null)
			access = true;

		else if (object[2] != null) {
			room = true;
			generic_data = (String) object[2];
		}

		else if (object[3] != null) {
			stair = true;
			generic_data = (String) object[3];
		}

		else if (object[4] != null) {
			elevator = true;
			generic_data = (String) object[4];
		}
	}

	// cancellazione del marker
	public void deleteMarker() {
		JPanelImmagine jpi = (JPanelImmagine) this.getParent();
		jpi.remove(this);
		jpi.marker.remove(this);
	}

	// MOUSE LISTENERS
	// /////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////

	// MOUSE CLICK LISTENER per visualizzare il markerDialog di un oggetto
	// quando viene cliccato
	@Override
	public void mouseClicked(MouseEvent arg0) {
		JPanelImmagine jpi = (JPanelImmagine) this.getParent();

		if (jpi.type != JPanelImmagine.TYPE_DELETE) {
			arg0.consume();
			Object[] output = jpi.createMarkerDialog(this);
			if (output != null) {
				jpi.marker.remove(this);
				this.setProperties(output);
				jpi.marker.add(this);
			}
		} else {
			arg0.consume();
			boolean test = jpi.createConfirmDialog(message);

			if (test) {
				deleteMarker();
				jpi.updatePanel();
			}
		}
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

					/*
					 * System.out.println("Mouse x:"+p.x+" y:"+p.y);
					 * System.out.println("New Mouse x:"+p_x+" y:"+p_y);
					 * System.out
					 * .println("Old x:"+current_scaled_X+" y:"+current_scaled_Y
					 * );
					 */
					jpi.marker.remove(m);

					m.setCoordinates(p_x, p_y, jpi.zoom);

					// System.out.print("New coord x:"+m.x+" m.y:"+y+"\n");

					for (Marker i : jpi.marker) {
						if (m.testNear(i)) {
							m.x = old_x;
							m.y = old_y;
							moveMarker = false;
							break;
						}
					}

					jpi.marker.add(m);

					m.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

		});
	}

	public SimpleMarker toSimpleMarker(long id_edificio) {

		SimpleMarker out = new SimpleMarker(id_edificio, x, y, floor, access);

		out.setRFID(RFID, RFID_data);
		out.setElevator(elevator, generic_data);
		out.setRoom(room, generic_data);
		out.setStair(stair, generic_data);

		return out;
	}
}
