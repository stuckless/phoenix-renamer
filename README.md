# phoenix-renamer
Command line media renaming tool for Movie and TV files

[ ![Download](https://api.bintray.com/packages/stuckless/sagetvphoenix/phoenix-renamer/images/download.svg) ](https://bintray.com/stuckless/sagetvphoenix/phoenix-renamer/_latestVersion)

# Dependencies
* Java 7
* Window/Linux/Mac
* Internet Connection

# Usage
```bash
usage: phoenix-renamer [OPTIONS] directory|file
 -?,--help                 Help
 -c,--cmd <arg>            Execute this command when the rename is
                           complete.  The only parameter passed to the
                           command will be the new filename to which the
                           content was renamed.  The ENVIRONMENT will
                           contain all the metadata properties prefixed by
                           PHOENIX_, e.g., MediaTitle will be
                           PHOENIX_MEDIATITLE.  Please note the command
                           must be a fully qualified path to the command
                           being executed.
    --cmd-args <arg>       These args gets passed as the second arg to the
                           --cmd
 -f,--cmd-failed <arg>     Execute this command when the rename fails.
                           Param 1 will be the filename, Param 2 will be a
                           message. Please note the command must be a
                           fully qualified path to the command being
                           executed.
    --loglevel <arg>       Log output level; debug, info, warn
 -m,--movieMask <arg>      Movie Rename mask.  ie, something like
                           '${MediaTitle} (${Year})'
    --movieOutDir <arg>    Movie destination dir (defaults to same dir as
                           movie file)
    --no-dirname-lookups   If passed, then it not attempt to use the
                           DIRNAME as the Media Title when the lookup
                           fails for a Media Item
    --renameArtifacts      Rename additional artifacts (ie, properties,
                           sub titles, etc)
    --renameDirectories    Rename Parent directories of a given video file
                           as well (use with caution)
 -t,--tvMask <arg>         TV Rename mask.  ie, something like
                           '${MediaTitle} -
                           S${SeasonNumber:%02d}E${EpisodeNumber:%02d} -
                           ${EpisodeName}'
    --tvOutDir <arg>       TV destination dir (defaults to same dir as
                           movie file)
    --watchDir <arg>       Watch this dir for new media files, and rename
                           when they arrive
    --watchSeconds <arg>   As files are being watched, ONLY process the
                           file if no changes have happened to the file
                           within this amount of seconds
```

# Examples
## Rename a single media file
```
$ phoenix-renamer Terminator.mp4
```
Terminator.mp4 would be renamed to The Terminator (1980).mp4

## Rename all files in directory
```
$ phoenix-renamer MediaDir/
```
Any media files, TV or Movies, that are found in MediaDir will be renamed.

```
$ phoenix-renamer --tvOutDir "/TV/" --movieOutDir "/Movies/" MediaDir/
```
Any media files that are found, will be renamed, and TV files will be renamed to the tvOutDir and the movie files will be renamed to the movieOutDir.  NOTE IF tvOutDir or movieOutDir is not on the same filesystem as the original file, the rename will fail.

## Rename media directories
```
$ phoenix-renamer --renameDirectories --renameArtifacts MediaDir/
```
This assumes that in your MediaDir you have each movie in a separate folder.  For example
```
MediaDir/Terminator/Terminator.mp4
```
Once the rename is complete it will be
```
MediaDir/The Terminator (1980)/The Terminator (1980).mp4
```
renameArtifacts will rename any other files, such are subtitles, to match the media file renaming.

## Run a command on success or failure
```
$ phoenix-renamer --cmd /full/path/to/success.sh --cmd-failed /full/path/to/failed.sh MediaDir/
```
For each file that is renamed, success.sh or failed.sh will be called, depending on if the rename was sucessful or not.

In the case it is successful, the success.sh will get the new filename as the first argument.  Also the ENVIRONMENT for the script will contain all the metadata for the newly renamed file, with each variable prefixed by PHOENIX_

The complete list of variables include.
```bash
PHOENIX_MEDIATITLE=The Terminator
PHOENIX_EXTENDEDRATINGS=
PHOENIX_STEREO=false
PHOENIX_HDTV=false
PHOENIX_PARENTALRATING=
PHOENIX_SEASONFINAL=false
PHOENIX_TOTALPARTS=0
PHOENIX_EPISODENUMBER=0
PHOENIX_SERIESINFOID=0
PHOENIX_GENREID=
PHOENIX_SEASONPREMIERE=false
PHOENIX_DEFAULTBACKGROUND=
PHOENIX_SCRAPEDBY=
PHOENIX_SERIESPREMIERE=false
PHOENIX_DISCNUMBER=0
PHOENIX_WRITER=James Cameron;Gale Anne Hurd
PHOENIX_TAGLINE=Your future is in his hands.
PHOENIX_MEDIAURL=
PHOENIX_EXTERNALID=
PHOENIX_ORIGINALAIRDATE=467611200000
PHOENIX_MEDIATYPE=Movie
PHOENIX_QUOTES=
PHOENIX_CHANNELPREMIERE=false
PHOENIX_FANART=0|BACKGROUND|http://image.tmdb.org/t/p/original/qafr2jiIqIcYQYq6pkWtaYlek5X.jpg;0|POSTER|http://image.tmdb.org/t/p/original/w9DzDW44CISoLJyaQICSOoSsIEN.jpg;0|POSTER|http://image.tmdb.org/t/p/original/3gYbsq8JK7twS4rAZhgCeOfyCzG.jpg;0|POSTER|http://image.tmdb.org/t/p/original/q7edM7f6b0cKMtE7pRxqEohdLif.jpg;0|POSTER|http://image.tmdb.org/t/p/original/q8ffBuxQlYOHrvPniLgCbmKK4Lv.jpg;0|POSTER|http://image.tmdb.org/t/p/original/wj9PcqCXJt5mxG2w9G8lwmvecWA.jpg;0|BACKGROUND|http://image.tmdb.org/t/p/original/6yFoLNQgFdVbA8TZMdfgVpszOla.jpg;0|BACKGROUND|http://image.tmdb.org/t/p/original/1i9ySmVWvIRZKCIQCxkz2807Y0.jpg;0|BACKGROUND|http://image.tmdb.org/t/p/original/97OEzl2I8fNmKoU2fkujZij3dBO.jpg;0|BACKGROUND|http://image.tmdb.org/t/p/original/mXVv4Chm01Ph3FydCD77YWClKhS.jpg
PHOENIX_PREMIERE=false
PHOENIX_CC=false
PHOENIX_SEASONNUMBER=0
PHOENIX_DESCRIPTION=In the post-apocalyptic future, reigning tyrannical supercomputers teleport a cyborg assassin known as the "Terminator" back to 1984 to kill Sarah Connor, whose unborn son is destined to lead insurgents against 21st century mechanical hegemony. Meanwhile, the human-resistance movement dispatches a lone warrior to safeguard Sarah. Can he stop the virtually indestructible killing machine?
PHOENIX_RATED=R
PHOENIX_ALBUM=
PHOENIX_GENRE=Action/Thriller/Science Fiction
PHOENIX_DIRECTOR=James Cameron
PHOENIX_DEFAULTBANNER=
PHOENIX_DEFAULTPOSTER=
PHOENIX_MEDIAPROVIDERID=tmdb
PHOENIX_SCRAPEDDATE=
PHOENIX_SUBTITLED=false
PHOENIX_ACTOR=Arnold Schwarzenegger -- The Terminator;Michael Biehn -- Kyle Reese;Linda Hamilton -- Sarah Connor;Paul Winfield -- Lieutenant Ed Traxler;Lance Henriksen -- Detective Vukovich;Bess Motta -- Ginger Ventura;Earl Boen -- Dr. Peter Silberman;Rick Rossovich -- Matt Buchanan;Bill Paxton -- Punk Leader;Brian Thompson -- Punk;Franco Columbu -- Future Terminator;Dick Miller -- Pawnshop Clerk;Joe Farago -- TV Anchorman
PHOENIX_MISC=
PHOENIX_PARTNUMBER=0
PHOENIX_PRODUCER=Gale Anne Hurd
PHOENIX_MEDIAPROVIDERDATAID=218
PHOENIX_RUNNINGTIME=6480000
PHOENIX_EPISODENAME=The Terminator
PHOENIX_EPISODENUMBER=0
PHOENIX_SEASONNUMBER=0
PHOENIX_USERRATING=69
PHOENIX_YEAR=1984
PHOENIX_SERIESFINALE=false
PHOENIX_SAP=false
PHOENIX_TRAILERURL=http://www.youtube.com/watch?v=CEpuUhM26k8
PHOENIX_TRIVIA=
PHOENIX_LANGUAGE=
PHOENIX_TITLE=
PHOENIX_IMDBID=tt0088247
```
For a sucess script, you might use this information to move the file yourself to another location using information that is in the metadata.  Or, you might choose write another metadata file for the renamed mediafile.

When the rename fails, failed.sh is called with 2 parameter.  The first is the filename and second is the message.

# Advanced Uses
Phoenix renamer can run, and watch a directory (and subdirectories) for changes, and then rename files as the appear.

```
$ phoenix-renamer --no-dirname-lookups --watchDir DIR_TO_WATCH  --watchSeconds 100
```

The above command will run and watch ```DIR_TO_WATCH``` for new files.  When a new file is added, it check the file every 100 seconds to make sure it is not being written to.  Once the file is stable (ie hasn't changed in 100 seconds), then it will try to rename it.

no-dirname-lookups is required when watching, since ```--watchDir``` does not support renaming directories and we don't want our files being renamed based on the name of the directory that they reside.

The above command would most likely be used with the ```--cmd-failed``` so that you can handle failures and perhaps send an email, or use pushbullet to send a notice to your phone, etc.

```watchDir``` is NOT compatible with rename directories or rename artifacts.  ie, this should only be used if you expect that DIR_TO_WATCH will only contain single media files and not media file directories.

