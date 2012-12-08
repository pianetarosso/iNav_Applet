package objects;

import gestore_immagini.ZoomManager;

import java.awt.Point;
import java.util.Map;

import drawable.Marker;
import drawable.Path;
import drawable.PathArrayList;

public class CustomPoint extends Point{

	private static final long serialVersionUID = -3515325984144028470L;

	public Marker marker = null;
	public Path path = null;

	private final static int MIN_DISTANCE = 10; 

	private ZoomManager zoom;

	// SALVO LE COORDINATE COSÌ COME SONO SULL'IMMAGINE IN DIMENSIONI REALI

	// costruttore generico
	public CustomPoint(int x, int y, ZoomManager zoom) {
		super(zoom.getRealPosition(x, y)[0], zoom.getRealPosition(x, y)[1]);
		this.zoom = zoom;
	}

	// costruttore per marker
	public CustomPoint(Marker m) {
		super(m.x, m.y);
		this.marker = m;
		this.zoom = m.zoom;
	}

	// costruttore per path, questo prende le coordinate sull'immagine REALE!!!!
	public CustomPoint(int x, int y, Path p, ZoomManager zoom) {
		super(x, y);
		this.path = p;
		this.zoom = zoom;
	}

	public Point getPanelPosition() {
		return zoom.getPanelPosition(new Point(x,y));
	}

	public Point getRealPosition() {
		return new Point(x,y);
	}


	public void resetValidation() {
		if (marker != null)
			marker.validated = false;
		
		if (path != null)
			path.validated = false;
	}
	
	public boolean isValid() {
		
		if (marker != null)
			return (marker.validated || marker.valido);
		
		if (path != null)
			return (path.validated);
		
		return false;
	}

	public void validate() {
		if (marker != null)
			marker.validated = true;
		
		if(path != null)
			path.validated = true;
	}

	@Override
	public String toString() {
		
		String out ="(x:"+x+", y:"+y+"); ";
		
		if (marker!=null)
			out += "marker: (x:"+marker.x+", y:"+marker.y+", valido:"+marker.valido+", validated:"+marker.validated+"); ";
		if(path!=null)
			out += "path: (validated:"+path.validated+"); ";
		
		return out;
	}
	// date le coordinate restituisco un CustomPoint con tanto di marker o Path vicina
	public static CustomPoint FindPoint(int x, int y, PathArrayList paths, Map<Integer, Marker> markers) {
		ZoomManager zoom = paths.zoom;

		Point test_point = new Point(x,y);

		Marker mfound = findMarker(test_point, markers);

		System.out.println("markers");
		if (mfound != null)
			return new CustomPoint(mfound);

		else {//System.out.println("paths");
			return findPath(test_point, paths, zoom);}
	}


	private static Marker findMarker(Point test_point, Map<Integer, Marker> markers) {
		for(Map.Entry<Integer, Marker> m : markers.entrySet()) {

			Point m_point = m.getValue().getScaledMarkerPosition();

			if (distance(m_point, test_point) <= MIN_DISTANCE) 
				return m.getValue();
		}
		return null;
	}

	private static CustomPoint findPath(Point test_point, PathArrayList paths, ZoomManager zoom) {

		int distance = Integer.MAX_VALUE;

		CustomPoint out = null;

		
		for (Path p : paths) {

			// verifico prima di tutto la distanza del punto dai capi della path in esame, 
			// se non funziona procedo a scansionare tutta la lunghezza della retta

			Point partenza = p.getScaledP();
			int distanceFromP = distance(partenza, test_point);

			if ((distanceFromP <= MIN_DISTANCE) && (distanceFromP < distance)) {
				out = new CustomPoint(p.A.x, p.A.y, p, zoom);
				distance = distanceFromP;
			}

			Point arrivo = p.getScaledA();
			int distanceFromA = distance(arrivo, test_point);

			if ((distanceFromA <= MIN_DISTANCE) && (distanceFromA < distance)) {
				out = new CustomPoint(p.A.x, p.A.y, p, zoom);
				distance = distanceFromA;
			}

			// se non mi trovo in nessuno dei due casi precedenti, costruisco la retta passante per i
			// due punti della path, e faccio una scansione per vedere se in "qualche punto" mi va bene

			CustomPoint out_t = findNearestLinePoint(p, test_point, zoom);
			System.out.println("OUT_T: "+out_t);
			// cerco la retta con la minore distanza dal punto dato
			if (out_t != null) 
				if (distance(test_point, out_t.getPanelPosition() ) < distance)
					out = out_t;
		}
		System.out.println("new out: "+out);
		if (out == null)
			out = new CustomPoint(test_point.x, test_point.y, zoom);

		return out;
	}


