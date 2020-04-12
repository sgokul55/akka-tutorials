package examples;

import java.util.ArrayList;
import java.util.List;

public class InventoryManagement {

    /**
     * Basic inventory Bean which maintains the stock count.
     */
    public static class Inventory {

        private long stockCount;

        public Inventory(long stockCount) {
            this.stockCount = stockCount;
        }

        public void incrementItemByOne() {
            this.stockCount++;
        }

        public void decrementItemByOne() {
            this.stockCount--;
        }

        public void printStockCount() {
            System.out.println("Total remaining stock is " + stockCount);
        }

    }

    /**
     * Task which increments the inventory
     */
    public static class InventoryIncrementer implements Runnable {

        private Inventory inventory;

        public InventoryIncrementer(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public void run() {
            inventory.incrementItemByOne();
        }
    }

    /**
     * Task which decrements the inventory
     */
    public static class InventoryDecrementer implements Runnable {

        private Inventory inventory;

        public InventoryDecrementer(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public void run() {
            inventory.decrementItemByOne();
        }
    }

    public static final int NO_OF_THREADS = 10_000;

    public static void main(String[] args) {

        Inventory inventory = new Inventory(0);
        List<Thread> incrementer = addThreads(inventory, "Increment-Threads");
        List<Thread> decrementer = addThreads(inventory, "Decrement-Threads");

        startAllThreads(incrementer);
        startAllThreads(decrementer);

        inventory.printStockCount();
    }

    private static void startAllThreads(List<Thread> threads) {
        for (int i = 0; i < NO_OF_THREADS; i++) {
            threads.get(i).start();
        }
    }

    private static List<Thread> addThreads(Inventory inventory, String type) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NO_OF_THREADS; i++) {
            if ("Increment-Threads".equals(type))
                threads.add(new Thread(new InventoryIncrementer(inventory)));
            else
                threads.add(new Thread(new InventoryDecrementer(inventory)));
        }
        return threads;
    }


}
