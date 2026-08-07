# Powder4J

Powder4J is a high-performance 2D cellular automata physics engine designed for simulating granular materials, liquids, gases, and their physical interactions on a discrete grid.

## Overview

Powder4J models complex emergent physics through local cell interactions. By applying custom rules for individual elements (such as gravity, displacement, ignition, and fluid dynamics), simple particle-level behaviors combine to create rich, interactive environments in real time.

## Core Features

- **Dynamic Material Engine**: Simulates distinct physical properties for solids, fluids, gases, and reactive elements (Sand, Water, Fire, Lava, Mercury, etc.) with custom visual shading and noise filters.
- **Radial Selection Menus**:
    - **Element Wheel (`TAB`)**: Quick-access radial HUD to select materials on the fly.
    - **Shape Wheel (`ALT`)**: Radial menu to switch brush geometries (**Circle**, **Square**, **Triangle**).
    - **Clear Everything (`E`)**: Just a tiny bit more convinient than clearing by hand.
- **Customizable Brush System**: Real-time radius adjustments with visual fading HUD slider feedback and geometric shape masking.
- **Context-Aware Input Locking**: Automatic click suppression while navigating HUD menus to prevent unintentional particle spawning.
- **Fast Buffer Rendering**: Optimized direct pixel buffer strategy ensuring high frame rates even under heavy grid loads.

## Getting Started

### Prerequisites

- **Java 25** or higher
- **Maven**

### Build and Run

To build and execute Powder4J from the project root:

```bash
mvn clean compile exec:java -Dexec.mainClass="org.xdg.p4j.App"