package objects;

import gestore_immagini.ZoomManager;

import java.applet.Applet;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import drawable.Marker;
import drawable.PathArrayList;

public class Floor {

	
	private static final String GET_FLOORS = "getFloors();";
	private static final String NUMERO_DI_PIANO = "numero_di_piano";
	private static final String LINK = "link";
	private static final String BEARING = "bearing";
	private static final String ID = "id";
	
	private static final String NEW_MARKER = "createNewMarker";
	private static final String EDIT_MARKER = "editMarker()";
	
	public int numero_di_piano;
	private URL link;
	private float bearing;
	private int id;
	
	private JButton floorButton;
	private BufferedImage image = null;
	
	private PathArrayList paths;
	
	private JSObject window;
	
	private int markers_counter = 0;
	
	// struttura (id: marker)
	private Map<Integer, Marker> markers;
	public boolean floorSelected = false;
	
	
	public Floor(int numero_di_piano, URL link, float bearing, int id, JSObject window) {
		this.numero_di_piano = numero_di_piano;
		this.link = link;
		this.bearing = bearing;
		this.id = id;
		
		this.window = window;
		
		paths = new PathArrayList();
		markers = new HashMap<Integer, Marker>();
	}
	
	
	public static Floor[] parse(Applet applet, URL codebase) {
			
		JSObject window = JSObject.getWindow(applet);
        JSObject jsonFloors = (JSObject) window.eval(GET_FLOORS);
        
        int i=0;
        
        for (; i < 50; i++) 
        	try {
        		jsonFloors.getSlot(i);
        	} catch (JSException jse) {
        		break;
        	}
       
        Floor[] floors = new Floor[i];
        
        for (int t=0; t < i; t++) {
        	JSObject jsonFloor = (JSObject) jsonFloors.getSlot(t);
        	
        	try {
	        	int numero_di_piano = Integer.parseInt(jsonFloor.getMember(NUMERO_DI_PIANO).toString());
	        	URL link = new URL(codebase, jsonFloor.getMember(LINK).toString());
	        	float bearing = Float.parseFloat(jsonFloor.getMember(BEARING).toString());
	        	int id = Integer.parseInt(jsonFloor.getMember(ID).toString());
	        	
	        	Floor floor = new Floor(numero_di_piano, link, bearing, id, window);
	        	floors[t] = floor;
	        	
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}	
        		
        }
/*		Floor[] floors = new Floor[2];
		
		try {
			floors[0] = new Floor(0, new URL("http://127.0.0.1:8000/media/floors/IMG_20111009_172117_3.jpg"), 356, 3, null);
			floors[1] = new Floor(1, new URL("http://127.0.0.1:8000/media/floors/IMG_20111009_171138_9.jpg"), 356, 4, null);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
        
		return floors;
	}
	
	public void setVisible(boolean value) {
		
		for (Map.Entry<Integer, Marker> m : markers.entrySet()) {
			
			m.getValue().setVisible(value);
			m.getValue().setEnabled(value);
			m.getValue().repaint();
		}
	}
	
	public void setButton(JButton floorButton) {
		this.floorButton = floorButton;
	}
	
	public JButton getButton() {
		return floorButton;
	}
	
	public PathArrayList getPaths() {
		return paths;
	}
	
	public void enableButton() {
		floorButton.setEnabled(true);
		floorButton.setText(""+numero_di_piano);
	}
	
	public void performClick() {
		floorButton.doClick();
	}
	
	public void setButtonSelected(boolean value) {
		floorButton.setSelected(value);
	}
	
	public void loadImage() throws IOException {
		image = ImageIO.read(link);
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public int getFloor() {
		return numero_di_piano;
	}
	
	public String toString() {
		return id + " "
				+ numero_di_piano + " " 
				+ bearing + " "
				+ link.toString() + " ";
	}
	
	// verifico la vicinanza tra il marker in input e quelli presenti qui
	public boolean testNear(Marker marker) {
		
		for (Map.Entry<Integer, Marker> m : markers.entrySet()) 
			if (marker.testNear(m.getValue())) 
				return true;
		return false;
	}
	
	public Marker addMarker(Point p, ZoomManager zoom) {
		
		Marker new_m = new Marker(p, zoom, buildMarkerId(), this);
		
		if (!testNear(new_m)) {	
			markers.put(buildMarkerId(), new_m);
			markers_counter++;
			sendNewMarker(new_m);
		}	
		return new_m;
	}
	
	// funzione per la costruzione dell'id dei marker
	private int buildMarkerId() {
		if (numero_di_piano == 0)
			return markers_counter;
		if (numero_di_piano > 0)
			return numero_di_piano * 10000 + markers_counter;
		
		return numero_di_piano * -10000 + markers_counter;
	}
	
	
	// chiamata alla funzione JS esterna per la creazione di un nuovo marker
	// createNewMarker(id, x, y, piano)
	private void sendNewMarker(Marker new_m) {
		Object[] out = new Integer[] {new_m.id, new_m.x, new_m.y, numero_di_piano};
		window.call(NEW_MARKER, out);
	}
	
	// chiamata alla funzione JS esterna per l'editing di un marker
	public void editMarker(int id) {
		Object[] out = {id};
		window.call(EDIT_MARKER, out);
	}
	
	// chiamata alla funzione JS esterna per la cancellazione di un marker
	public void deleteMarkerJS(int id) {
		Object[] out = {id};
		window.call(EDIT_MARKER, out);
	}
	
	// cancellazione del marker
	public void deleteMarker(int id) {
		markers.get(id).setVisible(false);
		markers.get(id).setEnabled(false);
		markers.get(id).getParent().remove(markers.get(id));
		markers.remove(id);
	}
	
	// aggiorno le coordinate di un marker
	public void updateLocation(int id, int x, int y) {
		
	}
}
