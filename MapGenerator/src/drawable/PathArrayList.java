package drawable;

import gestore_immagini.JPanelImmagine;
import gestore_immagini.ZoomManager;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PathArrayList extends ArrayList<Path> {

	private static final long serialVersionUID = 3932536512289067368L;
	private static final int MERGE_DISTANCE = 10;

	private static final String CONFIRM_DELETE_MESSAGE = "Sei sicuro di voler cancellare questa Path?";

	// costruttore
	public PathArrayList() {
		super();
	}

	// trovo la path che è stata cliccata
	protected Path findClicked(Point p, ZoomManager zoom) {

		for (Path i : this)
			if (i.testCollision(p, zoom))
				return i;

		return null;
	}

	// aggiungo una path (con test delle collisioni)
	public boolean add(Path path, ZoomManager zoom) {

		if (this.isEmpty())
			return this.add(path);

		for (Path p : this) {
			if (path.floor == p.floor) {
				Path[] out = path.testPathsCollision(p, MERGE_DISTANCE, zoom);

				System.out.println("OUT == NULL:" + (out == null));

				if (out != null) {
					System.out.println("OUT[0] == NULL:" + (out[0] == null));
					System.out.println("OUT[1] == NULL:" + (out[1] == null));
					if (out[0] != null)
						path = out[0];

					if (out[1] != null) {
						int position = this.indexOf(p);
						this.set(position, out[1]);
					}
				}
			}
		}

		return this.add(path);
	}

	// cancello una path dall'array
	public void delete(MouseEvent arg0, JPanelImmagine jpi) {

		Point p = arg0.getPoint();
		Path pathDesignated = null;

		for (Path i : this)
			if (i.testMouseClickPosition(p, jpi.zoom)) {
				pathDesignated = i;
				break;
			}

		if (pathDesignated != null) {
			arg0.consume();
			//boolean test = jpi.createConfirmDialog(CONFIRM_DELETE_MESSAGE);
			//if (test) {
				this.remove(pathDesignated);
				jpi.updatePanel();
			//}
		}

	}

}
