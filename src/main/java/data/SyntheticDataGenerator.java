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
    /**
     * A larger, more realistic dataset (~107 users, 15 merchants, 5 active
     * accounts, 239 transaction records) for the final class demonstration
     * and GUI. Deterministic -- no randomness anywhere.
     *
     * Merchant allocation:
     *   M001-M006  safe normal merchants   (4 distinct senders each -- stays
     *                                       under the incoming-count threshold)
     *   M007       boundary normal merchant (exactly 5 distinct senders --
     *                                       sits exactly on that threshold)
     *   M008-M009  suspicious hubs          (many repeat senders, funnel
     *                                       everything to one account each)
     *   M010-M015  six cycle merchants forming three disjoint 3-vertex cycles:
     *                M010 -> A003 -> M011 -> M010
     *                M012 -> A004 -> M013 -> M012
     *                M014 -> A005 -> M015 -> M014
     *              Each also carries light "noise" transactions, so it still
     *              looks like a real, active merchant.
     *
     * IMPORTANT, and deliberate: CycleDetector (Phase 5) stops at the FIRST
     * cycle it finds. With three disjoint cycles present, only the first one
     * reached -- M010/A003/M011, the earliest cycle group added -- is
     * actually flagged by MerchantAnalyzer. M012-M015 are just as cyclic
     * structurally, but will NOT show cycleDetected = true in a single run.
     * This is not a bug -- it is the same single-cycle limitation documented
     * in Phases 5 and 6, deliberately exercised here at scale so it can be
     * tested directly rather than only described.
     *
     * A duplicate transaction ID ("T001") is appended at the end to
     * demonstrate Graph's duplicate rejection (Phase 4) at realistic scale.
     */
    public static CustomLinkedList<Transaction> realistic() {
        SyntheticDataGenerator g = new SyntheticDataGenerator();
        g.addSafeNormalMerchants();
        g.addBoundaryNormalMerchant();
        g.addSuspiciousHubs();
        g.addDisjointCycles();
        g.addCycleMerchantNoise();
        g.addDuplicateDemonstration();
        return g.transactions;
    }

    /** M001-M006: 4 distinct senders each, moderate amounts -- stays clearly NORMAL. */
    private void addSafeNormalMerchants() {
        int userNumber = 1;
        for (int m = 1; m <= 6; m++) {
            String merchantId = String.format(Locale.ROOT, "M%03d", m);
            for (int i = 0; i < 4; i++) {
                String userId = String.format(Locale.ROOT, "U%03d", userNumber);
                long amount = 200 + ((userNumber * 23) % 600);
                add(userId, merchantId, amount);
                userNumber++;
            }
        }
    }

    /** M007: exactly 5 distinct senders -- sits exactly on the incoming-count threshold. */
    private void addBoundaryNormalMerchant() {
        for (int i = 0; i < 5; i++) {
            int userNumber = 25 + i; // continues on from M001-M006's 24 users
            String userId = String.format(Locale.ROOT, "U%03d", userNumber);
            long amount = 200 + ((userNumber * 23) % 600);
            add(userId, "M007", amount);
        }
    }

    /** M008, M009: many repeat senders, each funnelling everything to one account. */
    private void addSuspiciousHubs() {
        String[] hubMerchants = {"M008", "M009"};
        String[] hubAccounts = {"A001", "A002"};
        int userNumber = 30;
        for (int h = 0; h < hubMerchants.length; h++) {
            for (int s = 0; s < 30; s++) {
                String userId = String.format(Locale.ROOT, "U%03d", userNumber);
                for (int repeat = 0; repeat < 3; repeat++) {
                    long amount = 300 + (((userNumber * 17) + (repeat * 97)) % 700);
                    add(userId, hubMerchants[h], amount);
                }
                userNumber++;
            }
            add(hubMerchants[h], hubAccounts[h], 10000); // forwards a large sum onward
        }
    }

    /** Three disjoint 3-vertex cycles, in the shape M01 -> A01 -> M07 -> M01. */
    private void addDisjointCycles() {
        addCycle("M010", "A003", "M011");
        addCycle("M012", "A004", "M013");
        addCycle("M014", "A005", "M015");
    }

    private void addCycle(String merchantA, String account, String merchantB) {
        add(merchantA, account, 1000);
        add(account, merchantB, 1000);
        add(merchantB, merchantA, 1000);
    }

    /** Light, ordinary incoming activity for each cycle merchant, so it still looks like a real merchant. */
    private void addCycleMerchantNoise() {
        String[] cycleMerchants = {"M010", "M011", "M012", "M013", "M014", "M015"};
        int userNumber = 90;
        for (String merchantId : cycleMerchants) {
            for (int i = 0; i < 3; i++) {
                String userId = String.format(Locale.ROOT, "U%03d", userNumber);
                long amount = 150 + ((userNumber * 11) % 400);
                add(userId, merchantId, amount);
                userNumber++;
            }
        }
    }

    /** Deliberately reuses "T001" to demonstrate duplicate-transaction rejection at scale. */
    private void addDuplicateDemonstration() {
        transactions.addLast(new Transaction("T001", "U001", "M001", 999, clock));
        clock = clock.plusMinutes(2);
    }






}