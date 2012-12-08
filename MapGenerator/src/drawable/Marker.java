package drawable;

import gestore_immagini.JPanelImmagine;
import gestore_immagini.ZoomManager;

import java.awt.Point;
import java.awt.event.MouseEvent;

import objects.CommunicationWithJS;
import objects.Floor;


public class Marker extends DrawableMarker {

	private static final long serialVersionUID = -2609466806655673458L;

	public int id = 0;
	private Floor floor;
	private CommunicationWithJS cwjs;


	// COSTRUTTORI //
	public Marker(double x, double y, ZoomManager zoom, int id, Floor floor, CommunicationWithJS cwjs) {
		super(x, y, zoom);
		commonMethods(id, floor, this, cwjs);
	}

	public Marker(Point p, ZoomManager zoom, int id, Floor floor, CommunicationWithJS cwjs) {
		super(p.x, p.y, zoom);
		commonMethods(id, floor, this, cwjs);
	}

	public void commonMethods(int id, Floor floor, Marker marker, CommunicationWithJS cwjs) {
		this.id = id;
		this.floor = floor;
		this.cwjs = cwjs;
		
		this.stopped = false;
		
		// abilito i listener per il trascinamento
		addMouseMotionListener(this);
	}
	
	// imposta il marker come ingresso/scala/ascensore
	public void setAccessElevatorStair() {
		this.valido = true;
	}
	
	// valore per la validazione "in corsa"
	public void setValidated(boolean value) {
		this.validated = value;
	}

	public void setSelected(boolean selected) {
		this.clicked = selected;
		this.repaint();
	}

	public void setStop(boolean stop) {
		this.stopped = stop;
	}

	public void stopListeners() {
		removeMouseListener(this);
		removeMouseMotionListener(this);
	}
	
	public void startListeners() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}


	// MOUSE CLICK LISTENER per visualizzare un marker o cancellarlo //////////////////////////////

	@Override
	public void mouseClicked(MouseEvent arg0) {
		arg0.consume();

		JPanelImmagine jpi = (JPanelImmagine) this.getParent();
		if (!stopped && jpi.isMarkerType()) {
			System.out.println("Marker click");
			jpi.stopAll(true);
			cwjs.editMarker(id);
			floor.setMarkerSelected(id);
		}
	}


	// MOUSE DRAGGED LISTENER
	// implementa il trascinamento dell'oggetto
	// gestisce anche le collisioni

	@Override
	public void mouseDragged(MouseEvent arg0) {

		JPanelImmagine jpi = (JPanelImmagine) this.getParent();
		System.out.println("Marker dragged: "+ stopped +", "+ jpi.isMarkerType());
		if (!stopped && jpi.isMarkerType()) {


			Point p = arg0.getPoint();
			
			Point old_marker_s = zoom.getPanelPosition(new Point(x, y));

			// correggo le coordinate, quelle lette infatti sono in relazione 
			// alla posizione in alto a sx del marker
			p.x = p.x + old_marker_s.x - DIAMETER / 2;
			p.y = p.y + old_marker_s.y - DIAMETER / 2;

			System.out.println("Marker dragged: "+ arg0.getPoint().toString());
			System.out.println("Marker dragged new Coord: "+ p.toString());
			
			if (moveMarker && zoom.isPointOnImage(p)) {


				if (floor.testNear(p, id)) 
					moveMarker = false;
				else { 
					this.setCoordinates(p);
					cwjs.updateLocation(id, p.x, p.y);
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

}
