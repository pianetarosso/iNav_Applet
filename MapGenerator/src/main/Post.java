package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class Post {

	/**
	 * Reads data from the data reader and posts it to a server via POST
	 * request. data - The data you want to send link - The server's address
	 * output - writes the server's response to output
	 * 
	 * @throws Exception
	 */

	private static InputStream in;
	private static OutputStream out;

	public static void postData(String data, URL link, String cookie)
			throws Exception {

		HttpURLConnection urlc = null;

		urlc = (HttpURLConnection) link.openConnection();

		try {
			urlc.setRequestMethod("POST");
		} catch (ProtocolException e) {
			throw new Exception("NO POST SUPPORT!!!", e);
		}

		urlc.setDoOutput(true);
		urlc.setDoInput(true);
		urlc.setUseCaches(false);
		urlc.setAllowUserInteraction(false);
		urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");

		// imposto il cookie per la verifica di sicurezza
		urlc.setRequestProperty("Cookie", cookie);

		// SCRITTURA
		// ///////////////////////////////////////////////////////////////////////////

		try {
			out = urlc.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(out, "UTF-8");
			wr.write(data);
			wr.close();
			wr.flush();
		} catch (IOException e) {
			throw new Exception("IOException while posting data", e);
		} finally {
			if (out != null)
				out.close();
		}

		// LETTURA
		// ///////////////////////////////////////////////////////////////////////////

		try {
			in = urlc.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = rd.readLine()) != null)
				System.out.println(line);

			rd.close();

		} catch (IOException e) {
			throw new Exception("IOException while reading response", e);
		} finally {
			if (in != null)
				in.close();
		}

		if (urlc != null)
			urlc.disconnect();
	}
}