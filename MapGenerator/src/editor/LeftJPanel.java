package editor;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import objects.Floor;


// qui ci occupiamo della gestione dei piani
public class LeftJPanel extends JPanel {

	private static final long serialVersionUID = 2860228107825589515L;
	private static final String LOADING = "Loading...";

	private Floor[] floors;
	private Container cp;
	private Editor ed;

	public LeftJPanel(Floor[] floors, Container cp, Editor ed) {
		this.floors = floors;
		this.cp = cp;
		this.ed = ed;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		buildFloors();
		
		for (int i = floors.length - 1; i > -1; i--) 
			this.add(floors[i].getButton());
	}


	// Creo il gruppo di pulsanti per i piani
	private void buildFloors() {



		for (int i = floors.length - 1; i >= 0; i--) {

			JButton button = new JButton();
			button.setText(LOADING);
			button.setEnabled(false);

			button.addActionListener(new SelectFloor(floors[i]));

			floors[i].setButton(button);

		}

		new LoadImages();
	}


	// caricamento async delle immagini, con abilitazione dei pulsanti man mano
	class LoadImages implements Runnable {

		public LoadImages() {
			Thread ct = Thread.currentThread();
			ct.setName("Master Thread");
			Thread t = new Thread(this, "Load Images");
			t.start();
		}

		@Override
		public void run() {
			try {

				for (int i= 0; i < floors.length; i++) {
					Floor f = floors[i];
					f.loadImage();
					f.enableButton();
				}

				floors[floors.length-1].performClick();
			} catch (IOException e) {
				System.out.println("Error loading Image!");
				e.printStackTrace();
			}

		}

	}


	// Listener sui piani per modificare le immagini visualizzate
	class SelectFloor implements ActionListener {

		private Floor floor;

		public SelectFloor(Floor floor) {
			this.floor = floor;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			ed.setCurrentFloor(floor);
			for (Floor f:floors)
				f.setButtonSelected(false);
			floor.setButtonSelected(true);

			cp.invalidate();
			cp.repaint();

		}
	}
}
