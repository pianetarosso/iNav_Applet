package main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JApplet;

import objects.CommunicationWithJS;
import objects.Floor;


public class MapGenerator extends JApplet {

	private static final long serialVersionUID = 1674700860875805629L;

	// VARIABILI
	private Editor ed;
	private Floor[] floors;
	private CommunicationWithJS cwjs;
	
	private boolean debug = false;

	public void start() {

		cwjs = new CommunicationWithJS(this, debug);
		floors = cwjs.parseFloors(this.getCodeBase());

		new LoadImages();
		ed = new Editor(this.getContentPane(), floors, cwjs);

		// Rendo "fondo" visibile
		this.add(ed.getPanel());

		Toolkit kit = this.getToolkit();
		Dimension dim = kit.getScreenSize();

		this.setBounds(dim.width / 4, dim.height / 4, dim.width / 4, dim.height / 4);
		this.setVisible(true);
		this.repaint();
	}


	/////////////////////////////////////////////////////////////////////////////////////////////
	// FUNZIONI DI INTERAZIONE CON IL JS DELLA PAGINA


	// SET DEI PIANI E DEGLI OGGETTI DA DISEGNARE//////////////////////////////////////////////

	// FUNZIONE PER IMPOSTARE IL PIANO
	public void setFloor(int floor) {
		ed.setFloor(floor);
	}

	// IMPOSTO IL TIPO DI OPERAZIONE (MARKER, PATH O NONE)
	public void setOperation(String type) {
		ed.setOperationType(type);
	}


	// OPERAZIONI GENERICHE SUI MARKER E IMMAGINI /////////////////////////////////////////////

	// Abilitare di nuovo le azioni sulla mappa
	// queste sono disabilitate quando si crea un nuovo marker o si cerca di editarne uno
	public void operationComplete(boolean saved, int id, String type) {
		ed.operationComplete(saved, id, type);
	}


	// FUNZIONI PER IL CARICAMENTO ASINCRONO DELLE IMMAGINI, CON CONSEGUENTE ABILITAZIONE ////////////////
	// DEI METODI DI INPUT DELLA PAGINA
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
				}

				setFloor(floors[0].numero_di_piano);
				cwjs.enableInput();
			} catch (IOException e) {}

		}

	}
}
