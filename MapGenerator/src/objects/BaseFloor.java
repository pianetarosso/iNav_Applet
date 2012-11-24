package objects;


public class BaseFloor {

	public int numero_di_piano;
	public String link;
	public String bearing;

	/*
	 * public static Floor[] parse(URL url, String cookie) throws IOException {
	 * 
	 * URLConnection connection = url.openConnection();
	 * connection.setRequestProperty("Cookie", cookie);
	 * 
	 * String line; StringBuilder builder = new StringBuilder();
	 * 
	 * BufferedReader reader = new BufferedReader(new
	 * InputStreamReader(connection.getInputStream()));
	 * 
	 * while((line = reader.readLine()) != null) builder.append(line);
	 * 
	 * return Floor.parse(builder.toString()); }
	 */
	
	
	public float getBearing() {
		return Float.parseFloat(bearing);
	}

	public int getFloor() {
		return numero_di_piano;
	}
	
	public String getLink() {
		return link;
	}

	public String toString() {
		return numero_di_piano + " " + bearing + " " + link;
	}
}
