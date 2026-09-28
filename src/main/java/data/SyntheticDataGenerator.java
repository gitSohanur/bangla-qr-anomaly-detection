package data;

import datastructure.CustomLinkedList;
import model.Transaction;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Builds deterministic, synthetic transaction datasets. No randomness is used,
 * so every call returns exactly the same data. All IDs are synthetic.
 *
 * Each pattern uses its own ID range so patterns can be combined safely:
 *   normal: U001-U007, M001-M003, A002
 *   hub:    U011-U016, M005, A001
 *   cycle:  U021-U025, M007, M008, A003
 */
public class SyntheticDataGenerator {
    private final CustomLinkedList<Transaction> transactions = new CustomLinkedList<>();
    private int nextNumber = 1;
    private LocalDateTime clock = LocalDateTime.of(2026, 9, 1, 9, 0);

    private SyntheticDataGenerator() { }

    /** Scenario A: ordinary merchants with ordinary behaviour. */
    public static CustomLinkedList<Transaction> normal() {
        SyntheticDataGenerator g = new SyntheticDataGenerator();
        g.addNormalPattern();
        return g.transactions;
    }

    /** Scenario B: many users pay one merchant, which forwards the money to one account. */
    public static CustomLinkedList<Transaction> hub() {
        SyntheticDataGenerator g = new SyntheticDataGenerator();
        g.addHubPattern();
        return g.transactions;
    }

    /** Scenario C: a circular flow M007 -> A003 -> M008 -> M007. */
    public static CustomLinkedList<Transaction> cycle() {
        SyntheticDataGenerator g = new SyntheticDataGenerator();
        g.addCyclePattern();
        return g.transactions;
    }

    /** Scenario D: normal, hub and cycle patterns together in one dataset. */
    public static CustomLinkedList<Transaction> mixed() {
        SyntheticDataGenerator g = new SyntheticDataGenerator();
        g.addNormalPattern();
        g.addHubPattern();
        g.addCyclePattern();
        return g.transactions;
    }

    /** Appends one transaction with the next sequential ID; time advances 2 minutes each time. */
    private void add(String sourceId, String destinationId, long amount) {
        String id = String.format(Locale.ROOT, "T%03d", nextNumber++);
        transactions.addLast(new Transaction(id, sourceId, destinationId, amount, clock));
        clock = clock.plusMinutes(2);
    }

    private void addNormalPattern() {
        add("U001", "M001", 300);
        add("U002", "M001", 250);
        add("U003", "M001", 400);
        add("U004", "M002", 150);
        add("U005", "M002", 200);
        add("U006", "M003", 500);
        add("U007", "M003", 350);
        add("M003", "A002", 700);   // routine settlement to the merchant's own account
    }

    private void addHubPattern() {
        add("U011", "M005", 1000);
        add("U012", "M005", 1200);
        add("U013", "M005", 900);
        add("U014", "M005", 1100);
        add("U015", "M005", 1000);
        add("U016", "M005", 800);
        add("M005", "A001", 5800);  // everything forwarded to a single account
    }

    private void addCyclePattern() {
        add("U021", "M007", 600);
        add("U022", "M007", 700);
        add("U023", "M007", 500);
        add("U024", "M007", 800);
        add("U025", "M007", 900);
        add("M007", "A003", 3000);  // the ring: M007 -> A003 -> M008 -> M007
        add("A003", "M008", 3000);
        add("M008", "M007", 3000);
    }
}