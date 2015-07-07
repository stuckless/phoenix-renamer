package sagex.phoenix.standalone;

import java.io.File;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import sagex.phoenix.vfs.impl.FileResourceFactory;

public class WatchedFile implements WatchDir.NewFileHandler, Runnable {
	private Logger log = PhoenixStandalone.log;
	private PhoenixRenamer renamer;
	private File file;
	private long lastSize=0;
	private boolean cancelled=false;
	
	public WatchedFile(PhoenixRenamer renamer) {
		this.renamer=renamer;
	}
	
	@Override
	public void onCreate(Path file) {
		this.file=file.toFile();
		
		if (renamer.accepts(this.file)) {		
			Thread thread = new Thread(this);
			thread.start();
		} else {
			// not a new video file, so we don't care
		}
	}

	@Override
	public void run() {
		log.info("Watching File " + file + " until it settles down.");
		while(!cancelled) {
			if (lastSize==0 || file.length()==0 || file.length()!=lastSize) {
				try {
					log.debug("File is still changing. WIll wait longer for " + file);
					lastSize=file.length();
					Thread.sleep(renamer.watchSeconds*1000);
					continue;
				} catch (InterruptedException e) {
					Thread.interrupted();
					break;
				}
			}
			
			log.info("File " + file + " has settled.  It will be renamed.");
			renamer.processResource(FileResourceFactory.createResource(file));
			break;
		}
	}
}
