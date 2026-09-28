package data;

import datastructure.CustomLinkedList;
import model.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Reads synthetic transactions from a CSV file.
 * Expected line format, no header row:
 *   transactionId,sourceId,destinationId,amount,timestamp
 * Example:
 *   T001,U001,M001,500,2026-09-01T10:15
 *
 * String.split() is used only as a local, throwaway parsing step for a
 * single line -- the resulting array is never stored or passed around,
 * so it is a parsing mechanism, not a project data structure.
 */
public class TransactionLoader {
    private static final int EXPECTED_FIELD_COUNT = 5;

    /**
     * Loads and validates every transaction in the file, in file order.
     * Duplicate transaction IDs are not filtered here -- that is Graph's
     * responsibility (see Graph.addEdge).
     *
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if a line is malformed, or a field
     *         fails Transaction's own validation (invalid ID, non-positive
     *         amount, self-transaction)
     */
    public CustomLinkedList<Transaction> loadFromCsv(Path filePath) throws IOException {
        CustomLinkedList<Transaction> transactions = new CustomLinkedList<>();
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue; // skip blank lines
                }
                transactions.addLast(parseLine(trimmed, lineNumber));
            }
        }
        return transactions;
    }

    private Transaction parseLine(String line, int lineNumber) {
        String[] fields = line.split(",", -1); // local only, discarded after this method
        if (fields.length != EXPECTED_FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": expected " + EXPECTED_FIELD_COUNT
                            + " fields, found " + fields.length + ": " + line);
        }
        try {
            String transactionId = fields[0].trim();
            String sourceId = fields[1].trim();
            String destinationId = fields[2].trim();
            long amount = Long.parseLong(fields[3].trim());
            LocalDateTime timestamp = LocalDateTime.parse(fields[4].trim());
            return new Transaction(transactionId, sourceId, destinationId, amount, timestamp);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": malformed amount or timestamp: " + line, e);
        }
        // IllegalArgumentException thrown by the Transaction constructor itself
        // (bad ID, non-positive amount, self-transaction) propagates unchanged --
        // it already carries a clear message.
    }
}