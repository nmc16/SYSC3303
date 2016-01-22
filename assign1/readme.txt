#############################
#	SYSC 3303 Assignment 1 	#
#		Nicolas McCallum	#
#		100936816			#
#############################

Client.java
-------------
	Client that connects to the intermediate host and sends 11 requests that
	randomize between read and write requests.

	The 11th request is an invalid request that the server will throw an exception on.

Host.java
--------------
	Intermediate host that connects to the client and sends the requests
	to the server. Holds two sockets, one for the client and one for
	the server.
	
Server.java
--------------
	Server that waits for requests from clients and handles them.
  
	Creates a new response socket for each request from clients. Sends
	a response code for write and read requests if they are valid.
	
To Run Program
---------------
1) Start the Server by running the main method. In eclipse right click the file -> select run as -> java application.
2) Start the Host by running the main method. In eclipse right click the file -> select run as -> java application.
3) Start the Client by running the main method. In eclipse right click the file -> select run as -> java application.

