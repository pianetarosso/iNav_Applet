package objects;

public class SimpleMarker {

	private long id_edificio;
	private String RFID;
	private int x;
	private int y;
	private int piano;
	private boolean ingresso;
	private String ascensore;
	private String scala;
	private String stanza;

	public SimpleMarker(long id_edificio, int x, int y, int piano,
			boolean ingresso) {
		this.id_edificio = id_edificio;
		this.x = x;
		this.y = y;
		this.piano = piano;
	}

	public void setRFID(boolean RFID, String RFID_data) {
		if (RFID)
			this.RFID = RFID_data;
		else
			this.RFID = "";
	}

	public void setElevator(boolean elevator, String data) {
		if (elevator)
			ascensore = data;
		else
			ascensore = "";
	}

	public void setStair(boolean stair, String data) {
		if (stair)
			scala = data;
		else
			scala = "";
	}

	public void setRoom(boolean room, String data) {
		if (room)
			stanza = data;
		else
			stanza = "";
	}
}
