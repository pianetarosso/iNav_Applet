package gestore_immagini;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JViewport;

public class MoveManager {

	// minimo spostamento valido per un movimento dell'immagine
	private static final int MIN_REQUIRED_MOVEMENT = 7;

	private Point old_Position = null;

	private JViewport viewport;

	private JPanel imagebox;
	
	private boolean disable = false;

	protected MoveManager(JPanel imagebox) {

		this.imagebox = imagebox;
		this.viewport = (JViewport) imagebox.getParent();
	}

	protected void disableMovement(boolean disable) {
		this.disable = disable;
	}
	
	protected void setOriginPoint(MouseEvent arg0) {
		if (!disable) {
			if (arg0 == null)
				this.old_Position = null;
			else
				this.old_Position = arg0.getPoint();
		}
	}

	protected void moveImage(MouseEvent arg0) {

		// verifico se è stato fatto effettivamente un "click" sull'immagine
		if ((old_Position != null) && !disable) {

			// recupero il nuovo punto
			Point new_Position = arg0.getPoint();

			// recupero il punto della viewport (in alto a sx)
			Point viewp = viewport.getViewPosition();

			// recupero i valori dello spostamento
			int translate_x = old_Position.x - new_Position.x;
			int translate_y = old_Position.y - new_Position.y;

			// piccolo filtro per evitare spostamenti inconsulti
			if (Math.abs(translate_x) <= MIN_REQUIRED_MOVEMENT)
				translate_x = 0;
			if (Math.abs(translate_y) <= MIN_REQUIRED_MOVEMENT)
				translate_y = 0;

			// se è tutto ok, "sposto" il punto di viewport del fattore trovato
			// costruisco un nuovo rettangolo
			// sposto tutto l'ambaradan nella nuova posizione
			if ((translate_x != 0) || (translate_y != 0)) {
				viewp.translate(translate_x, translate_y);
				imagebox.scrollRectToVisible(new Rectangle(viewp, viewport
						.getSize()));
				old_Position.setLocation(new_Position);
				// imagebox.updatePanel();
			}
		}
	}

}
