# 3D Rendering in Java

This project is a 3D rendering engine constructed completely from scratch using Java. It currently includes a user-interactive 3D cube that can be rotated with mouse movements. The project utilizes Java's GUI capabilities to render a cube pixel by pixel on a `BufferedImage`, with visibility calculated using a Z-buffer algorithm and cross-product formula. 

## How It Works

1. **Rendering**: Each pixel of the cube is rendered on a `BufferedImage` using a Z-buffer algorithm. The algorithm checks if a pixel should be visible by comparing it with the depth of other pixels.
  
2. **Visibility Calculation**: Visibility is determined by the cross-product formula, which checks whether a pixel is on the same side as the triangle formed by the cube's vertex points.

3. **Rotation**: The cube is rotated by applying matrix multiplication to 3D vertex points. A transformation matrix, calculated from the user's mouse movements, rotates the points across the x and y axes.

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/SiddharthUmachandran/3D-rendering-java.git
    ```

2. Navigate to the project directory:
    ```bash
    cd 3D-rendering-java
    ```

3. Compile and run the project using your preferred Java IDE or command line:
    ```bash
    javac RenderingEngine.java
    java RenderingEngine
    ```

## Usage

- **Rotate the Cube**: Click and drag the mouse across the screen to rotate the cube.
- **Future Controls**: Additional keyboard controls will be added for enhanced interactivity.



