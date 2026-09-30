# Graph-Based Anomaly Detection for Suspicious Cash-Out Patterns in Bangla QR Transactions

**BIC2214: Data Structures & Algorithms**
Java · IntelliJ IDEA · GitHub · JavaFX

> This is an educational simulation using entirely synthetic data. It is **not** a
> production banking fraud-detection system, and it does not prove fraud, criminal
> activity, or guilt. High-scoring merchants require further review only.

---

## 1. Problem Statement

Merchant QR payment channels can be misused as an informal cash-out mechanism: a
customer pays a merchant via QR, and the merchant returns the equivalent in physical
cash. At scale, this kind of misuse leaves structural traces in the transaction network
— a merchant receiving from an unusually large or concentrated set of senders, funneling
funds to a single account, or participating in a circular flow of money that eventually
returns to its source.

This project models that scenario as a directed, weighted transaction graph and applies
two classical algorithms to surface **suspicious patterns** worth review — never a
determination of guilt.

## 2. Academic Scope: Exactly 3 Data Structures, Exactly 2 Algorithms

Per the BIC2214 project guideline, this project uses **no more than three data
structures** and **no more than two algorithms**, both implemented manually rather than
via the Java standard library.

**Data Structures**
1. **Graph** — a custom directed, weighted transaction graph (adjacency-list representation)
2. **Custom Linked List** — a hand-built singly linked list, used for graph adjacency, the queue, and every ordered collection in the project
3. **Queue** — a FIFO transaction-processing queue, built as a thin, restricted wrapper over the Custom Linked List

**Algorithms**
1. **DFS-based directed cycle detection** — recursive depth-first search with three-state (UNVISITED / VISITING / VISITED) vertex coloring
2. **Insertion Sort** — manually implemented, ranks merchants by anomaly score in descending order

