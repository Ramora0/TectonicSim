package com.leedavis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.leedavis.helpers.Vector; // Import Vector

import processing.core.PGraphics;

public class World {
  public static int length;
  public static List<PlatePoint>[][][] sides; // [side][length][length]

  public static List<Plate> plates;

  // Private constructor to prevent instantiation
  private World() {
  }

  public static void update() {
    for (Plate plate : plates) {
      plate.update();
    }
    for (int s = 0; s < 6; s++) {
      for (int x = 0; x < length; x++) {
        for (int y = 0; y < length; y++) {
          for (PlatePoint point : sides[s][x][y]) {
            point.update();
          }
        }
      }
    }
  }

  /**
   * Displays all Voronoi points stored in the world on the given PApplet canvas.
   * Uses the display method of each VoronoiPoint (which uses Mercator
   * projection).
   *
   * @param canvas The PApplet canvas to draw on.
   */
  public static void display(PGraphics canvas) {
    for (Plate plate : plates) {
      plate.display(canvas);
    }
  }

  public static List<PlatePoint> getNeighbors(PlatePoint point) {
    List<PlatePoint> neighbors = new ArrayList<>();
    int s = point.side;
    int i = point.x; // Grid coordinate corresponding to 'x' or 'u'
    int j = point.y; // Grid coordinate corresponding to 'y' or 'v'
    int lm1 = length - 1; // Pre-calculate length-1

    // 1. Neighbors on the same face (Orthogonal)
    if (i > 0)
      neighbors.addAll(getPoints(s, i - 1, j)); // Left neighbor
    if (i < lm1)
      neighbors.addAll(getPoints(s, i + 1, j)); // Right neighbor
    if (j > 0)
      neighbors.addAll(getPoints(s, i, j - 1)); // Down neighbor
    if (j < lm1)
      neighbors.addAll(getPoints(s, i, j + 1)); // Up neighbor

    // 2. Neighbors on adjacent faces (wrap-around based on provided rules)
    if (s == 0) {
      if (j == lm1)
        neighbors.addAll(getPoints(5, 0, i)); // 0u -> 5l (Map i to y on face 5)
      if (i == 0)
        neighbors.addAll(getPoints(1, 0, j)); // 0l -> 1l (Map j to y on face 1)
      if (i == lm1)
        neighbors.addAll(getPoints(4, 0, j)); // 0r -> 4l (Map j to y on face 4)
      if (j == 0)
        neighbors.addAll(getPoints(2, 0, i)); // 0d -> 2l (Map i to y on face 2)
    } else if (s == 1) {
      if (j == 0)
        neighbors.addAll(getPoints(2, i, 0)); // 1d -> 2d (Map i to x on face 2)
      if (j == lm1)
        neighbors.addAll(getPoints(5, i, 0)); // 1u -> 5d (Map i to x on face 5)
      if (i == 0)
        neighbors.addAll(getPoints(0, 0, j)); // 1l -> 0l (Map j to y on face 0)
      if (i == lm1)
        neighbors.addAll(getPoints(3, 0, j)); // 1r -> 3l (Map j to y on face 3)
    } else if (s == 2) {
      if (j == 0)
        neighbors.addAll(getPoints(1, i, 0)); // 2d -> 1d (Map i to x on face 1)
      if (j == lm1)
        neighbors.addAll(getPoints(4, i, 0)); // 2u -> 4d (Map i to x on face 4)
      if (i == 0)
        neighbors.addAll(getPoints(0, j, 0)); // 2l -> 0d (Map j to x on face 0)
      if (i == lm1)
        neighbors.addAll(getPoints(3, j, 0)); // 2r -> 3d (Map j to x on face 3)
    } else if (s == 3) {
      if (j == lm1)
        neighbors.addAll(getPoints(5, lm1, i)); // 3u -> 5r (Map i to y on face 5)
      if (i == 0)
        neighbors.addAll(getPoints(1, lm1, j)); // 3l -> 1r (Map j to y on face 1)
      if (i == lm1)
        neighbors.addAll(getPoints(4, lm1, j)); // 3r -> 4r (Map j to y on face 4)
      if (j == 0)
        neighbors.addAll(getPoints(2, lm1, i)); // 3d -> 2r (Map i to y on face 2)
    } else if (s == 4) {
      if (j == 0)
        neighbors.addAll(getPoints(2, i, lm1)); // 4d -> 2u (Map i to x on face 2)
      if (j == lm1)
        neighbors.addAll(getPoints(5, i, lm1)); // 4u -> 5u (Map i to x on face 5)
      if (i == 0)
        neighbors.addAll(getPoints(0, lm1, j)); // 4l -> 0r (Map j to y on face 0)
      if (i == lm1)
        neighbors.addAll(getPoints(3, lm1, j)); // 4r -> 3r (Map j to y on face 3)
    } else if (s == 5) {
      if (j == 0)
        neighbors.addAll(getPoints(1, i, lm1)); // 5d -> 1u (Map i to x on face 1)
      if (j == lm1)
        neighbors.addAll(getPoints(4, i, lm1)); // 5u -> 4u (Map i to x on face 4)
      if (i == 0)
        neighbors.addAll(getPoints(0, j, lm1)); // 5l -> 0u (Map j to x on face 0)
      if (i == lm1)
        neighbors.addAll(getPoints(3, j, lm1)); // 5r -> 3u (Map j to x on face 3)
    }

    return neighbors;
  }

