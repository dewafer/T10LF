Top10LargeFiles
================


Overview
---------

This java application will scan and find the top 10 largest files in the directory and sub-directories as you specified. This application was written in mulit-Thread style and scan rate is much more faster than ordinary one-thread recursive style.

NOTE: Do not scan directory with extreme large quantity of files like root folder of your file system. Too many files will cause too much threads and disk operations which may lower your system performance or even damage your hard disk especially SSD hard disk.



Usage
------

This app require JavaSE 7 or newer Java runtime.
Please install the newest Java runtime from [java.com](http://www.java.com)

After installed the Java runtime, run the jar package using following command, where `<dir>` is the directory you may want to scan. 
	
	java -jar Top10LargeFiles.jar <dir>
	
Or, run the Main method using following command:
	
	java -cp Top10LargeFiles.jar wyq.appengine2.toolbox.FindTop10LargeFiles <dir>
	
The largest TOP 10 files will be displayed at command line like:

	C:\test\java -jar Top10LargeFiles.jar .\
	starting search at: C:\test
	Search started, wait termination...
	Search finished. Found TOP 10 large files:
	TOP 10 size:704M(739174400)C:\test\file1
	TOP 9 size:715M(750776320)C:\test\file2
	TOP 8 size:754M(791371776)C:\test\subdir\file3
	TOP 7 size:795M(833779536)C:\test\file4
	TOP 6 size:998M(1046987908)C:\test\subdir\file5
	TOP 5 size:1015M(1065238547)C:\test\subdir\file6
	TOP 4 size:1G(1207140352)C:\test\file7
	TOP 3 size:1G(1583874048)C:\test\subdir\file8
	TOP 2 size:1G(1696366592)C:\test\subdir\file9
	TOP 1 size:1G(1705213952)C:\test\file10

If the scanned directory and sub-directories contain file less than 10, all the file will be displayed and ordered by size descending.

If the scanned directory and sub-directories are/is empty, the result will be displayed like this:

	C:\test\java -jar Top10LargeFiles.jar .\empty
	starting search at: .\empty
	Search started, wait termination...
	Search finished. Found TOP 0 large files:


Development
------------

This app was developed with Eclipse and JDK 8. You can acquire Eclipse from [eclipse.org](http://download.eclipse.org) and JDK 8 from [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Please import the Top10LargeFiles_src.zip into Eclipse to review the code.


### Directories ###

* The `src` directory contains the main source files.
* The `test` directory contains the JUnit test source files.


### Source Files ###

* This file 
`/Top10LargeFiles/src/wyq/appengine2/toolbox/FindTop10LargeFiles.java`
is the Main executable source file and contains the main algorithm for calculating the file size.
* This file
`/Top10LargeFiles/src/wyq/appengine2/toolbox/MuliThreadFileFinder.java`
provides the mulit-thread directory scan functionality.
* `/Top10LargeFiles/test/wyq/test/FindTop10Test.java` is the JUnit test file for FindTop10LargeFiles.java and
* `/Top10LargeFiles/test/wyq/test/MulitThreadTester.java` is the JUnit test file for MuliThreadFileFinder.java

All java files have been commented for easier understanding.



Author
-------

Created by dewafer [(Linkedin)](http://www.linkedin.com/pub/yin-qiu-wang/76/60/756). Review more code at my [github](https://github.com/dewafer) or [Jazzhub](https://hub.jazz.net/user/dewafer). Contact me: [dewafer@gmail.com](mailto:dewafer@gmail.com).



 