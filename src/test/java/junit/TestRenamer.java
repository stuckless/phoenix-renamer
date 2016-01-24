package junit;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sagex.phoenix.standalone.PhoenixRenamer;


public class TestRenamer {

	@Before
	public void setUp() throws Exception {
		new File("build/test/junit").mkdirs();
		System.setProperty("phoenix/standaloneHomeDir", "build/test/junit");
	}

	@After
	public void tearDown() throws Exception {
	}

	public File makeFile(File parent, String file) throws IOException {
		File f1 = new File(parent, file);
		f1.getParentFile().mkdirs();
		f1.createNewFile();
		return f1;
	}

	@Test
	public void testWithOutRenameDirectories() throws IOException {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "Revenge S01E02 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E02.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv");
		File f2 = makeFile(downloads, "Paranormal Activity 3 2011 UNRATED BluRay 1080p DTS x264 CHD/paranormal.activity.3.2011.bluray.1080p.dts.x264-chd.mkv");
		File f3 = makeFile(downloads, "Revenge S01E03 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E03.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv");
		File f4 = makeFile(downloads, "Salt/Salt (2010)/BDMV/file.m2ts");
		File f5 = makeFile(downloads, "Pulp Fiction/Pulp Fiction/VIDEO_TS/file.vob");
		File f6 = makeFile(downloads, "Valentines Day/Valentine's Day cd1.mkv");
		File f7 = makeFile(downloads, "Valentines Day/Valentine's Day cd2.mkv");
		
		PhoenixRenamer.main(new String[] {downloads.getAbsolutePath()});
		
		assertFalse("TV Failed", f1.exists());
		assertFalse("Movie Failed", f2.exists());
		assertFalse("TV Same Name Failed", f3.exists());
		assertFalse("BluRay Failed", f4.exists());
		assertFalse("DVD Failed", f5.exists());
		assertFalse("CD 1 Failed", f6.exists());
		assertFalse("CD 2 Failed", f7.exists());
		