	public static Path findNearestPath(Point test, PathArrayList paths, ZoomManager zoom) {
		CustomPoint cp = findPath(test, paths, zoom);
		return cp.path;
	}

	// costuisco la retta passante per due punti, e se la distanza di un punto di questa retta 
	// da un terzo punto è minore di MIN_DISTANCE, restituisco quel punto
	static CustomPoint findNearestLinePoint(Path path, Point p, ZoomManager zoom) {

		CustomPoint out = null;

		Point P = path.P;//.getScaledP();
		Point A = path.A; //getScaledA();
		
		int[] p_t = zoom.getRealPosition(p.x, p.y);
		p = new Point(p_t[0], p_t[1]);

		int distance = Integer.MAX_VALUE;

		// valori su cui scorrere la funzione
		int values_x = Math.abs(P.x - A.x);
		int values_y = Math.abs(P.y - A.y);

		// la retta è VERTICALE!!!!
		if (values_x == 0) {

			if ((Math.abs(P.x - p.x) <= MIN_DISTANCE / zoom.zoom) && 
					( ((P.y >= p.y) && (A.y <= p.y)) || ((P.y <= p.y) && (A.y >= p.y)) ) ) 
				
				out = new CustomPoint(P.x, p.y, path, zoom);	
			
		}

		else {

			// la retta è una NORMALE FUNZIONE y = Ax + B

			double Ax = (double)(P.y - A.y) / (double)(P.x - A.x);
			double B = (double)(A.y * P.x  -  P.y * A.x) / (double)(P.x - A.x);
			//System.out.println("MIN_DISTANCE:"+(MIN_DISTANCE / zoom.zoom));
			// verifico quale dei due campi è più esteso
			if (values_x >= values_y) {
				//System.out.println("X");
				// In questo caso il campo più esteso è x

				// calcolo il fattore di incremento 
				int incremento = 1;
				if (P.x > A.x)
					incremento = -1;
				//System.out.println("Incremento:"+incremento);
				// scansiono tutta la striscia, nel caso la distanza calcolata aumenti anziché
				// diminuire interrompo il ciclo
				//System.out.println("x:"+P.x+", A.x:"+A.x);
				for (int x = P.x; x != A.x; x += incremento) {
					int y = (int)(x * Ax + B);
					int t_distance = distance (new Point(x,y), p);
					//System.out.println("t_distance:"+t_distance+", distance:"+distance);
					if (t_distance <= distance) {
						distance = t_distance;
						
						if	(distance <= MIN_DISTANCE / zoom.zoom) 
							out = new CustomPoint(x, y, path, zoom);
					}
					else
						break;
				}
			}
			else {
				// In questo caso il campo più esteso è y

				// calcolo il fattore di incremento 
				int incremento = 1;
				if (P.y > A.y)
					incremento = -1;

				// scansiono tutta la striscia, nel caso la distanza calcolata aumenti anziché
				// diminuire interrompo il ciclo

				for (int y = P.y; y != A.y; y += incremento) {
					int x = (int)((y - B) / Ax);
					int t_distance = distance (new Point(x,y), p);

					if (t_distance <= distance) {
						distance = t_distance;
						
						if	(distance <= MIN_DISTANCE / zoom.zoom)
							out = new CustomPoint(x, y, path, zoom);
					}
					else
						break;
				}
			}
		}
		return out;
	}
	
	
	// calcolo la distanza tra due punti
	public static int distance(Point a, Point b) {

		double w = a.x - b.x;
		double h = a.y - b.y;

		double distance = Math.sqrt(w * w + h * h);

		return (int) distance;
	}

	
	// verifico se è presente il marker o la path
	public boolean isMarkerOrPath() {
		return (marker != null) || (path != null);
	}
}