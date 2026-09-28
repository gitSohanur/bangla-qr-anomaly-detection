package data;

import datastructure.CustomLinkedList;
import model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TransactionLoaderTest {

    private Path writeFile(Path dir, String name, String content) throws IOException {
        Path file = dir.resolve(name);
        Files.writeString(file, content);
        return file;
    }

    @Test
    void loadFromCsv_normalFile_parsesInOrder(@TempDir Path dir) throws IOException {
        Path file = writeFile(dir, "normal.csv",
                "T001,U001,M001,500,2026-09-01T10:15\n" +
                        "T002,U002,M001,800,2026-09-01T10:17\n" +
                        "T003,M001,A001,1200,2026-09-01T10:30\n");

        CustomLinkedList<Transaction> data = new TransactionLoader().loadFromCsv(file);
        assertEquals(3, data.size());
        assertEquals("T001: U001 -> M001 | 500 | 2026-09-01T10:15", data.peekFirst().toString());
    }

    @Test
    void loadFromCsv_emptyFile_returnsEmptyList(@TempDir Path dir) throws IOException {
        Path file = writeFile(dir, "empty.csv", "");
        assertTrue(new TransactionLoader().loadFromCsv(file).isEmpty());
    }

    @Test
    void loadFromCsv_blankLinesAreSkipped(@TempDir Path dir) throws IOException {
        Path file = writeFile(dir, "blanks.csv",
                "T001,U001,M001,500,2026-09-01T10:15\n\n   \nT002,U002,M001,800,2026-09-01T10:17\n");
        assertEquals(2, new TransactionLoader().loadFromCsv(file).size());
    }

    @Test
    void loadFromCsv_wrongFieldCount_throws(@TempDir Path dir) throws IOException {
        Path file = writeFile(dir, "bad.csv", "T001,U001,M001,500\n"); // missing timestamp
        TransactionLoader loader = new TransactionLoader();
        assertThrows(IllegalArgumentException.class, () -> loader.loadFromCsv(file));
    }

    @Test
    void loadFromCsv_invalidAmount_throws(@TempDir Path dir) throws IOException {
        Path file = writeFile(dir, "bad.csv", "T001,U001,M001,notANumber,2026-09-01T10:15\n");
        TransactionLoader loader = new TransactionLoader();
        assertThrows(IllegalArgumentException.class, () -> loader.loadFromCsv(file));
    }

    @Test
    void loadFromCsv_invalidTimestamp_throws(@TempDir Path dir) throws IOException {
        Path file = writeFile(dir, "bad.csv", "T001,U001,M001,500,not-a-date\n");
        TransactionLoader loader = new TransactionLoader();
        assertThrows(IllegalArgumentException.class, () -> loader.loadFromCsv(file));
    }

    @Test
    void loadFromCsv_invalidEntityId_throwsViaTransactionValidation(@TempDir Path dir) throws IOException {
        Path file = writeFile(dir, "bad.csv", "T001,X001,M001,500,2026-09-01T10:15\n"); // bad prefix
        TransactionLoader loader = new TransactionLoader();
        assertThrows(IllegalArgumentException.class, () -> loader.loadFromCsv(file));
    }

    @Test
    void loadFromCsv_selfTransaction_throwsViaTransactionValidation(@TempDir Path dir) throws IOException {
        Path file = writeFile(dir, "bad.csv", "T001,M001,M001,500,2026-09-01T10:15\n");
        TransactionLoader loader = new TransactionLoader();
        assertThrows(IllegalArgumentException.class, () -> loader.loadFromCsv(file));
    }

    @Test
    void loadFromCsv_duplicateTransactionIds_areBothParsed(@TempDir Path dir) throws IOException {
        // The loader's job is parsing only -- Graph is responsible for rejecting duplicates.
        Path file = writeFile(dir, "dup.csv",
                "T001,U001,M001,500,2026-09-01T10:15\n" +
                        "T001,U002,M001,800,2026-09-01T10:17\n");
        assertEquals(2, new TransactionLoader().loadFromCsv(file).size());
    }

    @Test
    void loadFromCsv_missingFile_throwsIOException() {
        TransactionLoader loader = new TransactionLoader();
        assertThrows(NoSuchFileException.class,
                () -> loader.loadFromCsv(Path.of("does/not/exist.csv")));
    }


    @Test
    void loadFromCsv_sampleTransactionsFile_matchesMasterPromptExample() throws IOException {
        CustomLinkedList<Transaction> data =
                new TransactionLoader().loadFromCsv(Path.of("data/sample_transactions.csv"));
        assertEquals(3, data.size());
        assertEquals("T001: U001 -> M001 | 500 | 2026-09-01T10:15", data.peekFirst().toString());
    }
}