		assertTrue(new File(downloads, "Revenge S01E02 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge - S01E02 - Trust.mkv").exists());
		assertTrue(new File(downloads, "Revenge S01E03 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge - S01E03 - Betrayal.mkv").exists());
		assertTrue(new File(downloads, "Paranormal Activity 3 2011 UNRATED BluRay 1080p DTS x264 CHD/Paranormal Activity 3 (2011).mkv").exists());
		assertTrue(new File(downloads, "Pulp Fiction/Pulp Fiction (1994)/VIDEO_TS/file.vob").exists());
		assertTrue(new File(downloads, "Salt/Salt (2010)/BDMV/file.m2ts").exists());
		assertTrue(new File(downloads, "Valentines Day/Valentine's Day (2010) cd1.mkv").exists());
		assertTrue(new File(downloads, "Valentines Day/Valentine's Day (2010) cd2.mkv").exists());
	}
	
	@Test
	public void testWithRenameDirectories() throws IOException {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "Revenge S01E02 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E02.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv");
		File f2 = makeFile(downloads, "Paranormal Activity 3 2011 UNRATED BluRay 1080p DTS x264 CHD/paranormal.activity.3.2011.bluray.1080p.dts.x264-chd.mkv");
		File f3 = makeFile(downloads, "Revenge S01E03 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E03.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv");
		File f4 = makeFile(downloads, "Salt/Salt/BDMV/file.m2ts");
		File f5 = makeFile(downloads, "Pulp Fiction/Pulp Fiction/VIDEO_TS/file.vob");
		File f6 = makeFile(downloads, "Valentines Day/Valentine's Day cd1.mkv");
		File f7 = makeFile(downloads, "Valentines Day/Valentine's Day cd2.mkv");
		
		PhoenixRenamer.main(new String[] {"--renameDirectories", downloads.getAbsolutePath()});
		
		assertFalse("TV Failed", f1.exists());
		assertFalse("Movie Failed", f2.exists());
		assertFalse("TV Same Name Failed", f3.exists());
		assertFalse("BluRay Failed", f4.exists());
		assertFalse("DVD Failed", f5.exists());
		assertFalse("CD 1 Failed", f6.exists());
		assertFalse("CD 2 Failed", f7.exists());
		
		assertTrue(new File(downloads, "Revenge/Revenge - S01E02 - Trust.mkv").exists());
		assertTrue(new File(downloads, "Revenge/Revenge - S01E03 - Betrayal.mkv").exists());
		assertTrue(new File(downloads, "Paranormal Activity 3 (2011)/Paranormal Activity 3 (2011).mkv").exists());
		assertTrue(new File(downloads, "Pulp Fiction (1994)/Pulp Fiction (1994)/VIDEO_TS/file.vob").exists());
		assertTrue(new File(downloads, "Salt (2010)/Salt (2010)/BDMV/file.m2ts").exists());
		assertTrue(new File(downloads, "Valentine's Day (2010)/Valentine's Day (2010) cd1.mkv").exists());
		assertTrue(new File(downloads, "Valentine's Day (2010)/Valentine's Day (2010) cd2.mkv").exists());
	}
	
	@Test
	public void testWithRenameDirectoriesWithMultipleNoVideoFiles() throws IOException {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "Revenge S01E02 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E02.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv");
		File f2 = makeFile(downloads, "Revenge S01E02 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E02.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv.properties");
		File f3 = makeFile(downloads, "Revenge S01E02 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E02.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv.srt");
		File f4 = makeFile(downloads, "Revenge S01E02 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E02.Trust.720p.WEB-DL.DD5.1.H.264-TB.nfo");
		File f5 = makeFile(downloads, "Revenge S01E03 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E03.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv");
		File f6 = makeFile(downloads, "Revenge S01E03 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E03.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv.properties");
		File f7 = makeFile(downloads, "Revenge S01E03 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E03.Trust.720p.WEB-DL.DD5.1.H.264-TB.mkv.srt");
		File f8 = makeFile(downloads, "Revenge S01E03 720p WEB DL DD5 1 H 264 TrollololBlue/Revenge.S01E03.Trust.720p.WEB-DL.DD5.1.H.264-TB.nfo");
		
		PhoenixRenamer.main(new String[]{"--renameDirectories", "--renameArtifacts", downloads.getAbsolutePath()});
		
		assertTrue(new File(downloads, "Revenge/Revenge - S01E02 - Trust.mkv").exists());
		assertTrue(new File(downloads, "Revenge/Revenge - S01E02 - Trust.mkv.properties").exists());
		assertTrue(new File(downloads, "Revenge/Revenge - S01E02 - Trust.mkv.srt").exists());
		assertTrue(new File(downloads, "Revenge/Revenge - S01E02 - Trust.nfo").exists());
		assertTrue(new File(downloads, "Revenge/Revenge - S01E03 - Betrayal.mkv").exists());
		assertTrue(new File(downloads, "Revenge/Revenge - S01E03 - Betrayal.mkv.properties").exists());
		assertTrue(new File(downloads, "Revenge/Revenge - S01E03 - Betrayal.mkv.srt").exists());
		assertTrue(new File(downloads, "Revenge/Revenge - S01E03 - Betrayal.nfo").exists());
		assertFalse(new File(downloads, "Revenge S01E02 720p WEB DL DD5 1 H 264 TrollololBlue").exists());
		assertFalse(new File(downloads, "Revenge S01E03 720p WEB DL DD5 1 H 264 TrollololBlue").exists());
	}

	@Test
	public void testWithRenameSamples() throws IOException {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "Salt/Salt.mkv");
		File f2 = makeFile(downloads, "Salt/Salt.mkv.properties");
		File f3 = makeFile(downloads, "Salt/Salt.sample.mkv");
		File f4 = makeFile(downloads, "Salt/Salt.sample.mkv.properties");
		File f5 = makeFile(downloads, "Paranormal Activity 3 2011 UNRATED BluRay 1080p DTS x264 CHD/paranormal.activity.3.2011.bluray.1080p.dts.x264-chd.mkv");
		File f6 = makeFile(downloads, "Paranormal Activity 3 2011 UNRATED BluRay 1080p DTS x264 CHD/paranormal.activity.3.2011.bluray.1080p.dts.x264-chd.mkv.properties");
		File f7 = makeFile(downloads, "Paranormal Activity 3 2011 UNRATED BluRay 1080p DTS x264 CHD/paranormal.activity.3.2011.bluray.1080p.dts.x264.sample-chd.mkv");
		File f8 = makeFile(downloads, "Paranormal Activity 3 2011 UNRATED BluRay 1080p DTS x264 CHD/paranormal.activity.3.2011.bluray.1080p.dts.x264.sample-chd.mkv.properties");
		
		PhoenixRenamer.main(new String[]{"--renameDirectories", "--renameArtifacts", downloads.getAbsolutePath()});
		
		assertTrue(new File(downloads, "Salt (2010)/Salt (2010).mkv").exists());
		assertTrue(new File(downloads, "Salt (2010)/Salt (2010).mkv.properties").exists());
		assertTrue(new File(downloads, "Salt (2010)/Salt (2010) - Sample.mkv").exists());
		assertTrue(new File(downloads, "Salt (2010)/Salt (2010) - Sample.mkv.properties").exists());
		
		assertTrue(new File(downloads, "Paranormal Activity 3 (2011)/Paranormal Activity 3 (2011).mkv").exists());
		assertTrue(new File(downloads, "Paranormal Activity 3 (2011)/Paranormal Activity 3 (2011).mkv.properties").exists());
		assertTrue(new File(downloads, "Paranormal Activity 3 (2011)/Paranormal Activity 3 (2011) - Sample.mkv").exists());
		assertTrue(new File(downloads, "Paranormal Activity 3 (2011)/Paranormal Activity 3 (2011) - Sample.mkv.properties").exists());
	}
	
	@Test
	public void testWithFolderLookup() throws Exception {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "Footloose 2011 1080p BluRay x264 RRH/rrh-footloose.mkv");
		PhoenixRenamer.main(new String[] {"--renameDirectories", "--renameArtifacts", "--loglevel", "info", downloads.getAbsolutePath()});

		assertTrue(new File(downloads, "Footloose (2011)/Footloose (2011).mkv").exists());
	}
	
	@Test
	public void testOddFiles() throws Exception {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "J Edgar 2011 MULTi 1080p BluRay x264 LOST/J Edgar 2011 MULTi 1080p BluRay x264 LOST.mkv");
		PhoenixRenamer.main(new String[] {"--renameDirectories", "--renameArtifacts", "--loglevel", "info", downloads.getAbsolutePath()});

		assertTrue(new File(downloads, "J. Edgar (2011)/J. Edgar (2011).mkv").exists());
	}

	@Test
	public void testRenameDirectoryWhenFileIsAlreadyRenamed() throws Exception {
		// reported by mkanet
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "The Rum Diary 2011 1080p BluRay x264 EbP/The Rum Diary (2011).mkv");
		PhoenixRenamer.main(new String[] {"--renameDirectories", "--renameArtifacts", "--loglevel", "info", downloads.getAbsolutePath()});

		assertTrue(new File(downloads, "The Rum Diary (2011)/The Rum Diary (2011).mkv").exists());
	}

	@Test
	public void testRenameTVWithAndInsteadOfAmperstand() throws Exception {
		// reported by ken birch
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "Mike Molly/Mike.And.Molly.S02E15.avi");
		PhoenixRenamer.main(new String[] {"--loglevel", "info", downloads.getAbsolutePath()});

		verify(new File(downloads, "Mike Molly/Mike & Molly - S02E15 - Valentine's Piggyback.avi"));
	}

	@Test
	public void testRenameTVWithDifferentMask() throws Exception {
		// reported by ken birch
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "Mike Molly/Mike.And.Molly.S02E15.avi");
		PhoenixRenamer.main(new String[] {"--tvMask","${Title} S${SeasonNumber:%02d}x${EpisodeNumber:%03d} ${EpisodeName}","--loglevel", "info", downloads.getAbsolutePath()});

		verify(new File(downloads, "Mike Molly/Mike & Molly S02x015 Valentine's Piggyback.avi"));
	}

	@Test
	public void testRenameMoviesWithDifferentMask() throws Exception {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "J Edgar 2011 MULTi 1080p BluRay x264 LOST/J Edgar 2011 MULTi 1080p BluRay x264 LOST.mkv");
		PhoenixRenamer.main(new String[] {"--renameDirectories", "--movieMask","${Title} - ${Year}", downloads.getAbsolutePath()});

		verify(new File(downloads, "J. Edgar - 2011/J. Edgar - 2011.mkv"));
	}

	@Test
	public void testWithSameNamedFolder() throws Exception {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "J. Edgar/J Edgar 2011 MULTi 1080p BluRay x264 LOST.mkv");
		File f2 = makeFile(downloads, "J. Edgar/J Edgar 2011 MULTi 1080p BluRay x264 LOST.mkv.properties");
		PhoenixRenamer.main(new String[] {"--renameDirectories", "--renameArtifacts", downloads.getAbsolutePath()});

		verify(new File(downloads, "J. Edgar (2011)/J. Edgar (2011).mkv"));
	}
	
	private void verify(File file) {
		assertNotNull(file);
		if (!file.exists()) {
			fail("Does Not Exist: " + file.getAbsolutePath());
		}
	}
	

	@Test
	public void testHugo() throws Exception {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);
		
		File f1 = makeFile(downloads, "Hugo/Hugo.mkv");
		PhoenixRenamer.main(new String[] {"--renameDirectories", "--renameArtifacts", "--loglevel", "info", downloads.getAbsolutePath()});

		assertTrue(new File(downloads, "Hugo (2011)/Hugo (2011).mkv").exists());
	}

	@Test
	public void testRenameMaskWithDir() throws Exception {
		File downloads = new File("build/test/junit/downloads");
		FileUtils.deleteDirectory(downloads);

		File f1 = makeFile(downloads, "Hugo.mkv");
		PhoenixRenamer.main(new String[] {"--movieMask","MOVIES/mv/${MediaTitle} (${Year})","--loglevel", "info", downloads.getAbsolutePath()});

        assertTrue(new File(downloads, "MOVIES/mv/Hugo (2011).mkv").exists());
	}

    @Test
    public void testRenameWithDifferentOutDir() throws Exception {
        File downloads = new File("build/test/junit/downloads");
        FileUtils.deleteDirectory(downloads);

        File f1 = makeFile(downloads, "Hugo.mkv");
        PhoenixRenamer.main(new String[] {"--movieOutDir", new File(downloads,"movies/new/").getAbsolutePath(),"--loglevel", "info", downloads.getAbsolutePath()});

        assertTrue(new File(downloads, "movies/new/Hugo (2011).mkv").exists());
    }

}
