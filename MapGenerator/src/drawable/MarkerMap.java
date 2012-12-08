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

	private CommunicationWithJS cwjs;

	private MouseListener ml;


	public MarkerMap(Floor floor, ZoomManager zoom, JPanelImmagine jpi, CommunicationWithJS cwjs) {
		super();
		this.floor = floor;
		this.jpi = jpi;
		this.zoom = zoom;
		
		this.cwjs = cwjs;
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


	// GESTORI DEL LISTENER //
	public void removeMarkersListener() {
		removeJPIListeners();
		for (Map.Entry<Integer, Marker> m : this.entrySet()) 
			m.getValue().stopListeners();
	}

	public void addMarkersListener() {
		setJPIListeners();
		for (Map.Entry<Integer, Marker> m : this.entrySet()) 
			m.getValue().startListeners();
	}

	///////////////////////////


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
	}

	private void removeJPIListeners() {
		jpi.removeMouseListener(ml);
	}
	
	private void setJPIListeners() {

		final MarkerMap mm = this;
		
		jpi.addMouseListener(ml = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (zoom.isPointOnImage(arg0.getPoint())) {

					Marker new_m = addMarker(arg0.getPoint());

					if (new_m != null) { 
						
						if(!cwjs.debug)
							jpi.stopAll(true);

						// aggiungo l'oggetto al JPanel principale
						jpi.add(new_m);

						// imposto la posizione dell'oggetto sul JPanel
						new_m.setBounds();

						new_m.setVisible(true);
						new_m.setEnabled(true);

						setMarkerSelected(new_m.id);

						jpi.updatePanel();

						cwjs.sendNewMarker(new_m, floor.numero_di_piano);
					}
					
					if(jpi.debug && (floor.markers.size() >= 4)) {
						jpi.setDrawOperationType(JPanelImmagine.TYPE_PATH);
						mm.get(0).valido = true;
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
	}

}
