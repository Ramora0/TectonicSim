package com.leedavis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.leedavis.helpers.MathUtils;
import com.leedavis.helpers.Matrix;
import com.leedavis.helpers.Vector;

import processing.core.PGraphics;

public class Plate {
  List<PlatePoint> points;
  double inertia;
  int color;

  Vector pole;
  Matrix rotationMatrix;

  boolean continental = false;

  public Plate() {
    points = new ArrayList<>();

    color = Game.canvas.color(
        (int) (Math.random() * 255),
        (int) (Math.random() * 255),
        (int) (Math.random() * 255));

    // pole = Vector.random3D().mult(1. / 1000);
    pole = new Vector();
  }

  public void update() {
    calculatePole();
    for (PlatePoint point : points) {
      rotationMatrix.mult(point.position);
      if (point.position.magSq() > 1.001) {
        point.position.normalize();
      }
      World.gridAlign(point);
    }
  }

  public void display(PGraphics canvas) {
    canvas.strokeWeight(10);
    canvas.stroke(color);

    for (PlatePoint point : points) {
      point.display(canvas);
    }

    float[] screenPos = MathUtils.screenPos(pole);
    canvas.stroke(255);
    canvas.fill(color);
    canvas.strokeWeight(1);
    canvas.circle(screenPos[0], screenPos[1], 10);
  }

  public void applyForce(Vector pos, Vector F) {
    Vector tau = Vector.cross(pos, F).div(inertia);

    pole.add(tau);
  }

  public void calculatePole() {
    this.inertia = 0;
    Vector poleN = Vector.normalize(pole);
    for (PlatePoint point : points) {
      inertia += Math.sqrt(1 - Vector.dot(poleN, point.position)) * point.density;
    }

    this.rotationMatrix = new Matrix(pole);
  }

  public void addPoint(PlatePoint point) {
    points.add(point);
    point.plate = this;
  }

  public boolean contains(PlatePoint point) {
    return points.contains(point);
  }

  // Inner class to store candidate information
  private static class CandidateInfo {
    double distance;
    int count;

    CandidateInfo(double distance) {
      this.distance = distance;
      this.count = 1;
    }

    /**
     * Evaluates the weight of this candidate.
     * Higher weight for closer points and points reached multiple times.
     * Weight decreases smoothly to zero as distance approaches maxDist.
     *
     * @param maxDist The maximum distance to consider for non-zero weight.
     * @return The calculated weight.
     */
    double evaluate(double maxDist) {
      // double distanceFactor = Math.pow(distance / maxDist, 6);
      if (distance > maxDist) {
        return 0;
      }

      // --- Count Factor ---
      // Increase weight significantly for higher counts.
      // Using a quadratic increase: count^2
      // count=1 -> 1, count=2 -> 4, count=3 -> 9, count=4 -> 16
      double countFactor = Math.pow((4 - count), 2);

      // --- Combined Weight ---
      // Multiply the factors together.
      return 1 / (countFactor + 1e-6);
    }

    void incrementCount() {
      this.count++;
    }
  }

  public void generateIteratively(PlatePoint seed, int size) {
    addPoint(seed);

    double maxDist = Math.sqrt(size) / (World.length) * Math.PI / 3;

    Map<PlatePoint, CandidateInfo> candidates = new HashMap<>();

    // Initialize candidates with neighbors of the seed
    for (PlatePoint neighbor : World.getAvailableNeighbors(seed)) {
      double dist = Vector.dist(seed.position, neighbor.position);
      candidates.put(neighbor, new CandidateInfo(dist));
    }

    // Limit the number of iterations to prevent infinite loops
    // int maxIterations = World.length * World.length; // Example limit based on
    // total points
    int iterations = 0;

    Random random = new Random();

    while (!candidates.isEmpty() && iterations < size) {
      PlatePoint selectedPoint = null;
      // If any candidate has a count of 4 or more, immediately fill it and skip the
      // rest
      for (Map.Entry<PlatePoint, CandidateInfo> entry : candidates.entrySet()) {
        if (entry.getValue().count >= 4) {
          selectedPoint = entry.getKey();
          break;
        }
      }

      if (selectedPoint == null) {
        // Calculate total weight based on evaluation
        double totalWeight = 0.0;
        for (CandidateInfo info : candidates.values()) {
          totalWeight += info.evaluate(maxDist);
        }

        if (totalWeight == 0) {
          break;
        }

        // Select a random point based on weighted probability
        double randomValue = random.nextDouble() * totalWeight;
        for (Map.Entry<PlatePoint, CandidateInfo> entry : candidates.entrySet()) {
          double weight = entry.getValue().evaluate(maxDist);
          if (weight <= 0)
            continue; // Skip candidates with zero or negative weight

          if (randomValue <= weight) {
            selectedPoint = entry.getKey();
            break;
          }
          randomValue -= weight;
        }
      }

      if (selectedPoint == null) {
        break; // No selectable candidate found
      }

      addPoint(selectedPoint);
      candidates.remove(selectedPoint);

      // Add neighbors of the newly added point to the candidates or update count
      List<PlatePoint> newNeighbors = World.getAvailableNeighbors(selectedPoint);
      for (PlatePoint newNeighbor : newNeighbors) {
        if (!points.contains(newNeighbor)) { // Only consider if not already in the plate
          if (candidates.containsKey(newNeighbor)) { // If neighbor is already a candidate, increment its count
            candidates.get(newNeighbor).incrementCount();
          } else { // If it's a new candidate, add it
            double dist = Vector.dist(seed.position, newNeighbor.position); // Distance from original seed
            candidates.put(newNeighbor, new CandidateInfo(dist));
          }
        }
      }
      iterations++;
    }
  }
}
