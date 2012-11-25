package objects;

import java.applet.Applet;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class Floor {

	
	private static final String GET_FLOORS = "getFloors();";
	private static final String NUMERO_DI_PIANO = "numero_di_piano";
	private static final String LINK = "link";
	private static final String BEARING = "bearing";
	
	private int numero_di_piano;
	private URL link;
	private float bearing;
	
	private JButton floorButton;
	private BufferedImage image = null;
	
	
	public Floor(int numero_di_piano, URL link, float bearing) {
		this.numero_di_piano = numero_di_piano;
		this.link = link;
		this.bearing = bearing;
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
        System.out.println(i);
        Floor[] floors = new Floor[i];
        
        for (int t=0; t < i; t++) {
        	JSObject jsonFloor = (JSObject) jsonFloors.getSlot(t);
        	
        	try {
	        	int numero_di_piano = Integer.parseInt(jsonFloor.getMember(NUMERO_DI_PIANO).toString());
	        	URL link = new URL(codebase, jsonFloor.getMember(LINK).toString());
	        	float bearing = Float.parseFloat(jsonFloor.getMember(BEARING).toString());
	        	
	        	Floor floor = new Floor(numero_di_piano, link, bearing);
	        	
	        	floors[t] = floor;
	        	
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}	
        }
      
		return floors;
	}
	
	public void setButton(JButton floorButton) {
		this.floorButton = floorButton;
	}
	
	public JButton getButton() {
		return floorButton;
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
	
	public String oString() {
		return numero_di_piano + " " 
				+ bearing + " "
				+ link.toString() + " ";
	}
}
