package sagex.phoenix.standalone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import sagex.SageAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.util.FileUtils;
import sagex.stub.StubSageAPI;
import sagex.util.Log4jConfigurator;

public class PhoenixStandalone {
	protected static Logger log = null;
	protected static Level logLevel = Level.INFO;
	
	/**
	 * @param args
	 * @throws Exception 
	 * @throws ZipException 
	 */
	public static void main(String[] args) throws ZipException, Exception {
		PhoenixStandalone ps = new PhoenixStandalone();
		ps.init();
	}

	public void init() throws ZipException, IOException {
		// configure log4j for the standalone console output
		log = Logger.getLogger(this.getClass());
		log.addAppender(
				new ConsoleAppender(new SimpleLayout(),
						ConsoleAppender.SYSTEM_OUT));
		log.setLevel(logLevel);
		
		String phoenixHome  = System.getProperty("phoenix/standaloneHomeDir", System.getProperty("launch4j.exedir",System.getenv("EXEDIR")));
		if (StringUtils.isEmpty(phoenixHome)) {
			phoenixHome = System.getProperty("user.home");
		}
		log.info("Phoenix Home: " + phoenixHome);
		File home = new File(phoenixHome);
		File phoenix = new File(home, System.getProperty("phoenix/standaloneDirName","PhoenixHome"));
		if (!phoenix.exists()) {
			phoenix.mkdirs();
		}

		// set the main log4j log file
		File logfile = new File(phoenix, "renamer.log");
		if (logfile.exists()) {
			logfile.delete();
		}
		System.setProperty("sagex.log4j.logfile", logfile.getAbsolutePath());
		
		File sagePhoenix = new File(phoenix, "sagetv");

		PrintStream sysout = System.out;
		PrintStream ps = new PrintStream(new File(phoenix, "systemout.log"));
		System.setOut(ps);
		
		System.setProperty("phoenix/standalone", "true");
		System.setProperty("phoenix/sagetvHomeDir",
				sagePhoenix.getAbsolutePath());

		
		Log4jConfigurator.configureQuietly("phoenix-standalone");

		String version = "2.5.6";
		String oldVersion = null;
		File f = new File(sagePhoenix, ".version");
		try {
			if (f.exists()) {
				oldVersion = org.apache.commons.io.FileUtils.readFileToString(f);
			}
			
		} catch (Exception e) {
		}
		
		if (oldVersion == null || !version.equals(oldVersion)) {
			log.info("Extracting Phoenix configuration and runtime files...");
			extractStream(this.getClass()
				.getClassLoader().getResourceAsStream("phoenix-core-"+version+".zip"), sagePhoenix);
			org.apache.commons.io.FileUtils.writeStringToFile(f, version);
		}
		
		File propFile = new File(phoenix, "standalone.properties");
		StubSageAPI api = new StubSageAPI();
		PropertiesStubAPIProxy propApi = new PropertiesStubAPIProxy(propFile);
		propApi.attach(api);
		
	    SageAPI.setProvider(api);
	    
	    Phoenix.getInstance();
	    ps.flush();
	    ps.close();
	    System.setOut(sysout);
	    log.info("Phoenix Standalone is online and ready for commands");
	}

	public void extractStream(InputStream zipFile, File destDir)
			throws ZipException, IOException {
		destDir.mkdirs();

		ZipInputStream zis = new ZipInputStream(zipFile);

		// Process each entry
		ZipEntry entry = null;
		while ((entry = zis.getNextEntry()) != null) {
			File destFile = new File(destDir, entry.getName());
			if (entry.isDirectory()) {
				destFile.mkdirs();
			} else {
				log.debug("Extracting: " + entry.getName());
				if (destFile.getParentFile() != null)
					destFile.getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(destFile);
				IOUtils.copy(zis, fos);
				fos.flush();
				fos.close();
			}
			zis.closeEntry();
		}
	}
}
