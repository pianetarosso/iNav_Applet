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

	public PathArrayList() {
		super();
	}

	protected Path findClicked(Point p, ZoomManager zoom) {

		for (Path i : this)
			if (i.testCollision(p, zoom))
				return i;

		return null;
	}

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
			boolean test = jpi.createConfirmDialog(CONFIRM_DELETE_MESSAGE);
			if (test) {
				this.remove(pathDesignated);
				jpi.updatePanel();
			}
		}

	}

}

/*
 * 
 * Graphics2D antiAlias = (Graphics2D) g;
 * antiAlias.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
 * RenderingHints.VALUE_ANTIALIAS_ON);
 * 
 * ZoomManager zoom = ((JPanelImmagine)this.getParent()).zoom;
 * 
 * antiAlias.setColor(DEFAULT_COLOR); antiAlias.setStroke(spessore);
 * 
 * Point[] coordinates = updateCoordinates(zoom);
 * 
 * 
 * // Line2D line = new Line2D.Double(coordinates[0], coordinates[1]);
 * 
 * //antiAlias.draw(line);
 * 
 * 
 * Line2D line = new Line2D.Double(new Point(2,0), new Point(90, 0)); shape =
 * line;
 * 
 * // AffineTransform at = new AffineTransform().getRotateInstance(45); //
 * antiAlias.transform(at); // antiAlias.rotate(45); antiAlias.draw(line);
 */
