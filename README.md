# Graph Algorithm Visualizer

An interactive Java-based graph algorithm visualizer with step-by-step animation for learning and demonstration purposes.

![Java](https://img.shields.io/badge/Java-25-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-21-green)
![Maven](https://img.shields.io/badge/Maven-3.8+-orange)

## Features

- **Interactive Graph Editing**: Create and modify graphs using mouse interactions
  - Left-click to add nodes
  - Drag nodes to reposition them
  - Right-click context menu for edge creation and node deletion
- **Multiple Algorithms**:
  - **BFS** (Breadth-First Search) - Level-by-level traversal
  - **DFS** (Depth-First Search) - Depth-first traversal with backtracking
  - **Dijkstra** - Shortest path with weighted edges
  - **Edmonds-Karp** - Maximum flow computation
- **Animation Controls**:
  - Play/Pause continuous playback
  - Step forward/backward through algorithm execution
  - Adjustable playback speed
- **Visual Feedback**:
  - Color-coded node states (visited, queued, current, source, sink)
  - Edge highlighting for paths and flow
  - Real-time step counter and log

## Architecture

### Key Design Principle

Algorithms produce **steps/events**, and the GUI animates them independently. This separation keeps algorithm logic pure and testable.

### Step Types

| Step Type | Description |
|-----------|-------------|
| `VISIT_NODE` | Mark a node as visited |
| `SET_PARENT` | Set parent in traversal tree |
| `RELAX_EDGE` | Update distance (Dijkstra) |
| `UPDATE_FLOW` | Augment flow (Edmonds-Karp) |
| `DONE` | Algorithm completed |

### Package Structure

```
src/main/java/algorithm_visualizer/
├── algorithms/          # Algorithm implementations
│   ├── BFS.java
│   ├── DFS.java
│   ├── Dijkstra.java
│   └── EdmondsKarp.java
├── model/               # Graph data structures
│   ├── Graph.java
│   ├── Node.java
│   ├── Edge.java
│   └── Position.java
├── steps/               # Step/event system
│   ├── StepType.java
│   └── AlgorithmStep.java
└── gui/                 # JavaFX visualization
    ├── AlgorithmVisualizerApp.java
    ├── MainController.java
    ├── animation/
    │   ├── NodeStyle.java
    │   └── StepPlayer.java
    ├── components/
    │   ├── GraphCanvas.java
    │   ├── AlgorithmSelector.java
    │   ├── ControlPanel.java
    │   └── InfoPanel.java
    └── interaction/
        ├── GraphEditor.java
        └── NodeDragHandler.java
```

## Getting Started

### Requirements

- Java 25
- Maven 3.8+

### Run the Application

```bash
mvn javafx:run
```

### Run Tests

```bash
mvn test
```

### Build

```bash
mvn clean package
```

## Usage Example

### Programmatic Usage

```java
Graph graph = new Graph(true);  // directed graph
graph.addNode(0, new Position(0, 0));
graph.addNode(1, new Position(100, 100));
graph.addNode(2, new Position(200, 200));
graph.addEdge(0, 1, 2.0);
graph.addEdge(1, 2, 3.0);

// Run BFS
BFS.BfsResult bfsResult = BFS.run(graph, 0, 2);
System.out.println("Found: " + bfsResult.isFound());
System.out.println("Steps: " + bfsResult.getSteps().size());

// Run Dijkstra
Dijkstra.DijkstraResult dijkstraResult = Dijkstra.run(graph, 0, 2);
System.out.println("Distance: " + dijkstraResult.getDistance().get(2));

// Run Max Flow
EdmondsKarp.EdmondsKarpResult flowResult = EdmondsKarp.run(graph, 0, 2);
System.out.println("Max Flow: " + flowResult.getMaxFlow());
```

### GUI Usage

1. Launch the application with `mvn javafx:run`
2. The default sample graph is loaded automatically
3. Select an algorithm from the dropdown
4. Enter start node (and target/sink if needed)
5. Click **Run** to start animation or **Step** for manual control
6. Use the speed slider to adjust playback rate

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).