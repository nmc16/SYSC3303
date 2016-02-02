/**
 * Table class to hold the synchronized methods for the chefs and agent
 * to create the sandwiches.
 *
 * Class should be shared between {@link ChefThread}s and {@link AgentThread}s
 * in order to create mutual exclusion on elements being placed onto table.
 *
 * @author  Nicolas McCallum 100936816
 */
public class ElementTable {
    private Ingredient missingElement;
    private boolean tableEmpty;
    private boolean agentFinished;

    public ElementTable() {
        // Initialize all the placeholder variables
        missingElement = Ingredient.ALL;
        tableEmpty = true;
        agentFinished = false;
    }

    /**
     * Synchronized method for the agent to place two ingredients onto the table
     * and notifies all the threads that the table is no longer empty.
     *
     * @param missingElement Ingredient missing from the table to make a sandwhich
     */
    public synchronized void placeElements(Ingredient missingElement) {
        // Wait for lock and until the table is empty
        while(!tableEmpty) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for table to empty: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        // The table is no longer empty and set the missing element from the table
        tableEmpty = false;
        System.out.println("[Agent] Agent placed ingredients! Missing ingredient is: " + missingElement);
        this.missingElement = missingElement;

        // Notify other threads that table has elements
        notifyAll();
    }

    /**
     * Synchronized method that returns the missing ingredient from the table given the
     * table has elements on it already.
     *
     * @return Missing ingredient needed to make a sandwich
     */
    public synchronized Ingredient getMissingElement() {
        // Wait for lock and until the table has elements
        while(tableEmpty) {
            // If the agent is finished leave the method to avoid deadlock at program end
            if (agentFinished) {
                return null;
            }

            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for table to fill: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return missingElement;
    }

    /**
     * Synchronized method that will make the sandwich for the thread using the element passed
     * if the table has elements and the missing element is the same.
     *
     * @param threadName Name to use when printing thread information
     * @param element Chef thread missing element to use to make sandwich
     */
    public synchronized void sandwichMade(String threadName, Ingredient element) {
        // Wait for lock and until the missing element is the same as the chef's element
        while (tableEmpty && element != missingElement) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting to make sandwich: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        // Empty the table and notify other threads
        System.out.println("[" + threadName + "] Chef added " + element + " and made a sandwich!");
        tableEmpty = true;
        notifyAll();
    }

    /**
     * Pauses the thread until an event happens on the table.
     */
    public synchronized void pause() {
        try {
            wait();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for event: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Notifies all waiting threads of an event.
     */
    public synchronized void wake() {
        notifyAll();
    }

    /**
     * Sets the agent finished which closes all chef threads waiting on this table if set to true.
     *
     * @param agentFinished Boolean value to set true if agent is finished
     */
    public void setAgentFinished(boolean agentFinished) {
        this.agentFinished = agentFinished;
    }

    /**
     * Returns true if the agent is finished producing sandwiches.
     *
     * @return Returns true if agent has finished producing elements
     */
    public boolean isAgentFinished() {
        return agentFinished;
    }
}