  public static List<PlatePoint> getAvailableNeighbors(PlatePoint point) {
    return getNeighbors(point).stream()
        .filter(p -> p.plate == null) // Use method reference
        .collect(Collectors.toList());
  }

  public static List<PlatePoint> getUnavailableNeighbors(PlatePoint point) {
    return getNeighbors(point).stream()
        .filter(p -> p.plate != null) // Use method reference
        .collect(Collectors.toList());
  }

  /**
   * Loads and initializes the world represented by points on the faces of a cube,
   * normalized to lie on a sphere. Each face is a square grid.
   * This method must be called before accessing any world data or methods.
   *
   * @param worldLength The number of points along each side of the square cube
   *                    faces.
   */
  public static void load(int worldLength) {
    if (worldLength <= 0) {
      throw new IllegalArgumentException("Length must be positive.");
    }
    length = worldLength; // Use the parameter name to avoid confusion with the static field
    sides = new ArrayList[6][length][length];
    initializePoints();

    plates = new ArrayList<>();
    initializePlates();

    // Delete all but two plates, and any points that are not part of a plate
    // for (int i = 0; i < plates.size(); i++) {
    // if (i > 1) {
    // plates.get(i).points.forEach(p -> p.plate = null);
    // plates.get(i).points.clear();
    // }
    // }
    // plates = plates.stream().filter(p -> p.points.size() >
    // 0).collect(Collectors.toList());

    // for (int s = 0; s < 6; s++) {
    // for (int x = 0; x < length; x++) {
    // for (int y = 0; y < length; y++) {
    // sides[s][x][y] = sides[s][x][y].stream()
    // .filter(p -> p.plate != null)
    // .collect(Collectors.toList());
    // }
    // }
    // }
  }

  /**
   * Initializes the Voronoi points by mapping grid positions on cube faces
   * to 3D space and normalizing them onto a unit sphere.
   */
  private static void initializePoints() {
    for (int s = 0; s < 6; s++) {
      for (int x = 0; x < length; x++) {
        for (int y = 0; y < length; y++) {
          sides[s][x][y] = new ArrayList<>();
          sides[s][x][y].add(new PlatePoint(generatePosition(s, x, y), s, x, y));
        }
      }
    }
  }

  public static Vector generatePosition(int side, int x, int y) {
    int xyz = side % 3;
    int p = side / 3 == 0 ? -1 : 1;
    double px = getPosition(x), py = getPosition(y);
    switch (xyz) {
      case 0:
        return new Vector(p, px, py).normalize();
      case 1:
        return new Vector(px, p, py).normalize();
      case 2:
        return new Vector(px, py, p).normalize();
    }
    return null;
  }

  private static double getPosition(int x) {
    return Math.tan(((x + Math.random()) / length - 0.5) * Math.PI / 2);
    // return 2 * ((x + 0.5) / length - 0.5);
  }

