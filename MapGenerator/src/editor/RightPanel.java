package editor;

import gestore_immagini.JPanelImmagine;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

// pannello degli strumenti
public class RightPanel extends JPanel {

	private static final long serialVersionUID = -3846264539158311996L;

	private static final String LOADING = "Loading...";
	private static final int ICON_WIDTH = 85;
	private static final int ICON_HEIGHT = 40;

	// ICONE
	//////////////////////////////////////////////////////////////////////

	// Path di base
	private static final String path = "icons/";

	// Estensione comune delle icone
	private static final String icon_extension = "-icon.png";

	// Nome delle varie icone
	private static final String[] icon_name = { "save", "delete", "move",
		"path", "marker" };

	//////////////////////////////////////////////////////////////////////////
	
	
	private Editor ed;
	private JPanelImmagine immagine;
	private JToggleButton[] buttons = new JToggleButton[icon_name.length];

	
	
	public RightPanel(Editor ed, JPanelImmagine immagine) {

		this.ed = ed;
		this.immagine = immagine;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		buildInstruments();
	}





	// Creo il gruppo di pulsanti per gli strumenti
	private void buildInstruments() {

		for (int i = 0; i < buttons.length; i++) {
			JToggleButton button = new JToggleButton();
			button.setText(LOADING);
			button.addActionListener(new ActionButton(icon_name[i], i));
			button.setBorder(null);
			buttons[i] = button;
		}

		for (int i = buttons.length - 1; i > -1; i--) {
			this.add(buttons[i]);
			if (i % 2 != 0)
				this.add(new JSeparator(JSeparator.HORIZONTAL));
		}

		new LoadIcons(this);

	}


	// caricamento async delle immagini, con abilitazione dei pulsanti man mano
	class LoadIcons implements Runnable {

		RightPanel rightPanel;
		
		public LoadIcons(RightPanel rightPanel) {
			Thread ct = Thread.currentThread();
			ct.setName("Master Thread");
			Thread t = new Thread(this, "Load Icons");
			this.rightPanel = rightPanel;
			t.start();
		}

		@Override
		public void run() {


			for (int i = 0; i < icon_name.length; i++) {
				try {
		
					Image img = ImageIO.read(ed.getClass().getResource(
							path + icon_name[i] + icon_extension));
					img = img.getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
							java.awt.Image.SCALE_SMOOTH);
					buttons[i].setText("");
					buttons[i].setIcon(new ImageIcon(img));

				} catch (IOException e) {
					System.out.println("Error loading Icon!");
					e.printStackTrace();
				}
			}
		}
	}



	// listener sul pulsante degli strumenti
	class ActionButton implements ActionListener {

		private String name;
		private int number;

		public ActionButton(String name, int number) {
			this.name = name;
			this.number = number;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			System.out.println(name + " " + number);
			immagine.setDrawOperationType(number);
			
			if (number != 0)
				for (int i = 1; i < buttons.length; i++)
					buttons[i].setEnabled(i != number);
			else
				ed.saveButton();
		}
	}
}
