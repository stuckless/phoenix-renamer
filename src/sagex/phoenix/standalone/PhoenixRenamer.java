package sagex.phoenix.standalone;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sagex.api.Configuration;
import sagex.phoenix.Phoenix;
import sagex.phoenix.metadata.IMetadata;
import sagex.phoenix.metadata.IMetadataSearchResult;
import sagex.phoenix.metadata.MediaType;
import sagex.phoenix.metadata.MetadataManager;
import sagex.phoenix.metadata.MetadataUtil;
import sagex.phoenix.metadata.proxy.SageProperty;
import sagex.phoenix.metadata.search.ScraperUtils;
import sagex.phoenix.metadata.search.SearchQuery;
import sagex.phoenix.metadata.search.SearchQueryFactory;
import sagex.phoenix.util.Hints;
import sagex.phoenix.util.TextReplacement;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.impl.FileMediaFile;
import sagex.phoenix.vfs.impl.FileResourceFactory;
import sagex.phoenix.vfs.util.PathUtils;
import sagex.phoenix.vfs.visitors.MetadataPropertiesExportVisitor;

public class PhoenixRenamer extends PhoenixStandalone {
	private static final long SAMPLE_FILE_SIZE = 1024 * 1024 * 250;
	
	private Properties props = new Properties();
	
	private List<File> files = new ArrayList<File>();
	private boolean renameDirectories = false;
	private boolean renameArtifacts = false;

	private String postExecuteCommand = null;
	private String postExecuteCommandArgs = null;
	
	/**
	 * {@value}
	 */
	public static final String DEFAULT_TV_MASK = "${MediaTitle} - S${SeasonNumber:%02d}E${EpisodeNumber:%02d} - ${EpisodeName}";
	/**
	 * {@value}
	 */
	public static final String DEFAULT_MOVIE_MASK = "${MediaTitle} (${Year})";

	/**
	 * {@value}
	 */
	public static final String PROP_TV_MASK = "phoenix/renamer/tvMask";
	/**
	 * {@value}
	 */
	public static final String PROP_MOVIE_MASK = "phoenix/renamer/movieMask";

	public static void main(String[] args) throws ZipException, IOException {
		PhoenixRenamer renamer = new PhoenixRenamer();

		Options options = new Options();
		options.addOption("?", "help", false, "Help");
		options.addOption(null, "renameDirectories", false,
				"Rename Parent directories of a given video file as well (use with caution)");
		options.addOption(null, "renameArtifacts", false,
				"Rename additional artifacts (ie, properties, sub titles, etc)");
		options.addOption(null, "loglevel", true,
				"Log output level; debug, info, warn");
		
		options.addOption("t", "tvMask", true, "TV Rename mask.  ie, something like '"+DEFAULT_TV_MASK+"'");
		options.addOption("m", "movieMask", true, "Movie Rename mask.  ie, something like '"+DEFAULT_MOVIE_MASK+"'");
		options.addOption("c", "cmd", true, "Execute this command when the rename is complete.  The only parameter passed to the command will be the new filename to which the content was renamed.  The ENVIRONMENT will contain all the metadata properties prefixed by PHOENIX_, e.g., MediaTitle will be PHOENIX_MEDIATITLE.  Please note the command must be a fully qualified path to the command being executed.");
		options.addOption(null, "cmd-args", true, "These args gets passed as the second arg to the --cmd");
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine cl = parser.parse(options, args);

			if (cl.hasOption("help")) {
				printHelp(options, null);
				return;
			}

			if (cl.getArgList().size() == 0) {
				printHelp(options, null);
				return;
			} else {
				for (Object s : cl.getArgList()) {
					if (s instanceof File) {
						renamer.files.add((File) s);
					} else if (s instanceof String) {
						renamer.files.add(new File((String) s));
					}
				}
			}

			if (cl.hasOption("renameDirectories")) {
				renamer.renameDirectories = true;
			}

			if (cl.hasOption("renameArtifacts")) {
				renamer.renameArtifacts = true;
			}
			
			if (cl.hasOption("tvMask")) {
				renamer.props.put(PROP_TV_MASK, cl.getOptionValue("tvMask"));
			}

			if (cl.hasOption("movieMask")) {
				renamer.props.put(PROP_MOVIE_MASK, cl.getOptionValue("movieMask"));
			}

			if (cl.hasOption("c")) {
				renamer.postExecuteCommand = cl.getOptionValue("c");
			}

			if (cl.hasOption("cmd-args")) {
				renamer.postExecuteCommandArgs = cl.getOptionValue("cmd-args");
			}
			
			String level = cl.getOptionValue("loglevel", "info");
			if ("warn".equalsIgnoreCase(level)) {
				logLevel = Level.WARN;
			} else if ("debug".equalsIgnoreCase(level)) {
				logLevel = Level.DEBUG;
			} else {
				logLevel = Level.INFO;
			}
			
		} catch (Exception e) {
			printHelp(options, e);
			return;
		}