  public static int[] getGridCoordinatesFromPosition(Vector pos) {
    double absX = Math.abs(pos.x), absY = Math.abs(pos.y), absZ = Math.abs(pos.z);

    int side;
    double u, v; // Projected coordinates on the cube face before inverse tan

    if (absX >= absY && absX >= absZ) {
      side = (pos.x > 0) ? 3 : 0;
      u = pos.y / absX;
      v = pos.z / absX;
    } else if (absY >= absX && absY >= absZ) { // Point lies primarily on face 1 or 4 (Y-axis)
      side = (pos.y > 0) ? 4 : 1;
      u = pos.x / absY;
      v = pos.z / absY;
    } else {
      side = (pos.z > 0) ? 5 : 2;
      u = pos.x / absZ;
      v = pos.y / absZ;
    }

    int x = (int) Math.max(0, Math.min(length - 1, Math.round((u + 1) / 2 * length - 0.5)));
    int y = (int) Math.max(0, Math.min(length - 1, Math.round((v + 1) / 2 * length - 0.5)));

    return new int[] { side, x, y };
  }

  public static void gridAlign(PlatePoint point) {
    // Calculate the grid coordinates that the point's current position maps to.
    int[] correctCoords = getGridCoordinatesFromPosition(point.position);
    int correctSide = correctCoords[0];
    int correctX = correctCoords[1];
    int correctY = correctCoords[2];

    if (point.side == correctSide && point.x == correctX && point.y == correctY) {
      return;
    }

    sides[point.side][point.x][point.y].remove(point); // Remove from old position
    sides[correctSide][correctX][correctY].add(point); // Add to new position

    point.side = correctSide; // Update the side
    point.x = correctX; // Update the x coordinate
    point.y = correctY; // Update the y coordinate
  }

  private static void initializePlates() {
    int numPlates = 15;
    double size = length * length;

    for (int i = 0; i < numPlates; i++) {
      PlatePoint seed = null;
      int count = 0;
      while ((seed == null || seed.plate != null) && count < 500) {
        seed = sides[(int) (Math.random() * 6)][(int) (Math.random() * length)][(int) (Math.random() * length)].get(0);

        if (seed.plate == null && World.getAvailableNeighbors(seed).isEmpty()) {
          World.getNeighbors(seed).get(0).plate.addPoint(seed);
          seed = null;
          System.out.println("Filling seed...");
        }

        count++;
      }

      if (seed == null) {
        break;
      }

      Plate plate = new Plate();
      plate.generateIteratively(seed, (int) size);
      plates.add(plate);

      size *= 5 / 6.;
    }

    fillRemainingPoints();
  }

  /**
   * Iteratively assigns remaining unassigned points to the plate of one of their
   * neighbors until no more assignments can be made.
   */
  private static void fillRemainingPoints() {
    boolean assignedPointThisIteration;
    int count = 0;
    do {
      assignedPointThisIteration = false;
      for (int s = 0; s < 6; s++) {
        for (int x = 0; x < length; x++) {
          for (int y = 0; y < length; y++) {
            PlatePoint point = sides[s][x][y].get(0);
            if (point.plate == null) {
              List<PlatePoint> neighborsWithPlates;
              if (!(neighborsWithPlates = World.getUnavailableNeighbors(point)).isEmpty()) {
                // Randomly select a neighbor with a plate
                PlatePoint chosenNeighbor = neighborsWithPlates
                    .get((int) (Math.random() * neighborsWithPlates.size()));
                chosenNeighbor.plate.addPoint(point); // Assign point to the chosen plate
                assignedPointThisIteration = true;
                count++;
              }
            }
          }
        }
      }
    } while (assignedPointThisIteration);
    System.out.println("Assigned " + count + " points to plates.");
  }

  /**
   * Gets the VoronoiPoint at the specified side and grid coordinates.
   *
   * @param side The index of the cube face (0-5).
   * @param i    The first grid index (0 to length-1).
   * @param j    The second grid index (0 to length-1).
   * @return The VoronoiPoint at the specified location.
   */
  public static List<PlatePoint> getPoints(int side, int i, int j) {
    return sides[side][i][j];
  }
}
