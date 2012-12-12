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
		

		JPanelImmagine jpi = (JPanelImmagine) this.getParent();
		if (!stopped && jpi.isMarkerType()) {
			
			jpi.stopAll(true);
			cwjs.editMarker(id);
			floor.setMarkerSelected(id);
			arg0.consume();
		}
		else
			jpi.mouseClicked(arg0);
	}


	// MOUSE DRAGGED LISTENER
	// implementa il trascinamento dell'oggetto
	// gestisce anche le collisioni

	@Override
	public void mouseDragged(MouseEvent arg0) {

		JPanelImmagine jpi = (JPanelImmagine) this.getParent();
		
		if (!stopped && jpi.isMarkerType()) {


			Point p = arg0.getPoint();
			
			Point old_marker_s = zoom.getPanelPosition(new Point(x, y));

			// correggo le coordinate, quelle lette infatti sono in relazione 
			// alla posizione in alto a sx del marker
			p.x = p.x + old_marker_s.x - DIAMETER / 2;
			p.y = p.y + old_marker_s.y - DIAMETER / 2;

			if (moveMarker && zoom.isPointOnImage(p)) {


				if (floor.testNear(p, id)) 
					moveMarker = false;
				else { 
					this.setCoordinates(p);
					cwjs.updateLocation(id, p.x, p.y);
					jpi.isValid();
				}
			}
			arg0.consume();
		}
		else {
			Point p = arg0.getPoint();
			
			Point old_marker_s = zoom.getPanelPosition(new Point(x, y));

			// correggo le coordinate, quelle lette infatti sono in relazione 
			// alla posizione in alto a sx del marker
			p.x = p.x + old_marker_s.x - DIAMETER / 2;
			p.y = p.y + old_marker_s.y - DIAMETER / 2;
			
			MouseEvent me = new MouseEvent(arg0.getComponent(), arg0.getID(), arg0.getWhen(), arg0.getModifiers(), p.x, p.y, arg0.getClickCount(), false);
			
			jpi.mouseDragged(me);
		}
			
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		if (!stopped) {
			moveMarker = true;
			arg0.consume();
		}
		else {
			JPanelImmagine jpi = (JPanelImmagine) this.getParent();
			// MouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger)
			Point coord = this.getScaledMarkerPosition();
			MouseEvent me = new MouseEvent(arg0.getComponent(), arg0.getID(), arg0.getWhen(), arg0.getModifiers(), coord.x, coord.y, arg0.getClickCount(), false);
			jpi.mousePressed(me);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
		if (!stopped) {
			moveMarker = false;
			arg0.consume();
		}
		else {
			JPanelImmagine jpi = (JPanelImmagine) this.getParent();
			Point p = arg0.getPoint();
			
			Point old_marker_s = zoom.getPanelPosition(new Point(x, y));

			// correggo le coordinate, quelle lette infatti sono in relazione 
			// alla posizione in alto a sx del marker
			p.x = p.x + old_marker_s.x - DIAMETER / 2;
			p.y = p.y + old_marker_s.y - DIAMETER / 2;
			
			MouseEvent me = new MouseEvent(arg0.getComponent(), arg0.getID(), arg0.getWhen(), arg0.getModifiers(), p.x, p.y, arg0.getClickCount(), false);
			
			jpi.mouseReleased(me);
		}
	}
	
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}

}
