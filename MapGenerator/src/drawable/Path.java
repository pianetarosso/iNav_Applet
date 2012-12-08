package drawable;

import gestore_immagini.ZoomManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import objects.CustomPoint;

public class Path {



	// Una PATH può essere tirata da/verso un punto su di un'altra PATH o un MARKER

	// Quindi è necessario tenere conto di
	// 	coordinate generiche
	//	oggetti marker
	// 	oggeti path

	// Inoltre bisogna aggiungere metodi di controllo per verificare le CANCELLAZIONI 
	// e la validazione


	// le PATH sono cosituite da 2 CUSTOMPOINT, questi sono in grado di gestire il processo di
	// scansione di marker e path per individuare i più vicini

	private final Color VALIDATED_COLOR = Color.green;
	private final Color NOT_VALIDATED_COLOR = Color.red;
	private final Color SELECTED_COLOR = Color.orange;
	
	private final int SPESSORE = 1;
	
	public CustomPoint P;
	public CustomPoint A;

	ZoomManager zoom;
	public boolean validated = false;


	// costruttore
	Path(CustomPoint P, ZoomManager zoom) {
		this.zoom = zoom;
		this.P = P;
		this.A = P;
	}

	// imposto il "punto di arrivo"
	public void setArrivePoint(CustomPoint A) {
		this.A = A;
	}

	// disegno la linea (per ora senza giochini di validazione riguardo al colore)
	// se la path in input e this sono uguali, vuol dire che è stata selezionata
	protected void draw(Graphics2D g2, Path p) {
		
		Line2D line = new Line2D.Double(P.getPanelPosition(), A.getPanelPosition());
		
		
		//g2.setStroke(new BasicStroke(SPESSORE));
		
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		
		
		// disegno del bordo
		g2.setColor(Color.white);
		
		if (p != null) 
			if (p == this)
				g2.setColor(SELECTED_COLOR);
		
		g2.setStroke(new BasicStroke(SPESSORE+2));
		g2.draw(line);

		// disegno della linea
		if (validated)
			g2.setColor(VALIDATED_COLOR);
		else
			g2.setColor(NOT_VALIDATED_COLOR);	
		g2.setStroke(new BasicStroke(SPESSORE));
		g2.draw(line);
		
		///////////////////////////////////////////////////////////
	}

	public void resetValidation() {
		P.resetValidation();
		A.resetValidation();
		validated = false;
	}
	
	public Point getScaledP() {
		return zoom.getPanelPosition(P);
	}
	
	public Point getScaledA() {
		return zoom.getPanelPosition(A);
	}

	// verifico se la path è collegata ad almeno un marker o ad un'altra path
	public boolean isCorrect() {
		return P.isMarkerOrPath() || A.isMarkerOrPath();
	}
	
