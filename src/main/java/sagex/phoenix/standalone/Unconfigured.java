package sagex.phoenix.standalone;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

public class Unconfigured {
	public static void main(String args[]) throws URISyntaxException {
		System.out.println("It appears that you are running the tools without providing a toolname.  If you are running from windows, then be sure to the .exe, but if you are running the .jar directly, then be sure to pass -DMainClass=YOUR_TOOL_CLASS to the jar -jar launcher.");
		
		 CodeSource codeSource = Unconfigured.class.getProtectionDomain().getCodeSource();
		    File jarFile = new File(codeSource.getLocation().toURI());
		    String jarDir = jarFile.getParentFile().getPath();
		System.out.println(jarDir);
	}
}
