# Graph Algo Visualizer

Java-based graph algorithm visualizer focused on step-by-step simulation for learning and demonstration.

## Project Goal

Build a clean architecture where algorithms produce **steps/events**, and the GUI plays those steps as animations.

This keeps algorithm logic independent from rendering logic.

## Current Status

### Implemented

- Core graph model:
	- `Node`
	- `Edge`
	- `Position`
	- `Graph` (directed/undirected support)
- Step/event system:
	- `StepType`
	- `AlgorithmStep`
- Algorithm:
	- `BFS` with result object and step generation
- Unit tests for:
	- model classes
	- step classes
	- BFS

### Planned

- `DFS`
- `Dijkstra`
- `Edmonds-Karp` (max flow)
- JavaFX GUI with:
	- graph drawing
	- algorithm dropdown
	- run/pause/step controls
	- speed slider

## Architecture

### Key Design Rule

Algorithms do **not** draw anything.

They only output a list of `AlgorithmStep` entries, e.g.:

- `VISIT_NODE`
- `SET_PARENT`
- `RELAX_EDGE`
- `UPDATE_FLOW`
- `DONE`

The future GUI reads those steps and animates them.

### Package Structure

```
src/main/java/algorithm_visualizer/
	algorithms/
		BFS.java
		DFS.java
		Dijkstra.java
		EdmondsKarp.java
	model/
		Graph.java
		Node.java
		Edge.java
		Position.java
	steps/
		StepType.java
		AlgorithmStep.java
```

## Getting Started

### Requirements

- Java 25
- Maven 3.8+

### Run tests

```bash
mvn test
```

### Build

```bash
mvn clean package
```

## Example: BFS Usage

```java
Graph graph = new Graph(true);
graph.addNode(0, new Position(0, 0));
graph.addNode(1, new Position(1, 1));
graph.addNode(2, new Position(2, 2));
graph.addEdge(0, 1, 1.0);
graph.addEdge(1, 2, 1.0);

BFS.BfsResult result = BFS.run(graph, 0, 2);
System.out.println(result.isFound());
System.out.println(result.getSteps());
```

## Roadmap

1. Finalize DFS with step generation
2. Implement Dijkstra (`RELAX_EDGE`, shortest path reconstruction)
3. Implement Edmonds-Karp (`UPDATE_FLOW`, residual updates)
4. Build JavaFX canvas + controls
5. Add import/export graph JSON


## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).