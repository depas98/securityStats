# Security Device Service
This program will monitor a directory every second for json files that come from a security device.  It will read in and process the new files and save status information on them.

The files will contain one event per file with the following format:

	{"Type":"Door", "Date":"2017-02-01 10:01:02", "open": true}
	{"Type":"Alarm", "Date":"2017-02-01 10:01:01", "name":"fire", "floor":"1", "Room": "101"}
	{"Type":"img", "Date":"2017-02-01 10:01:02", "bytes": "ab39szh6", "size": 8}
	
The program will then print out the status information every second see example below for sample output:

	Event Count: 8, Door Count: 2, Image Count: 1, Alarm Count: 5, avgProcessingTime: 10ms

#### The program makes the following assumptions:
	1. Security Types can only be "DOOR", "ALARM", or "IMG"
	2. Only one task thread for reading in files
	3. Only one task thread for printing out status
	4. Service doesn't store each event item added, only stores the status information 
	(event counts, and avgProcessingTime) 
	5. Files are only read from the <prgrams home>/files directory Note: Four files are already 
	included in the Files directory
	6. The program only checks the last modified date of a file, so if a file is updated it 
	will be processed	
    
### Instructions For Running Program

### Setup
Install Open JDK 11.x, downloads can be found here
https://jdk.java.net/archive/

Make sure java is in your environment PATH 
#### Linux
	export JAVA_HOME="path where java is installed"
	export PATH=$JAVA_HOME/bin:$PATH

#### Windows
See the following:
https://javatutorial.net/set-java-home-windows-10

#### Testing Java Install
You should be able to run  both of these from the command line:
	
	java -version
	javac -version

Output should be something like the following:

	java -version
	openjdk version "11.0.2" 2019-01-15
	OpenJDK Runtime Environment 18.9 (build 11.0.2+9)
	OpenJDK 64-Bit Server VM 18.9 (build 11.0.2+9, mixed mode)

	javac -version
	javac 11.0.2

### Running
#### SecuirtyMonitor Main Method
You can test the program by running the run.sh or run.bat scripts, this will read files in the Files directory and then print out the status every second.  Output will be similar to the following:

	Event Count: 0, avgProcessingTime: 0
	Event Count: 4, Alarm: 2, Image: 1, Door: 1, avgProcessingTime: 52
	Event Count: 4, Alarm: 2, Image: 1, Door: 1, avgProcessingTime: 52

To stop the program you can type in "q", "quit", or "ctrl-c"

In addition I included a File Generator that will randomly create 100 files, so you can see the program process those as they come in.

To run you will need to open a separate command window and run runFileGenertor.bat or runFileGenertor.sh.  Output will be the following:


	Generating file: C:\source\github\depas98\security_device\files\door_gen30.json
	with text: {"Type":"door", "Date":"2017-02-01 10:01:02", "open": true}

	---------------------

	Generating file: C:\source\github\depas98\security_device\files\door_gen31.json
	with text: {"Type":"door", "Date":"2017-02-01 10:01:02", "open": true}



#### Unit Test
You can run all the unit test for this service, output will be like the following:

	JUnit version 4.12
	..The line value [{"Type""door", "Date""2017-02-01 10:01:02", "open" true}] is not the correct format.
	..
	Time: 0.066

	OK (4 tests)

#### Linux
In the "security_device" directory

Give the script files exec permission

	chmod 755 *.sh

Running Main Program execute the script below:

	./run.sh

Running File Generator Program execute the script below:

	./runFileGenertor.sh
	
Running Unit Tests execute the script below
	
	./runTests.sh


#### Windows
In the "security_device" directory

Running Main Program execute the script below:
  	
	run.bat

Running File Generator Program execute the script below:

	./runFileGenertor.bat

Running Unit Tests execute the script below
  
  	runTests.bat