	/*
	private static final double COLLISION_ERROR = 10.0;

	// le coordinate di una retta possono essere sia un punto che un Marker
	public Point P = null, A = null;
	public Marker mP = null, mA = null;

	private ZoomManager zoom;

	private boolean vertical_line = false;

	// COSTRUTTORI  PER PUNTI //
	public Path(Point p, ZoomManager zoom) {

		this.zoom = zoom;

		int[] t = zoom.getRealPosition(p.x, p.y);
		P = new Point(t[0], t[1]);
		A = P;
		//	buildMathLine();
	}

	// imposto il "punto di arrivo"
	public void setArrivePoint(Point a) {

		int[] t = zoom.getRealPosition(a.x, a.y);
		mA = null;
		A = new Point(t[0], t[1]);
		//buildMathLine();
	}
	//////////////////////////////////////////////////////////////////////////////////////////

	// COSTRUTTORI PER MARKER//
	public Path(Marker m, ZoomManager zoom) {

		this.zoom = zoom;
		mP = m;
		mA = mP;
		//	buildMathLine();
	}

	// imposto il "punto di arrivo"
	public void setArrivePoint(Marker a) {

		A = null;
		mA = a;
		//buildMathLine();
	}
	//////////////////////////////////////////////////////////////////////////////////////////

	// calcolo la distanza tra due punti
	public static int distance(Point a, Point b) {

		double w = a.x - b.x;
		double h = a.y - b.y;

		double distance = Math.sqrt(w * w + h * h);

		return (int) distance;
	}

	// disegno la linea
	public Line2D getLine() {
		int[] partenza = new int[2];

		if (P == null) {
			partenza[0] = mP.getScaledX(zoom);
			partenza[1] = mP.getScaledY(zoom);
		}
		else 
			partenza = zoom.getPanelPosition(P.x, P.y);


		int[] arrivo = new int[2];

		if (A == null) {
			arrivo[0] = mA.getScaledX(zoom);
			arrivo[1] = mA.getScaledY(zoom);
		}
		else
			arrivo = zoom.getPanelPosition(A.x, A.y);

		return new Line2D.Double(new Point(partenza[0], partenza[1]),
				new Point(arrivo[0], arrivo[1]));
	}

	private Point getScaledPointA() {

		if (A != null) {
			int [] arrivo = zoom.getPanelPosition(A.x, A.y);
			return new Point(arrivo[0], arrivo[1]);
		}

		if (mA != null)
			return Marker.getPanelCoordinates(mA.x, mA.y, zoom);

		return null;
	}

	private Point getScaledPointP() {

		if (P != null) {
			int [] arrivo = zoom.getPanelPosition(P.x, P.y);
			return new Point(arrivo[0], arrivo[1]);
		}

		if (mP != null)
			return Marker.getPanelCoordinates(mP.x, mP.y, zoom);

		return null;
	}

	// verifico se esiste un punto sulla retta distante dal punto dato minore della distanza data
	// in caso lo restituisco
	public Point testifNear(Point p, int DISTANCE) {
		double[] coeff = buildMathLine(getScaledPointP(), getScaledPointA());

		int increment = 1;

		if (P.y > A.y)
			increment = -1;

		Point found = null;
		int distance = DISTANCE;

		for (int i = P.y; i < Math.abs(P.y - A.y); i++) {

			int y = P.y + (i * increment);
			int x = 0;

			if (!vertical_line)
				// x = (y - b) / a
				x = (int)((y - coeff[1]) / coeff[0]);

			Point f = new Point(x,y);

			int d = distance(p, found);

			if (d <= distance) {
				distance = d;
				found = f;
			}			
		}

		return found;
	}


	// costuisco l'equazione y = ax + b che passa per i due punti
		private double[] buildMathLine(Point P, Point A) {
		//	System.out.println("POINT P:" + P.toString() + "\nPOINT A:"
		//			+ A.toString());

			double a, b;
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
				b = 0;
			}

			double[] t = {a,b};

			return t;
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
		//	System.out.println("1 test:" + test_result);
		}
		// test nel caso la retta NON sia verticale, con scostamento massimo
		// COLLISION_ERROR
		else
			test_result = (Math.abs(t.y - (t.x * a + b)) <= COLLISION_ERROR);

//		System.out.println("2 test:" + test_result + ", result:"
//				+ (Math.abs(t.y - (t.x * a + b))));

		// test per verificare se il punto è compreso (come coordinate) tra i
		// punti di partenza
		// e arrivo della nostra retta
		test_result = test_result && (t.x >= Math.min(P.x, A.x))
				&& (t.y >= Math.min(P.y, A.y)) && (t.x <= Math.max(P.x, A.x))
				&& (t.y <= Math.max(P.y, A.y));
	//	System.out.println("3 test:" + test_result);

		return test_result;
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





	public void testMarkers(Map<Integer, Marker> markers, ZoomManager zoom) {

		int MAX_DISTANCE = 17;

		int distanceA = MAX_DISTANCE;
		int distanceP = MAX_DISTANCE;

		Point new_A = null;
		Point new_P = null;

		System.out.println("Path A:"+A+", P:"+P);

		Point scaled_P = Marker.getPanelCoordinates(P.x, P.y, zoom);
		Point scaled_A = Marker.getPanelCoordinates(A.x, A.y, zoom);

		for (Map.Entry<Integer, Marker> m : markers.entrySet()) {
			Point marker = m.getValue().getScaledMarkerPosition(zoom);
			System.out.println("Marker:"+marker.getLocation());

			if (distance(marker, scaled_A) <= distanceA) {
				new_A = m.getValue().getRealMarkerPosition();
				distanceA = distance(marker, scaled_A);
			}

			if (distance(marker, scaled_P) <= distanceP) {
				new_P = m.getValue().getRealMarkerPosition();
				distanceP = distance(marker, scaled_P);
			}

			System.out.println("A:"+distanceA+", P:"+distanceP);
			System.out.println("A:"+distance(marker, scaled_A));
			System.out.println("P:"+distance(marker, scaled_P));
		}

		if (new_A != null)
			this.A = new_A;

		if (new_P != null)
			this.P = new_P;
	}
	 */
}
