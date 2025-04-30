package com.leedavis;

import java.util.List;

import com.leedavis.helpers.MathUtils;
import com.leedavis.helpers.Vector;

import processing.core.PGraphics;

/**
 * Represents a point in the Voronoi diagram, typically a site/generator point.
 * Stores its position on the sphere.
 */
public class PlatePoint {
  public Vector position;
  public Plate plate;
  public int side, x, y;

  public double density;

  /**
   * Constructs a VoronoiPoint with a given position.
   * 
   * @param position The 3D position vector (should be normalized for spherical
   *                 Voronoi).
   */
  public PlatePoint(Vector position, int side, int x, int y) {
    if (position == null) {
      throw new IllegalArgumentException("Position vector cannot be null.");
    }
    this.position = position;
    this.side = side;
    this.x = x;
    this.y = y;
    this.density = 1; // Default density
  }

  /**
   * Gets the position vector of this point.
   * 
   * @return The position vector.
   */
  public Vector getPosition() {
    return position;
  }

  /**
   * Displays the point on a PApplet canvas using a standard projection.
   * Assumes the point's position vector is normalized (lies on a unit sphere).
   *
   * @param canvas The PApplet canvas to draw on.
   */
  public void display(PGraphics canvas) {
    float[] screenPos = MathUtils.screenPos(position);

    canvas.strokeWeight(10);
    canvas.point(screenPos[0], screenPos[1]);

    if (screenPos[0] < 5) {
      canvas.point(screenPos[0] + canvas.width, screenPos[1]);
    }
    if (screenPos[0] > canvas.width - 5) {
      canvas.point(screenPos[0] - canvas.width, screenPos[1]);
    }
  }

  public void update() {
    List<PlatePoint> points = World.getPoints(side, x, y);

    Vector flow = MathUtils.flowField(position, Game.canvas.frameCount / 4000.0);
    plate.applyForce(position, flow);

    for (PlatePoint point : points) {
      if (point.plate == plate) {
        continue;
      }
      collideWith(point);
    }
  }

  public void collideWith(PlatePoint other) {
    Vector pos = Vector.add(this.position, other.position).div(2); // Assuming position from origin

    // Angular velocities of both plates
    Vector omega1 = plate.pole;
    Vector omega2 = other.plate.pole;

    // Velocities at the point of contact
    Vector v1 = Vector.cross(omega1, pos);
    Vector v2 = Vector.cross(omega2, pos);

    // Relative velocity
    Vector vRel = Vector.sub(v2, v1);

    other.plate.applyForce(vRel, pos);
  }

  @Override
  public String toString() {
    return "VoronoiPoint{" +
        "position=" + position +
        '}';
  }
}
