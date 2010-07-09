The finch.jar used by the applications module gets created in this directory.  You can create it by calling
"ant build-finch-jar" which won't do anything if the jar already exists.  To force it to be recreated, use
"ant force-build-finch-jar".

All the files in this directory (except for this one) are generated or copied from elsewhere, so they should NOT be
checked in to SVN.