package drawable;

import gestore_immagini.ZoomManager;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;

public class Path {

	public static final Color DEFAULT_COLOR = Color.green;
	public static final Color DRAWING_COLOR = new Color(0, 255, 0, 185);
	public static final Color SELECTED_COLOR = Color.orange;
	public static final int SPESSORE = 3;
	private static final double COLLISION_ERROR = 10.0;

	private Point P;
	private Point A;

	public int floor;

	private boolean vertical_line = false;

	protected double a = 0, b = 0;

	// COSTRUTTORI //
	public Path(Point p, int floor, ZoomManager zoom) {

		this.floor = floor;

		int[] t = zoom.getRealPosition(p.x, p.y);
		P = new Point(t[0], t[1]);
		A = P;
		buildMathLine();
	}

	public Line2D getLine(ZoomManager zoom) {
		int[] partenza = zoom.getPanelPosition(P.x, P.y);
		int[] arrivo = zoom.getPanelPosition(A.x, A.y);

		return new Line2D.Double(new Point(partenza[0], partenza[1]),
				new Point(arrivo[0], arrivo[1]));
	}

	// verifico se un punto è presente sulla nostra retta
	protected boolean testCollision(Point test, ZoomManager zoom) {

		int[] t = zoom.getRealPosition(test.x, test.y);

		return testCollisionNotScaledPoint(new Point(t[0], t[1]));
	}

	public boolean testMouseClickPosition(Point test, ZoomManager zoom) {

		int[] t = zoom.getRealPosition(test.x, test.y);
		Point currentPoint = new Point(t[0], t[1]);

		// provo a togliere 1 px a dx, 1 px a sx, 1 px sotto e 1 px sopra

		t = zoom.getRealPosition(test.x - 1, test.y);
		Point Point_shift_sx = new Point(t[0], t[1]);

		t = zoom.getRealPosition(test.x + 1, test.y);
		Point Point_shift_dx = new Point(t[0], t[1]);

		t = zoom.getRealPosition(test.x, test.y - 1);
		Point Point_shift_down = new Point(t[0], t[1]);

		t = zoom.getRealPosition(test.x, test.y + 1);
		Point Point_shift_up = new Point(t[0], t[1]);

		return testCollisionNotScaledPoint(currentPoint)
				|| testCollisionNotScaledPoint(Point_shift_sx)
				|| testCollisionNotScaledPoint(Point_shift_dx)
				|| testCollisionNotScaledPoint(Point_shift_up)
				|| testCollisionNotScaledPoint(Point_shift_down);

	}

	private boolean testCollisionNotScaledPoint(Point t) {

		// verifico se il punto si trova sulla retta passante per i due punti
		// con un errore minimo

		boolean test_result;

		// test nel caso che la retta sia verticale
		if (vertical_line) {
			test_result = (t.x == (int) a);
			System.out.println("1 test:" + test_result);
		}
		// test nel caso la retta NON sia verticale, con scostamento massimo
		// COLLISION_ERROR
		else
			test_result = (Math.abs(t.y - (t.x * a + b)) <= COLLISION_ERROR);

		System.out.println("2 test:" + test_result + ", result:"
				+ (Math.abs(t.y - (t.x * a + b))));

		// test per verificare se il punto è compreso (come coordinate) tra i
		// punti di partenza
		// e arrivo della nostra retta
		test_result = test_result && (t.x >= Math.min(P.x, A.x))
				&& (t.y >= Math.min(P.y, A.y)) && (t.x <= Math.max(P.x, A.x))
				&& (t.y <= Math.max(P.y, A.y));
		System.out.println("3 test:" + test_result);

		return test_result;
	}

	// costuisco l'equazione y = ax + b che passa per i due punti
	private void buildMathLine() {
		System.out.println("POINT P:" + P.toString() + "\nPOINT A:"
				+ A.toString());
		// caso standard
		if (P.x != A.x) {
			// il coefficiente angolare
			a = (double) (P.y - A.y) / (double) (P.x - A.x);
			// elevazione
			b = P.y - (a * P.x);

			// //System.out.println("COEFF a:"+a+", b:"+b+"\n");
			vertical_line = false;
		}
		// caso in cui la linea si una retta verticale
		else {
			vertical_line = true;
			a = P.x;
		}
	}

	// testo la collisione tra due rette e, nel caso si "tocchino" restituisco
	// il punto di intersezione
	// distance di solito è posto a 0, è la distanza massima con cui posso
	// "estendere" una delle rette
	// per far toccare l'altra.
	// Restituisco il punto da cui estendere la corrispettiva path

