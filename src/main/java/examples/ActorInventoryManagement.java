package examples;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;
import java.util.List;


public class ActorInventoryManagement {


    private static final int NO_OF_THREADS = 10000;

    public static void main(String[] args) {
        ActorSystem actor_inventory = ActorSystem.create(AkkaInventory.create(), "ActorInventory");
        List<Thread> incrementer = new ArrayList<>();
        List<Thread> decrementer = new ArrayList<>();
        // Add the incrementer and decrementer tasks
        for (int i = 0; i < NO_OF_THREADS; i++) {
            incrementer.add(new Thread(new InventoryIncrementer(actor_inventory)));
            decrementer.add(new Thread(new InventoryDecrementer(actor_inventory)));
        }
        for (int i = 0; i < NO_OF_THREADS; i++) {
            incrementer.get(i).start();
        }
        for (int i = 0; i < NO_OF_THREADS; i++) {
            decrementer.get(i).start();
        }
        actor_inventory.tell(AkkaInventory.Print.PRINT);
    }

    public static class InventoryIncrementer implements Runnable {

        private ActorSystem actor_inventory;

        public InventoryIncrementer(ActorSystem actor_inventory) {
            this.actor_inventory = actor_inventory;
        }

        @Override
        public void run() {
            actor_inventory.tell(AkkaInventory.Increment.INCREMENT);
        }
    }

    public static class InventoryDecrementer implements Runnable {

        private ActorSystem actor_inventory;

        public InventoryDecrementer(ActorSystem actor_inventory) {
            this.actor_inventory = actor_inventory;
        }

        @Override
        public void run() {
            actor_inventory.tell(AkkaInventory.Decrement.DECREMENT);
        }
    }


    public static class AkkaInventory extends AbstractBehavior<AkkaInventory.Command> {

        long stockCount = 0;

        private AkkaInventory(ActorContext<Command> context) {
            super(context);
        }

        public static Behavior<Command> create() {
            return Behaviors.setup(context -> new AkkaInventory(context));
        }

        @Override
        public Receive<Command> createReceive() {
            return newReceiveBuilder()
                    .onMessage(Increment.class, param -> {
                        stockCount++;
                        return this; // which behaviour will be used for the next message the actor receives... here same
                        // incase if we are switching, it will be useful
                    }).onMessage(Decrement.class, param -> {
                        stockCount--;
                        return this;
                    }).onMessage(Print.class, param -> {
                        System.out.println("Total stocks now " + stockCount);
                        return this;
                    })
                    .build();
        }


        public static enum Increment implements Command {
            INCREMENT
        }

        public static enum Decrement implements Command {
            DECREMENT
        }

        public static enum Print implements Command {
            PRINT
        }

        // marker interface
        interface Command {
        }


    }
}