/**
 * Chef Thread class that holds one of the three ingredients in the {@link Ingredient}
 * enum needed to create a sandwich.
 *
 * Thread runs until the {@link AgentThread} has shut down and set the flag in the
 * {@link ElementTable}.
 *
 * Thread will produce sandwich on table if the table has elements and the missing element
 * is the same as the thread's.
 *
 * @author  Nicolas McCallum 100936816
 */
public class ChefThread implements Runnable {
    private Ingredient myIngredient;
    private ElementTable table;
    private String name;

    public ChefThread(ElementTable table, Ingredient myIngredient, String name) {
        this.table = table;
        this.myIngredient = myIngredient;
        this.name = name;
    }

    @Override
    public void run() {
        // Print startup and begin infinite loop
        System.out.println("[" + name + "] Starting...");
        while(true) {
            // If missing element is same as thread element then make a sandwich
            if (table.getMissingElement() == myIngredient) {
                table.sandwichMade(name, myIngredient);
            }

            // If the agent is finished release lock and break from loop
            if (table.isAgentFinished()) {
                table.wake();
                break;
            }
        }
        System.out.println("[" + name + "] Shutting down...");
    }
}