	protected Path[] testPathsCollision(Path path, int distance,
			ZoomManager zoom) {

		int scaledDistance = (int) (distance / zoom.zoom);
		// //System.out.println("DISTANCE:"+scaledDistance);
		// CASI PARTICOLARI -

		// Rette verticali
		if (vertical_line && path.vertical_line) {
			// System.out.println("VERTICALE!");
			if (a == path.a) {

				Path[] out = test_tng_and_path(this.P, path, scaledDistance);

				if (out[0] != null && out[1] != null)
					return out;
				else
					return test_tng_and_path(this.A, path, scaledDistance);
			} else
				return null;

		}
		// rette orizzontali
		else if ((a == 0) && (path.a == 0)) {
			// System.out.println("ORIZZONTALE!");
			if (b == path.b) {

				Path[] out = test_tng_and_path(this.P, path, scaledDistance);

				if (out[0] != null)
					return out;
				else
					return test_tng_and_path(this.A, path, scaledDistance);
			} else
				return null;
		}
		// TUTTE le altre (qui si presta attenzione anche alla posizione
		// all'interno dell'immagine del punto)
		else {

			// calcolo le coordinate del punto di intersezione

			double x = (path.b - b) / (a - path.a);
			double y = a * x + b;

			int[] pos = zoom.getPanelPosition(x, y);
			Point tng = new Point(pos[0], pos[1]);

			// System.out.println("ORIGINAL X:"+x+", Y:"+y);
			// System.out.println("PANEL X:"+pos[0]+", Y:"+pos[1]);
			// System.out.println("TEST POINT ON IMAGE:"+zoom.isPointOnImage(tng));

			if (zoom.isPointOnImage(tng))
				return test_tng_and_path(new Point((int) x, (int) y), path,
						scaledDistance);

			return null;
		}
	}

	// testo la distanza tra il punto di tangente e la retta
	// in input ho: il PUNTO TANGENTE calcolato tra le due rette
	// la RETTA OSPITE
	// la DISTANZA
	// quello che restituisco è un array di path.
	// POSIZIONE 0 => eventualmente this modificata, o null
	// POSIZIONE 1 => eventualmente l'altra modificata, o null
	// Come procedimento calcolo la distanza tra questo punto ed entrambe le
	// rette
	// poiché c'è la possibilità che debbano essere estese entrambe di un valore
	// inferiore
	// alla distance. In questo caso le restituisco entrambe modificate.

	private Path[] test_tng_and_path(Point tng, Path path, int distance) {

		Path[] out = new Path[2];

		out[0] = null; // retta THIS modificata
		out[1] = null; // retta path modificata

		// //System.out.println("DISTANCE P tng:"+distance(P, tng));
		// //System.out.println("DISTANCE A tng:"+distance(A, tng));
		// //System.out.println("DISTANCE path.P tng:"+distance(path.P, tng));
		// //System.out.println("DISTANCE path.A tng:"+distance(path.A, tng));

		// backup dei valori originali prima di fare danni
		Point old_P = P;
		Point old_A = A;

		Point path_old_P = path.P;
		Point path_old_A = path.A;

		// se la distanza tra il punto e la tangente è minore di distance,
		// riposiziono il punto nella tangente
		if (distance(P, tng) <= distance) {
			this.P = tng;
			out[0] = this;
		} else if (distance(A, tng) <= distance) {
			this.A = tng;
			out[0] = this;
		}

		if (distance(path.P, tng) <= distance) {
			path.P = tng;
			out[1] = path;
		} else if (distance(path.A, tng) <= distance) {
			// System.out.println("Path.A:"+ path.A.toString());
			// System.out.println("tng:"+ tng.toString());
			path.A = tng;
			out[1] = path;
		}

		// nel caso in cui la tangente NON sia contenuta in entrambe le rette,
		// riporto la
		// situazione allo stato precedente.
		if (this.testCollisionNotScaledPoint(tng)
				&& path.testCollisionNotScaledPoint(tng))
			return out;
		else {
			// ripristino i valori originali
			P = old_P;
			A = old_A;

			path.P = path_old_P;
			path.A = path_old_A;

		}
		return null;
	}

	// imposto il "punto di arrivo"
	public void setArrivePoint(Point a, ZoomManager zoom) {

		int[] t = zoom.getRealPosition(a.x, a.y);
		A = new Point(t[0], t[1]);
		buildMathLine();
	}

	// calcolo la distanza tra due punti
	private int distance(Point a, Point b) {

		double w = a.x - b.x;
		double h = a.y - b.y;

		double distance = Math.sqrt(w * w + h * h);

		return (int) distance;
	}
}
