# phoenix-renamer
Command line media renaming tool for Movie and TV files

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
