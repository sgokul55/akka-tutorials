package examples;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.japi.function.Function;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static examples.InventoryManagement.startAllThreads;


public class ActorInventoryManagement {

    /**
     * Actor based Inventory.
     */
    public static class AkkaInventory extends AbstractBehavior<AkkaInventory.Command> {

        private long stockCount = 0;

        private AkkaInventory(ActorContext<Command> context) {
            super(context);
        }

        public static Behavior<Command> create() {
            return Behaviors.setup(context -> new AkkaInventory(context));
        }

        @Override
        public Receive<Command> createReceive() {
            return newReceiveBuilder()
                    .onMessage(Update.class, processTheCommand()).onMessage(View.class, param -> {
                        System.out.println("Total stocks now " + stockCount);
                        return this;
                    })
                    .build();
        }

        private Function<Update, Behavior<Command>> processTheCommand() {
            return param -> {
                if (param.equals(Update.INCREMENT)) {
                    stockCount++;
                } else {
                    stockCount--;
                }
                // which behaviour will be used for the next message the actor receives... here same
                return this;
            };
        }

        public static enum Update implements Command {
            INCREMENT,
            DECREMENT
        }

        public static enum View implements Command {
            PRINT
        }

        interface Command {
        }
    }

    public static class InventoryIncrementer implements Runnable {

        private ActorSystem actor_inventory;

        public InventoryIncrementer(ActorSystem actor_inventory) {
            this.actor_inventory = actor_inventory;
        }

        @Override
        public void run() {
            actor_inventory.tell(AkkaInventory.Update.INCREMENT);
        }
    }

    public static class InventoryDecrementer implements Runnable {

        private ActorSystem actor_inventory;

        public InventoryDecrementer(ActorSystem actor_inventory) {
            this.actor_inventory = actor_inventory;
        }

        @Override
        public void run() {
            actor_inventory.tell(AkkaInventory.Update.DECREMENT);
        }
    }

    private static final int NO_OF_THREADS = 10000;

    public static void main(String[] args) {
        ActorSystem actor_inventory = ActorSystem.create(AkkaInventory.create(), "ActorInventory");

        List<Thread> incrementer = addThreads(actor_inventory, "Increment-Threads");
        List<Thread> decrementer = addThreads(actor_inventory, "Decrement-Threads");

        startAllThreads(incrementer);
        startAllThreads(decrementer);

        actor_inventory.tell(AkkaInventory.View.PRINT);
        System.exit(0);
    }

    private static List<Thread> addThreads(ActorSystem actor_inventory, String type) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NO_OF_THREADS; i++) {
            if ("Increment-Threads".equals(type))
                threads.add(new Thread(new InventoryIncrementer(actor_inventory)));
            else
                threads.add(new Thread(new InventoryDecrementer(actor_inventory)));
        }
        return threads;
    }

}