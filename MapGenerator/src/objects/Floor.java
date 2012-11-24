package objects;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.google.gson.Gson;

public class Floor extends BaseFloor {

	private JButton floorButton;
	private BufferedImage image = null;
	private URL codebase;
	
	
	
	public Floor (BaseFloor bf) {
		this.numero_di_piano = bf.numero_di_piano;
		this.bearing = bf.bearing;
		this.link = bf.link;
	}
	
	
	public static Floor[] parse(String input, URL codebase) {
		System.out.println(input+" "+codebase.toString());
		
		Gson gson = new Gson();
	
		BaseFloor[] bfloors = gson.fromJson(input, BaseFloor[].class);
		System.out.println(bfloors[0].toString());
		Floor[] floors = new Floor[bfloors.length];
		
		for (int i=0; i < bfloors.length; i++) {
			
			Floor f = new Floor(bfloors[i]);
			f.setCodeBase(codebase);
			floors[i] = f;
		}
		return floors;
	}
	
	public void setCodeBase(URL codebase) {
		this.codebase = codebase;
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
		
		URL url = new URL(codebase, link);
		image = ImageIO.read(url);
	}

	public BufferedImage getImage() {
		return image;
	}
}
