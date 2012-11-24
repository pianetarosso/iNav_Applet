package drawable;

import java.util.ArrayList;

public class MarkerArrayList extends ArrayList<Marker> {

	private static final long serialVersionUID = -993082393336743642L;

	private ArrayList<String> RFID; // contenitore generale rfid
	private ArrayList<ArrayList<String>> floor_elevators; // contenitore (diviso
															// per piano) degli
															// ascensori
	private ArrayList<ArrayList<String>> floor_stairs; // contenitore (diviso
														// per piano) delle
														// scale

	// mappa tra i piani e la posizione nelle arraylist
	// Posizione 0 => numero di piano
	// Posizione 1 => numero di riferimento nell'arraylist
	private ArrayList<int[]> map_elevators;
	private ArrayList<int[]> map_stairs;

	// OVERRIDE
	// //////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	// costruttore
	public MarkerArrayList() {
		super();
		floor_elevators = new ArrayList<ArrayList<String>>();
		floor_stairs = new ArrayList<ArrayList<String>>();
		RFID = new ArrayList<String>();

		map_elevators = new ArrayList<int[]>();
		map_stairs = new ArrayList<int[]>();
	}

	// add
	@Override
	public boolean add(Marker m) {

		if (m.RFID)
			RFID.add(m.RFID_data);

		if (m.elevator)
			addNewElement(m.floor, map_elevators, floor_elevators,
					m.generic_data);
		else if (m.stair)
			addNewElement(m.floor, map_stairs, floor_stairs, m.generic_data);

		return super.add(m);
	}

	// remove
	@Override
	public boolean remove(Object arg0) {

		Marker m = (Marker) arg0;

		if (m.RFID)
			RFID.remove(m.RFID_data);

		if (m.elevator)
			removeElement(m.floor, map_elevators, m.generic_data,
					floor_elevators);
		else if (m.stair)
			removeElement(m.floor, map_stairs, m.generic_data, floor_stairs);

		return super.remove(arg0);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	// FUNZIONE PER RESTITUIRE GLI ARRAY
	// /////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	// funzione per recuperare :
	// 1) array di ascensori di QUESTO piano
	// 2) array di scale di QUESTO piano
	// 3) array degli ascensori di ALTRI piani
	// 4) array di scale di ALTRI piani
	// 5) array TOTALE di RFID
	public Object[] getTestParameters(int floor) {

		// Posizione 0 => ascensori e scale QUESTO piano
		// Posizione 1 => tutto il resto
		Object[] output = new Object[2];

		// elementi di QUESTO piano
		// Posizione 0 => ascensori
		// Posizione 1 => scale
		Object[] this_floor = new Object[2];

		int index_elevators = getFloor(floor, map_elevators);
		int index_stairs = getFloor(floor, map_stairs);
		String[] elevators = new String[0], stairs = new String[0];

		if (index_elevators > -1) {
			ArrayList<String> elevators_this_floor = floor_elevators
					.get(index_elevators);
			elevators = convertArrayListToArrayString(elevators_this_floor);
		}

		this_floor[0] = elevators;

		if (index_stairs > -1) {
			ArrayList<String> stairs_this_floor = floor_stairs
					.get(index_stairs);
			stairs = convertArrayListToArrayString(stairs_this_floor);
		}

		this_floor[1] = stairs;

		output[0] = this_floor;

		// elementi degli ALTRI piani
		// Posizione 0 => RFID (globali)
		// Posizione 1 => ascensori
		// Posizione 2 => scale
		Object[] other_floors = new Object[3];

		// aggiungo gli RFID
		other_floors[0] = convertArrayListToArrayString(RFID);

		// creo il (nuovo) arraylist globale degli ascensori
		ArrayList<String> elevators_other_floors = new ArrayList<String>();
		for (int i = 0; i < floor_elevators.size(); i++) {

			if (i != index_elevators) {
				ArrayList<String> e = floor_elevators.get(i);
				elevators_other_floors.addAll(e);
			}
		}

		// aggiungo l'array al contenitore
		other_floors[1] = convertArrayListToArrayString(elevators_other_floors);

		// creo il (nuovo) arraylist globale delle scale
		ArrayList<String> stairs_other_floors = new ArrayList<String>();
		for (int i = 0; i < floor_stairs.size(); i++) {

			if (i != index_stairs) {
				ArrayList<String> e = floor_stairs.get(i);
				stairs_other_floors.addAll(e);
			}
		}

		// aggiungo l'array al contenitore
		other_floors[2] = convertArrayListToArrayString(stairs_other_floors);

		// unisco tutto nell'output
		output[1] = other_floors;

		return output;
	}

	private String[] convertArrayListToArrayString(ArrayList<String> input) {

		String[] output = new String[input.size()];
		for (int i = 0; i < input.size(); i++)
			output[i] = input.get(i);
		return output;
	}

	// AGGIUNGI / RIMUOVI OGGETTI
	// ////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	private void removeElement(int floor, ArrayList<int[]> map, String data,
			ArrayList<ArrayList<String>> container) {

		int position = getFloor(floor, map);

		ArrayList<String> object = container.get(position);
		object.remove(data);

		if (object.size() > 0)
			container.set(position, object);
		else {
			container.remove(position);
			removeFloor(position, map);
		}
	}

	// funzione per aggiungere nuovi elementi (usata nell'ADD)
	private void addNewElement(int m_floor, ArrayList<int[]> map,
			ArrayList<ArrayList<String>> container, String generic_data) {

		// recupero la posizione del piano nel container
		int position = getFloor(m_floor, map);

		// caso in cui compaia un nuovo piano
		if (position < 0) {
			setFloor(m_floor, container.size(), map);
			ArrayList<String> new_ = new ArrayList<String>();
			new_.add(generic_data);
			container.add(new_);
		}

		// aggiunta ad un piano già esistente
		else {
			ArrayList<String> current_floor = container.get(position);
			current_floor.add(generic_data);
			container.set(position, current_floor);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	// GESTIONE DELLA MAPPA TRA PIANO E OGGETTI
	// ///////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	// gestione della mappa dei piani
	private int getFloor(int floor, ArrayList<int[]> map) {

		for (int[] i : map) {
			if (i[0] == floor)
				return i[1];
		}
		return -1;
	}

	private void setFloor(int floor, int position, ArrayList<int[]> map) {
		map.add(new int[] { floor, position });
	}

	private void removeFloor(int index, ArrayList<int[]> map) {

		map.remove(index);

		for (int i = 0; i < map.size(); i++) {
			int[] temp = map.get(i);

			if (temp[1] > index) {
				temp[1] = temp[1] - 1;
				map.set(i, temp);
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////

	// IMPOSTA COME VALIDI E VISIBILI I MARKER DEL PIANO
	// /////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////
	public void setVisibleMarkers(int floor) {

		for (Marker i : this) {
			System.out.println("" + i.RFID_data + " " + i.floor);
			i.setVisible(i.floor == floor);
			i.setEnabled(i.floor == floor);
			i.repaint();
		}
	}

}
