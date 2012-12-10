package drawable;

import gestore_immagini.ZoomManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
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
	private final Color SELECTED_COLOR = Color.yellow;

	private final int SPESSORE = 2;
	private final int MINIMUM_LENGTH = 10;

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

	// restituisco la lunghezza della path
	public boolean isLengthEnougth() {
		return CustomPoint.distance(P, A) >= MINIMUM_LENGTH / zoom.zoom;
	}

	
	
	// cancello la validazione della path e dei suoi CustomPoint
	public void resetValidation() {
		P.resetValidation();
		A.resetValidation();
		validated = false;
	}

	// Recupero i punti A e P scalati
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
}
