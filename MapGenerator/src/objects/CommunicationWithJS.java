package objects;

import java.applet.Applet;
import java.net.MalformedURLException;
import java.net.URL;

import drawable.Marker;


import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class CommunicationWithJS {

	private static final String GET_FLOORS = "getFloors();";
	private static final String NUMERO_DI_PIANO = "numero_di_piano";
	private static final String LINK = "link";
	private static final String BEARING = "bearing";
	private static final String ID = "id";

	private static final String NEW_MARKER = "createNewMarker";
	private static final String EDIT_MARKER = "editMarker";

	private static final String ENABLE_JS_INPUT = "enableInputs()";
	private static final String UPDATE_POSITION = "updatePosition";

	private JSObject window = null;
	
	// funzione che chiamo per abilitare il debug in eclipse
	public boolean debug = true;

	public CommunicationWithJS(Applet applet, boolean debug) {
		if (!debug)
			this.window = JSObject.getWindow(applet);
		this.debug = debug;
	}


	public Floor[] parseFloors(URL codebase) {

		if (debug) {
			Floor[] fts = new Floor[2];
			try {
				fts[0] = new Floor(0, new URL("http://127.0.0.1:8000/media/floors/IMG_20111009_172117_3.jpg"), 0, 23);
				fts[1] = new Floor(1, new URL("http://127.0.0.1:8000/media/floors/IMG_20111009_171138_9.jpg"), 0, 43);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return fts;
		}
		
		
		JSObject jsonFloors = (JSObject) window.eval(GET_FLOORS);

		int i=0;

		for (; i < 50; i++) 
			try {
				jsonFloors.getSlot(i);
			} catch (JSException jse) {
				break;
			}

		Floor[] floors = new Floor[i];

		for (int t=0; t < i; t++) {
			JSObject jsonFloor = (JSObject) jsonFloors.getSlot(t);

			try {
				int numero_di_piano = Integer.parseInt(jsonFloor.getMember(NUMERO_DI_PIANO).toString());
				URL link = new URL(codebase, jsonFloor.getMember(LINK).toString());
				float bearing = Float.parseFloat(jsonFloor.getMember(BEARING).toString());
				int id = Integer.parseInt(jsonFloor.getMember(ID).toString());

				Floor floor = new Floor(numero_di_piano, link, bearing, id);
				floors[t] = floor;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}	

		}
		return floors;
	}


	// creazione di un nuovo marker
	// createNewMarker(id, x, y, piano)
	public void sendNewMarker(Marker new_m, int numero_di_piano) {
		Object[] out = new Integer[] {new_m.id, new_m.x, new_m.y, numero_di_piano};
		if (debug)
			System.out.println("sendNewMarker:"+out);
		else
			window.call(NEW_MARKER, out);
	}

	// editing di un marker
	public void editMarker(int id) {
		Object[] out = new Integer[] {id};
		if (debug)
			System.out.println("editMarker:"+out);
		else
			window.call(EDIT_MARKER, out);
	}

	// abilito i campi di input (i piani e il tipo) della pagina
	public void enableInput() {
		if (debug)
			System.out.println("enableInput:"+true);
		else
			window.eval(ENABLE_JS_INPUT);
	}

	// aggiorno le coordinate di un marker
	public void updateLocation(int id, int x, int y) {
		Object[] out = new Integer[] {id, x, y};
		if (debug)
			System.out.println("updateLocation:"+out);
		else
			window.call(UPDATE_POSITION, out);
	}

}
