package objects;

import gestore_immagini.JPanelImmagine;
import gestore_immagini.ZoomManager;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import drawable.Marker;
import drawable.MarkerMap;
import drawable.PathArrayList;

public class Floor {


	public int numero_di_piano;
	private URL link;
	private float bearing;
	private int id;

	private BufferedImage image = null;

	public PathArrayList paths;

	

	// struttura (id: marker)
	public MarkerMap markers;


	public Floor(int numero_di_piano, URL link, float bearing, int id) {
		this.numero_di_piano = numero_di_piano;
		this.link = link;
		this.bearing = bearing;
		this.id = id;
	}

	public void initializePathsAndMarkers(JPanelImmagine jpi, ZoomManager zoom, CommunicationWithJS cwjs) {
		markers = new MarkerMap(this, zoom, jpi, cwjs);
		paths = new PathArrayList(jpi, cwjs, markers);
	}

	public void loadImage() throws IOException {
		image = ImageIO.read(link);
	}

	public BufferedImage getImage() {
		return image;
	}

	public String toString() {
		return id + " "
				+ numero_di_piano + " " 
				+ bearing + " "
				+ link.toString() + " ";
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// PATHS
	
	public PathArrayList getPaths() {
		return paths;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// MARKERS
	
	
	// imposto la visibilità dei markers quando cambio il piano
	public void setVisibleMarkers(int piano) {
		markers.setVisibleMarkers(numero_di_piano == piano);
	}
	
	
	// verifico la vicinanza tra un "marker" (dato dal suo centro e id) in input e i marker dell'array
		public boolean testNear(Point marker, int id) {
			return markers.testNear(marker, id);
		}
		
		
		///////////////////////////
		
		
		public void setMarkerSelected(int id) {
			markers.setMarkerSelected(id);
		}
		
		
		public Marker addMarker(Point p) {
			return markers.addMarker(p);
		}

		// cancellazione del marker
		public void deleteMarker(int id) {
			markers.deleteMarker(id);
		}
		
	/////////////////////////////////////////////////////////////////////////////////////////
	
}
