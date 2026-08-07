# Powder4J

Powder4J is a streamlined 2D physics engine designed for the simulation of diverse materials and their physical interactions within a discrete grid.

## Overview

This application simulates the behavior of various substances—such as sand, water, and fire—in a two-dimensional environment. By using cellular automata principles, each particle interacts with its neighbors based on specific physical rules, enabling complex emergent phenomena from simple local interactions.

## Core Features

- **Material Simulation**: Distinct behaviors for different elements (Sand, Water, Fire, etc.).
- **Interactive Environment**: Users can manipulate the simulation in real-time using a mouse-driven brush.
- **Efficient Rendering**: A high-performance rendering pipeline ensures a smooth visual experience even with many active particles.
- **Dynamic Physics**: Elements react to gravity, obstacles, and each other, creating a living simulation.

## Getting Started

### Prerequisites

- Java 25 or higher
- Maven

### Execution

To build and run the application, execute the following command in the project root:

```bash
mvn clean compile exec:java -Dexec.mainClass="org.xdg.p4j.App"
```

## Controls

- **Left-Click**: Place the currently selected element.
- **Right-Click**: Erase elements (set to Air).
- **Mouse Wheel**: Adjust the brush radius.
