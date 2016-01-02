package sagex.phoenix.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
	InputStream is;
	String type;

	StreamGobbler(InputStream is) {
		this.is = is;
		this.type = "OUTPUT";
	}

	StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
				handleLine(line);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void handleLine(String line) {
		System.out.println(type + ">" + line);
	}
}