		renamer.init();
		renamer.run();
	}

	private void run() {
		for (File f : files) {
			if (!f.exists()) {
				log.debug("Invalid file: " + f);
				continue;
			}

			IMediaResource res = FileResourceFactory.createResource(f);
			processResource(res);
		}
	}

	public void processResource(IMediaResource res) {
		if (res instanceof IMediaFolder) {
			for (IMediaResource r : (IMediaFolder) res) {
				processResource(r);
			}
		} else {
			processMediaFile((IMediaFile) res);
		}
	}

	public void processMediaFile(IMediaFile res) {

		File file = PathUtils.getFirstFile(res);
		if (FileResourceFactory.isVideoFile(file)
				|| FileResourceFactory.isBluRay(file)
				|| FileResourceFactory.isDVD(file)) {
			log.debug("Processing " + res);
			try {
				SearchQueryFactory qf = new SearchQueryFactory();
				Hints hints = Phoenix.getInstance().getMetadataManager()
						.getDefaultMetadataOptions();
				SearchQuery q = qf.createQueryFromFilename(res, hints);

				MetadataManager mm = Phoenix.getInstance().getMetadataManager();

				List<IMetadataSearchResult> results = null;
				IMetadata md = null;
				try {
					results = mm.search(q);
					md = mm.getMetdata(results, q);
				} catch (Exception e) {
					if (md == null) {
						FileMediaFile fmf = (FileMediaFile) FileResourceFactory.createResource(PathUtils.getFirstFile(res));
						log.debug("Attempting to use folder name as the file name for " + fmf.getTitle());
						fmf.getFiles().clear();
						fmf.addFile(PathUtils.getFirstFile(res).getParentFile());
						
						q = qf.createQueryFromFilename(fmf, hints);
						mm = Phoenix.getInstance().getMetadataManager();
						results = mm.search(q);
						md = mm.getMetdata(results, q);
					}
				}

				// TODO: This should be handled by the metadata manager
				int disc = ScraperUtils.parseCD(PathUtils.getFirstFile(res)
						.getAbsolutePath());
				if (disc > 0) {
					md.setDiscNumber(disc);
				}

				rename(res, md);
			} catch (Exception e) {
				log.warn("Cannot rename " + res.getTitle() + " since there is no metadata");
				Logger.getRootLogger().warn("failed to rename " + res, e);
			}
		}
	}

	private String fixName(String in) {
		if (in == null)
			return "";
		return in.replaceAll("[\\\\/:\\*?\"<>|]+", "");
	}

	private String getMovieName(IMediaFile res, IMetadata md, boolean withCD)
			throws Exception {
		String title = fixName(md.getMediaTitle());
		if (StringUtils.isEmpty(title)) {
			throw new Exception("No valid title for renaming.");
		}

		Map<String, String> rep = new HashMap<String, String>();
		rep.put("MediaTitle", title);
		rep.put("Title", title);
		if (md.getYear()>1900) {
			rep.put("Year", String.valueOf(md.getYear()));
		}
		
		String mask = props.getProperty(PROP_MOVIE_MASK, Configuration.GetProperty(PROP_MOVIE_MASK, DEFAULT_MOVIE_MASK));
		String name = TextReplacement.replaceVariables(mask, rep);

		name = name.replace("()", "");
		name = name.trim();

		if (withCD && md.getDiscNumber() > 0) {
			name += (" cd" + String.valueOf(md.getDiscNumber()));
		}

		if (PathUtils.getExtension(res) != null) {
			name +=("." + PathUtils.getExtension(res));
		}

		return name;
	}

	private String getTVName(IMediaFile res, IMetadata md) throws Exception {
		Map<String, String> rep = new HashMap<String, String>();
		String title = fixName(md.getMediaTitle());
		if (StringUtils.isEmpty(title)) {
			throw new Exception("No valid title for renaming.");
		}

		if (md.getEpisodeNumber() == 0) {
			throw new Exception(
					"Could not locate season/episode information for file");
		}

		rep.put("MediaTitle", title);
		rep.put("Title", title);
		rep.put("SeasonNumber", String.valueOf(md.getSeasonNumber()));
		rep.put("EpisodeNumber", String.valueOf(md.getEpisodeNumber()));
		rep.put("EpisodeName", fixName(md.getEpisodeName()));
		
		String mask = props.getProperty(PROP_TV_MASK, Configuration.GetProperty(PROP_TV_MASK, DEFAULT_TV_MASK));
		String name = TextReplacement.replaceVariables(mask, rep);
		if (name.trim().endsWith("-")) {
			name = StringUtils.chop(name);
			name = name.trim();
		}

		if (PathUtils.getExtension(res) != null) {
			name += ("." + PathUtils.getExtension(res));
		}

		return name;
	}

	private static void printHelp(Options options, Throwable t) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(
				"phoenix-renamer [OPTIONS] directory|file",
				options);
		if (t != null) {
			System.out.println("ERROR: " + t.getMessage());
			t.printStackTrace(System.err);
		}
	}

	private void rename(IMediaFile res, IMetadata md) throws Exception {
		String newName = null;
		String dirName = null;

		if (MediaType.toMediaType(md.getMediaType()) == MediaType.TV) {
			newName = getTVName(res, md);
			dirName = md.getMediaTitle();
		} else if (MediaType.toMediaType(md.getMediaType()) == MediaType.MOVIE) {
			newName = getMovieName(res, md, true);
			dirName = FilenameUtils.getBaseName(getMovieName(res, md, false));
		} else {
			throw new Exception("Can't rename media type " + md.getMediaType());
		}

		log.info("Renaming " + res.getTitle() + " to " + newName);
		File file = PathUtils.getFirstFile(res);

		file = resolveFile(file, dirName);
		
		File origFile = new File(file.getAbsolutePath());

		// check for bluray and dvd folders
		if (file.isDirectory() && file.getName().equalsIgnoreCase("bdmv")) {
			file = file.getParentFile();
		} else if (file.isDirectory()
				&& file.getName().equalsIgnoreCase("video_ts")) {
			file = file.getParentFile();
		}

		if (isSampleFile(res)) {
			String name = FilenameUtils.getBaseName(newName);
			String ext = FilenameUtils.getExtension(newName);
			newName = name + " - Sample." + ext;
		}
		
		File newFile = new File(file.getParentFile(), newName);
		if (newFile.exists()) {
			log.info(newName + " already exists.");
		} else {
			if (!file.renameTo(newFile)) {
				log.warn("Rename failed for " + newName);
				return;
			}
			
			log.debug("Renamed Sucessful for: " + newName);
			Logger.getRootLogger().info("Renamed " + origFile + " to " + newFile);
		}
		
		file = newFile;
		
		if (renameArtifacts) {
			log.debug("Renaming Artifacts for file " + origFile);
			final File oldFile = file;
			final File newLocFile = file.getParentFile();
			final String baseName = FilenameUtils.getBaseName(origFile.getName());
			final String newBaseName = FilenameUtils.getBaseName(file.getName());
			final String baseDirName = dirName;
			origFile.getParentFile().listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (file.getName().startsWith(baseName + ".")) {
						try {
							file = resolveFile(file, baseDirName);
							if (file.equals(oldFile)) {
								// we already renamed this file
								return false;
							}
							
							// rename this file
							File renTo = new File(newLocFile, newBaseName + file.getName().substring(baseName.length()));
							file.renameTo(renTo);
							log.info("Renamed " + file + " to " + renTo );
							Logger.getRootLogger().info("Renamed " + file + " to " + renTo );
						} catch (Exception e) {
							log.warn("Could not rename file " + file);
							Logger.getRootLogger().warn("Rename failed for " + file, e);
						}
					}
					return false;
				}
			});
		}
		
		if (renameDirectories) {
			log.debug("Renaming directories for " + origFile);
			try {
				File parent = file.getParentFile();
				String newParentName = dirName;

				if (!parent.getName().equals(newParentName)) {
					File dest = new File(parent.getParentFile(), newParentName);
					if (dest.exists()) {
						Logger.getRootLogger().info("Have to move the directory contents, since the directory we are renaming to already exists");
						// move all children to the new directory
						for (File f : parent.listFiles()) {
							try {
								log.debug("Moving " + f + " to dir " + dest);
								Logger.getRootLogger().info("Moving " + f + " to dir " + dest);
								FileUtils.moveToDirectory(f, dest, false);
							} catch (Exception e) {
								log.warn("Failed to move file: " + f);
								Logger.getRootLogger().warn("Failed to move file: " + f, e);
							}
						}
						if (!parent.delete()) {
							log.warn("Unable to remove directory: "	+ parent);
							Logger.getRootLogger().warn("Unable to remove directory: "	+ parent);
						}
					} else {
						if (parent.renameTo(dest)) {
							log.info("Renamed parent's directory to " + newParentName);
							Logger.getRootLogger().info("Renamed parent's directory to " + newParentName);
						} else {
							log.warn("Unable to rename direcotry");
							Logger.getRootLogger().warn("Unable to rename direcotry");
						}
					}
				}
			} catch (Exception e) {
				log.warn("Unable to rename directory for: " + file);
				Logger.getRootLogger().warn("Unable to rename directory for: " + file, e);
			}
		}
		
		// notify rename worked
		if (postExecuteCommand!=null) {
			log.info("Executing Command '"+postExecuteCommand +" \""+ newFile.getAbsolutePath() +"\"'");
			
			List<String> cmd = new ArrayList<String>();
			cmd.add(postExecuteCommand);
			cmd.add(newFile.getCanonicalPath());
			if (postExecuteCommandArgs!=null) {
				cmd.add(postExecuteCommandArgs);
			}
			ProcessBuilder pb = new ProcessBuilder(cmd);
			// set the environment
			Map<String, String> map = new HashMap<String, String>();
			IMetadata newMD = MetadataUtil.createMetadata(map);
			MetadataUtil.copyMetadata(md, newMD);

			Map env = pb.environment();
			for (Map.Entry<String, String> me: map.entrySet()) {
				env.put("PHOENIX_" + me.getKey().toUpperCase(), me.getValue());
			}
			
			pb.redirectErrorStream();
			
			try {
				Process p = pb.start();
				StreamGobbler gobble = new StreamGobbler(p.getInputStream());
				gobble.start();
				gobble.join();
			} catch (Throwable t) {
				log.error("Failed to start process: " + postExecuteCommand + "; " + t.getMessage());
			}
		}
	}

	private boolean isSampleFile(IMediaFile res) {
		File file = PathUtils.getFirstFile(res);
		if (file.getName().toLowerCase().contains("sample") && file.length() < SAMPLE_FILE_SIZE) {
			return true;
		}
		return false;
	}
	
	private File resolveFile(File file, String dirName) throws Exception {
		if (!file.exists()) {
			// this is most likely becuase it's parent file has been renamed,
			// so lets reparent the file and try again.
			File nFile = new File(file.getParentFile().getParentFile(), dirName);
			nFile = new File(nFile, file.getName());
			if (!nFile.exists()) {
				throw new Exception("Can't rename non-existent file: " + file);
			} else {
				file = nFile;
			}
		}
		return file;
	}
}
