package objects;

import gestore_immagini.ZoomManager;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import drawable.Marker;
import drawable.PathArrayList;

public class Floor {


	public int numero_di_piano;
	private URL link;
	private float bearing;
	private int id;

	private JButton floorButton;
	private BufferedImage image = null;

	private PathArrayList paths;

	private int markers_counter = 0;

	// struttura (id: marker)
	private Map<Integer, Marker> markers;
	public boolean floorSelected = false;


	public Floor(int numero_di_piano, URL link, float bearing, int id) {
		this.numero_di_piano = numero_di_piano;
		this.link = link;
		this.bearing = bearing;
		this.id = id;

		paths = new PathArrayList();
		markers = new HashMap<Integer, Marker>();
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
		}	
		else return null;

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

	// cancellazione del marker
	public void deleteMarker(int id) {
		markers.get(id).setVisible(false);
		markers.get(id).setEnabled(false);
		markers.get(id).getParent().remove(markers.get(id));
		markers.remove(id);
	}
}
