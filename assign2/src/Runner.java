/**
 * Runner class that starts up 1 {@link AgentThread} and 3 {@link ChefThread}s
 * and joins them after to ensure all threads are closed successfully.
 *
 * @author  Nicolas McCallum 100936816
 */
public class Runner {

    public void run() {
        // Create the element table and agent thread
        ElementTable table = new ElementTable();
        Thread agentThread = new Thread(new AgentThread(table), "Agent Thread");

        // Create the chef threads
        Thread chefPBThread = new Thread(new ChefThread(table, Ingredient.PB, "Chef PB"));
        Thread chefBreadThread = new Thread(new ChefThread(table, Ingredient.BREAD, "Chef Bread"));
        Thread chefJamThread = new Thread(new ChefThread(table, Ingredient.JAM, "Chef Jam"));

        // Start all of the threads
        agentThread.start();
        chefPBThread.start();
        chefBreadThread.start();
        chefJamThread.start();

        // Wait for the agent thread to exit
        try {
            agentThread.join();
        } catch (InterruptedException e) {
            System.out.println("Error waiting for agent thread to finish: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        // Wait for all chef threads to exit
        try {
            chefBreadThread.join();
            chefJamThread.join();
            chefPBThread.join();
        } catch (InterruptedException e) {
            System.out.println("Error waiting for chef threads to finish: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        System.exit(0);
    }

    public static void main(String args[]) {
        Runner r = new Runner();
        r.run();
    }
}
