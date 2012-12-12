package drawable;

import gestore_immagini.JPanelImmagine;
import gestore_immagini.ZoomManager;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import objects.CommunicationWithJS;
import objects.Floor;


public class MarkerMap extends HashMap<Integer, Marker> {


	private static final long serialVersionUID = -7558557304450393312L;

	// contatore dei marker per generare id univoci
	private int markers_counter = 0;

	private Floor floor;
	private ZoomManager zoom;
	private JPanelImmagine jpi;

	public CommunicationWithJS cwjs;


	public MarkerMap(Floor floor, ZoomManager zoom, JPanelImmagine jpi, CommunicationWithJS cwjs) {
		super();
		this.floor = floor;
		this.jpi = jpi;
		this.zoom = zoom;

		this.cwjs = cwjs;
	}


	// restituisco la validità complessiva dei marker
	public boolean isValid() {

		if (this.size() < 1)
			return false;

		for(Map.Entry<Integer, Marker> m : this.entrySet())
			if (!(m.getValue().validated || m.getValue().valido))
				return false;

		return true;
	}

	// imposto la visibilità dei markers quando cambio il piano
	public void setVisibleMarkers(boolean value) {

		for (Map.Entry<Integer, Marker> m : this.entrySet()) {

			m.getValue().setVisible(value);
			m.getValue().setEnabled(value);
			m.getValue().repaint();
		}
	}


	// verifico la vicinanza tra un "marker" (dato dal suo centro e id) in input e i marker dell'array
	public boolean testNear(Point marker, int id) {

		for (Map.Entry<Integer, Marker> m : this.entrySet()) 
			if (m.getValue().testNear(marker) && (id != m.getValue().id)) 
				return true;
		return false;
	}


	public void setMarkerSelected(int id) {
		for (Map.Entry<Integer, Marker> m : this.entrySet())
			m.getValue().setSelected(m.getValue().id == id); 
	}

	public void stopAllMarkers(boolean stop) {
		for (Map.Entry<Integer, Marker> m : this.entrySet())
			m.getValue().setStop(stop);
	}


	public Marker addMarker(Point p) {

		Marker new_m = new Marker(p, zoom, buildMarkerId(), floor, cwjs);

		if (!testNear(p, new_m.id)) {	
			this.put(new_m.id, new_m);
			markers_counter++;
		}	
		else 
			return null;

		jpi.isValid();

		return new_m;
	}

	// funzione per la costruzione dell'id dei marker
	private int buildMarkerId() {

		if (floor.numero_di_piano > 0)
			return floor.numero_di_piano * 10000 + markers_counter;

		return floor.numero_di_piano * -10000 + markers_counter;
	}

	// cancellazione del marker
	public void deleteMarker(int id) {

		Marker m = this.get(id);
		m.setVisible(false);
		m.setEnabled(false);
		m.getParent().remove(m);
		this.remove(id);
		m = null;
		jpi.isValid();
	}



}
