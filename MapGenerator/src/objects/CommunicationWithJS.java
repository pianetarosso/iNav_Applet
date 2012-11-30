package objects;

import java.applet.Applet;
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

	public JSObject window;

	public CommunicationWithJS(Applet applet) {
		this.window = JSObject.getWindow(applet);		
	}


	public Floor[] parseFloors(URL codebase) {

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
		window.call(NEW_MARKER, out);
	}

	// editing di un marker
	public void editMarker(int id) {
		Object[] out = {id};
		window.call(EDIT_MARKER, out);
	}

	// abilito i campi di input (i piani e il tipo) della pagina
	public void enableInput() {
		window.eval(ENABLE_JS_INPUT);
	}

	// aggiorno le coordinate di un marker
	public void updateLocation(int id, int x, int y) {

	}

}
