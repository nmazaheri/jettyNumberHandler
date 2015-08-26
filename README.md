Number Server:
=====================
A demonstration of a Java Server Application that dedupes 9 digit numerical input data and writes it to file.

Requirements:
------------
Create a Server which opens a socket and allows 5 concurrent clients to send any number of 9 digit numbers. The application must write a de-dupliated list to a log file in any order. If a client sends an invalid number then the data should be discarded and the client should be terminated. Every 10 seconds the application must print the new unique numbers, duplicate count, and the unique total. If any client sent the word "terminate", then the client should perform a clean shutdown.

Assumptions / Tradeoffs:
-----------
1. Client input does not need to be written to file in real-time.
2. During a termination sequence new data may be lost for the above reason.

Design:
------

* NumberManager: the main class and initializes the other classes.
* server.ServerListener: creates server.ClientTask's when a new connections occurs.
* server.ClientTask: Multiple runnables which reads data from the client socket and passes it to a single shared WindowDataStore
* DataStore: 
* DataLogger: 

Intructions:
-----------
* install and setup maven
* install java7 JDK
* run "mvn package" in root folder
* run "java -jar target/numberserver.jar" 

Sample Output:
-------------
* Received 1,608,429 unique numbers, 227,510 duplicates. UniqueTotal: 5,994,428
* Received 1,793,213 unique numbers, 135,951 duplicates. UniqueTotal: 7,787,641
* Received 3,602,506 unique numbers, 269,901 duplicates. UniqueTotal: 11,390,147