No `HashMap`, `HashSet`, `ArrayList`, `Stack`, `Deque`, `PriorityQueue`, `Collections.sort()`, `Arrays.sort()`, BFS, or any other graph/sorting algorithm is used as a project-level component. A small number of framework-required exceptions exist and are documented explicitly where they occur (a local, throwaway array inside CSV line-parsing; JavaFX's own `ObservableList`, required to populate a `TableView`) — neither stores, computes, or represents project data; both are presentation/parsing plumbing, never a substitute for the three declared structures.

## 3. Anomaly Score: What It Is and Is Not

Each merchant's **Anomaly Score** is a simple, deterministic, rule-based calculation —
not a machine-learning model, not a banking risk model, and not a validated regulatory
threshold. It combines:

| Indicator | Points |
|---|---|
| High incoming transaction count (≥ 5) | +3 |
| High incoming volume (≥ 5,000) | +2 |
| Concentrated senders (≥ 3 transactions, 1 unique sender) | +2 |
| Concentrated recipient (≥ 1 outgoing transaction, 1 unique recipient) | +2 |
| Merchant appears on the detected cycle | +4 |

Resulting status: **NORMAL** (0–2) → **LOW ANOMALY** (3–5) → **MEDIUM ANOMALY** (6–8) →
**HIGH ANOMALY** (9+). All thresholds and weights are educational parameters chosen for
this project, not officially validated values.

**Known limitation, by design:** `CycleDetector` stops at the first cycle it finds. In a
graph containing multiple independent cycles, only merchants on the first-discovered
cycle receive the +4 bonus — see Section 8 (Limitations) for how this is demonstrated
directly in the test suite rather than hidden.

## 4. Architecture
Load (CSV or synthetic generator)
↓
QUEUE <- Custom Queue (FIFO)
↓
Transaction Processing
↓
GRAPH <- Custom Graph (adjacency list, built on Custom Linked List)
↓
DFS Cycle Detection <- Algorithm 1
↓
Merchant Anomaly Scoring
↓
Insertion Sort <- Algorithm 2
↓
Report <- Console (CLI) and/or JavaFX Dashboard (GUI)


Both the CLI and the GUI call the **same** `pipeline.AnalysisPipeline` class, so neither
duplicates the DSA logic. The GUI is a presentation layer only — it reads results from
the pipeline and displays them; it never computes a score, sorts a list, or detects a
cycle itself. See `pipeline/AnalysisPipeline.java` for the shared orchestration.

### Package structure 
src/main/java/
model/ Transaction, EntityType
datastructure/ Node, CustomLinkedList, CustomQueue
graph/ Vertex, Edge, Graph, VisitState
algorithm/ CycleDetector, InsertionSorter
detection/ AnomalyRecord, AnomalyStatus, MerchantAnalyzer
data/ SyntheticDataGenerator, TransactionLoader
pipeline/ AnalysisPipeline, GraphBuildResult
report/ ReportGenerator
gui/ App, Launcher, DashboardController, DashboardStats,
GraphLayout, GraphView, CycleHighlighter, MerchantTableView
Main.java CLI entry point



## 5. Dataset

The default dataset, `SyntheticDataGenerator.realistic()`, contains **239 deterministic,
synthetic transaction records** (~107 users, 15 merchants, 5 actively-used accounts). It
deliberately plants:

- 7 ordinary merchants (one sitting exactly on the high-incoming-count boundary)
- 2 suspicious high-volume hubs (many senders, one cash-out account each)
- 3 disjoint 3-vertex cycles (only the first is flagged by DFS — see Section 8)
- 1 duplicate transaction ID, to exercise the graph's duplicate-rejection logic

All data is synthetic. No real customer, merchant, or account information is used or
referenced anywhere in this project. IDs follow the pattern `U001`, `M001`, `A001`,
`T001`. Full breakdown and rationale: see `docs/dataset.md`.

## 6. How to Run

### Prerequisites
- JDK 25 (or the release configured in `pom.xml`)
- Maven (via IntelliJ, or standalone)

### Run the CLI
In IntelliJ, right-click `Main.java` → **Run**, or from a terminal at the project root:
mvn compile exec:java -Dexec.mainClass=Main 

Produces a full console report: transaction processing, graph construction, detected
cycle (if any), per-merchant analysis, and the Insertion Sort-ranked results.

### Run the GUI
In IntelliJ, right-click `gui/Launcher.java` → **Run**, or from a terminal:
Opens the JavaFX dashboard. Click **Load Demo Dataset**, then **Run Analysis**, then
use **Show Graph** or **Show Suspicious Merchants** to explore the results. **Reset**
clears the session.

> JavaFX dependencies are pinned to the `win` classifier in `pom.xml`. On macOS or
> Linux, change the classifier on all three `javafx-*` dependencies to `mac` or `linux`
> respectively.

### Run the tests 

mvn test
202 tests across model, data structures, graph, algorithms, detection, data generation,
pipeline orchestration, GUI logic, and report formatting.

## 7. Complexity Summary

Full, phase-by-phase complexity tables live in `docs/complexity.md`. Headline results:

| Component | Time | Notes |
|---|---|---|
| Queue enqueue/dequeue | O(1) | Backed by a tail-tracked Custom Linked List |
| Graph vertex lookup | O(V) | No `HashMap` — a deliberate, documented trade-off |
| DFS cycle detection | O(V + E) | Visitation state stored as an O(1) field read on each `Vertex` |
| Insertion Sort | O(n²) worst case, O(n) best case | Matches textbook Insertion Sort exactly; realized via linked-list splicing rather than array shifting |

## 8. Testing & Limitations

- **202 automated tests** cover every non-GUI class, plus the pure logic layers inside
  the GUI package (`DashboardStats`, `GraphLayout`, `CycleHighlighter`,
  `MerchantTableView.colorFor`).
- **JavaFX UI construction itself is not unit tested** — `DashboardController`,
  `GraphView`, and `MerchantTableView`'s `TableView` wiring need a running UI-testing
  framework (e.g. TestFX), which is genuine added infrastructure outside this course's
  scope. This is verified instead by a documented manual walkthrough after each GUI
  milestone.
- **DFS finds only the first cycle in a run.** The `realistic()` dataset plants three
  disjoint cycles specifically to demonstrate this limitation directly: only the first
  one discovered is flagged in scoring and in the GUI's highlighting.
- **No vertex or merchant hashing.** Every lookup (`Graph.getVertex`, merchant
  record lookup) is a linear scan, an explicit, accepted cost of staying within the
  3-data-structure limit rather than introducing `HashMap`.

Full detail: `docs/testing.md`, `docs/limitations.md`.

## 9. Academic Note

This project demonstrates the application of fundamental data structures and algorithms
to a realistic problem. It is an undergraduate coursework submission, developed and
understood individually, and is **not** intended as, and must not be represented as, a
production-grade fraud detection or banking compliance system. 
