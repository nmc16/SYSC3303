import java.util.Random;

/**
 * Agent Thread that adds two {@link Ingredient} elements to the table at a time
 * leaving one missing element needed for a sandwich.
 *
 * Repeats 10 times and adds random elements each time. Afterwards sets the flag in
 * the {@link ElementTable} object to notify Chef threads to close.
 *
 * @author  Nicolas McCallum 100936816
 */
public class AgentThread implements Runnable {
    private ElementTable table;

    public AgentThread(ElementTable table) {
        this.table = table;
    }

    @Override
    public void run() {
        // Print startup and start looping
        System.out.println("[Agent] Agent starting...");
        Random random = new Random();
        int produced = 0;

        while(produced < 10) {
            // Produce missing ingredient randomly
            int missing = random.nextInt(3);
            Ingredient missingIngredient = Ingredient.ALL;

            // Set the missing ingredient
            switch (missing) {
                case 0: missingIngredient = Ingredient.BREAD;
                        break;
                case 1: missingIngredient = Ingredient.JAM;
                        break;
                case 2: missingIngredient = Ingredient.PB;
                        break;
            }

            // Place the elements and add to the counter
            table.placeElements(missingIngredient);
            produced++;
        }

        // Wait until the last sandwich is produced and set the flag to shutdown
        table.pause();
        table.setAgentFinished(true);
        System.out.println("[Agent] Agent shutting down...");
    }
}
