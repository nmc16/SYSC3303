#############################
#	SYSC 3303 Assignment 2  #
#		Nicolas McCallum    #
#		100936816           #
#############################

ChefThread.java
----------------
    Chef Thread class that holds one of the three ingredients in the Ingredient
    enum needed to create a sandwich.
 
    Thread runs until the Agent has shut down and set the flag in the Element Table.

    Thread will produce sandwich on table if the table has elements and the missing element
    is the same as the thread's.

AgentThread.java
-----------------
    Agent Thread that adds two Ingredient elements to the table at a time
    leaving one missing element needed for a sandwich.
 
    Repeats 20 times and adds random elements each time.
	
ElementTable.java
------------------
	Table class to hold the synchronized methods for the chefs and agent
    to create the sandwiches.
	
Runner.java
------------
    Runner class that runs 3 chef threads and one agent thread for 20 times and then closes the threads.
	
To Run Program
---------------
1) Start the program by running the main method in the Runner Class. In eclipse right click the file -> select run as -> java application.

