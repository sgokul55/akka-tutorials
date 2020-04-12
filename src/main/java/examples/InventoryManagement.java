package examples;

import java.util.ArrayList;
import java.util.List;

public class InventoryManagement {

    public static final int NO_OF_THREADS = 10_000;

    public static void main(String[] args) {


        Inventory inventory = new Inventory(0);

        List<Thread> incrementer = new ArrayList<>();
        List<Thread> decrementer = new ArrayList<>();
        // Add the incrementer and decrementer tasks
        for (int i = 0; i < NO_OF_THREADS; i++) {
            incrementer.add(new Thread(new InventoryIncrementer(inventory)));
            decrementer.add(new Thread(new InventoryDecrementer(inventory)));
        }
        for (int i = 0; i < NO_OF_THREADS; i++) {
            incrementer.get(i).start();
        }
        for (int i = 0; i < NO_OF_THREADS; i++) {
            decrementer.get(i).start();
        }
        inventory.printStockCount();
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
}
