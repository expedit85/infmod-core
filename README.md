
Inference Module
================


Folders:
--------


WebClockServer/		clock emulator (start, final e rate may be configured)
InferenceModule/	Inference Module engine
Libraries/		common sources (used by both projects above)
Samples/		samples


Tips:
-----

1) A list of relevant jars is listed in Libraries/jars.txt. Not all jars are required
   for building and/or running but some of them holds source code, which is useful for
   debugging purposes.

2) The source is able to be imported into eclipse, but it will not run until dependency
   jars are downloaded.

3) In case more jars are added in the future, it would be good to update de file
   Libraries/jars.txt instead of add the jars directly. Or a dependency manager could
   be configured (like maven).

4) Before running the samples by using command line scripts (run.bat or run.sh) build
   the required jars: ClockWebServer.jar and InferenceModule.jar. Samples may be run
   directly from eclipse too.
