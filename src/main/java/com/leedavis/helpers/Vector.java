package com.leedavis.helpers;

import java.util.Objects;
import java.util.Random;

public class Vector {

  public double x;
  public double y;
  public double z;

  private static Random random; // For random3D

  /**
   * Constructor for a 3D vector.
   *
   * @param x the x coordinate.
   * @param y the y coordinate.
   * @param z the z coordinate.
   */
  public Vector(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Constructor for a 3D vector from an array.
   * 
   * @param arr the array containing the x, y, z coordinates.
   */
  public Vector(double[] arr) {
    if (arr.length != 3) {
      throw new IllegalArgumentException("Array must have exactly 3 elements.");
    }
    this.x = arr[0];
    this.y = arr[1];
    this.z = arr[2];
  }

  /**
   * Constructor for a 3D vector with coordinates (0,0,0).
   */
  public Vector() {
    this(0, 0, 0);
  }

  // ==========================================================================
  // Instance Methods (Modify the vector)
  // ==========================================================================

  /**
   * Set the x, y, and z components of the vector.
   *
   * @param x the x coordinate.
   * @param y the y coordinate.
   * @param z the z coordinate.
   * @return this vector.
   */
  public Vector set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }

  /**
   * Set the components of the vector from another vector.
   *
   * @param v the vector to copy components from.
   * @return this vector.
   */
  public Vector set(Vector v) {
    return set(v.x, v.y, v.z);
  }

  /**
   * Adds a vector to this vector.
   *
   * @param v the vector to add.
   * @return this vector.
   */
  public Vector add(Vector v) {
    this.x += v.x;
    this.y += v.y;
    this.z += v.z;
    return this;
  }

  /**
   * Adds specified components to this vector.
   *
   * @param x x component to add.
   * @param y y component to add.
   * @param z z component to add.
   * @return this vector.
   */
  public Vector add(double x, double y, double z) {
    this.x += x;
    this.y += y;
    this.z += z;
    return this;
  }

  /**
   * Subtracts a vector from this vector.
   *
   * @param v the vector to subtract.
   * @return this vector.
   */
  public Vector sub(Vector v) {
    this.x -= v.x;
    this.y -= v.y;
    this.z -= v.z;
    return this;
  }

  /**
   * Subtracts specified components from this vector.
   *
   * @param x x component to subtract.
   * @param y y component to subtract.
   * @param z z component to subtract.
   * @return this vector.
   */
  public Vector sub(double x, double y, double z) {
    this.x -= x;
    this.y -= y;
    this.z -= z;
    return this;
  }

  /**
   * Multiplies this vector by a scalar.
   *
   * @param n the scalar to multiply by.
   * @return this vector.
   */
  public Vector mult(double n) {
    this.x *= n;
    this.y *= n;
    this.z *= n;
    return this;
  }

  /**
   * Divides this vector by a scalar.
   *
   * @param n the scalar to divide by.
   * @return this vector.
   * @throws IllegalArgumentException if n is 0.
   */
  public Vector div(double n) {
    if (n == 0) {
      throw new IllegalArgumentException("Cannot divide by zero.");
    }
    this.x /= n;
    this.y /= n;
    this.z /= n;
    return this;
  }

  /**
   * Calculates the squared magnitude of this vector.
   *
   * @return the squared magnitude.
   */
  public double magSq() {
    return (x * x) + (y * y) + (z * z);
  }

  /**
   * Calculates the magnitude (length) of this vector.
   *
   * @return the magnitude.
   */
  public double mag() {
    return Math.sqrt(magSq());
  }

  /**
   * Normalizes the vector to length 1 (making it a unit vector).
   * Modifies the vector directly.
   *
   * @return this vector.
   */
  public Vector normalize() {
    double m = mag();
    if (m != 0 && m != 1) {
      div(m);
    }
    return this;
  }

  /**
   * Limits the magnitude of this vector.
   *
   * @param max the maximum magnitude.
   * @return this vector.
   */
  public Vector limit(double max) {
    if (magSq() > max * max) {
      normalize();
      mult(max);
    }
    return this;
  }

  /**
   * Calculates the dot product between this vector and another vector.
   *
   * @param v the other vector.
   * @return the dot product.
   */
  public double dot(Vector v) {
    return x * v.x + y * v.y + z * v.z;
  }

  /**
   * Calculates the cross product between this vector and another vector.
   * Modifies this vector to store the result.
   *
   * @param v the other vector.
   * @return this vector (now containing the cross product).
   */
  public Vector cross(Vector v) {
    double crossX = y * v.z - z * v.y;
    double crossY = z * v.x - x * v.z;
    double crossZ = x * v.y - y * v.x;
    return set(crossX, crossY, crossZ);
  }

  /**
   * Calculates the Euclidean distance between this vector and another vector.
   *
   * @param v the other vector.
   * @return the distance.
   */
  public double dist(Vector v) {
    double dx = x - v.x;
    double dy = y - v.y;
    double dz = z - v.z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  /**
   * Calculates the angle (in radians) between this vector and another vector.
   *
   * @param v the other vector.
   * @return the angle in radians (0 to PI). Returns NaN if either vector is zero.
   */
  public double angleBetween(Vector v) {
    double dot = dot(v);
    double magProduct = mag() * v.mag();
    if (magProduct == 0)
      return Double.NaN; // Or throw exception / return 0?
    // Clamp dot / magProduct to [-1, 1] due to potential floating point errors
    double cosTheta = Math.max(-1.0, Math.min(1.0, dot / magProduct));
    return Math.acos(cosTheta);
  }

  /**
   * Linearly interpolates the vector to another vector.
   * Modifies this vector.
   *
   * @param target the target vector.
   * @param amt    the amount of interpolation (0.0 is this vector, 1.0 is target
   *               vector).
   * @return this vector.
   */
  public Vector lerp(Vector target, double amt) {
    this.x = lerp(this.x, target.x, amt);
    this.y = lerp(this.y, target.y, amt);
    this.z = lerp(this.z, target.z, amt);
    return this;
  }

  /**
   * Linearly interpolates between two values.
   * Helper function for lerp(Vector, double).
   */
  private static double lerp(double start, double stop, double amt) {
    return start + (stop - start) * amt;
  }

  /**
   * Creates a copy of this vector.
   *
   * @return a new Vector object with the same x, y, z components.
   */
  public Vector copy() {
    return new Vector(x, y, z);
  }

  // ==========================================================================
  // Static Methods (Return new Vector or value)
  // ==========================================================================

  /**
   * Adds two vectors.
   *
   * @param v1 the first vector.
   * @param v2 the second vector.
   * @return a new Vector representing the sum.
   */
  public static Vector add(Vector v1, Vector v2) {
    return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
  }

  /**
   * Subtracts one vector from another.
   *
   * @param v1 the first vector.
   * @param v2 the vector to subtract from v1.
   * @return a new Vector representing the difference.
   */
  public static Vector sub(Vector v1, Vector v2) {
    return new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
  }

  /**
   * Multiplies a vector by a scalar.
   *
   * @param v the vector.
   * @param n the scalar.
   * @return a new Vector representing the scaled vector.
   */
  public static Vector mult(Vector v, double n) {
    return new Vector(v.x * n, v.y * n, v.z * n);
  }

  /**
   * Divides a vector by a scalar.
   *
   * @param v the vector.
   * @param n the scalar.
   * @return a new Vector representing the divided vector.
   * @throws IllegalArgumentException if n is 0.
   */
  public static Vector div(Vector v, double n) {
    if (n == 0) {
      throw new IllegalArgumentException("Cannot divide by zero.");
    }
    return new Vector(v.x / n, v.y / n, v.z / n);
  }

  /**
   * Calculates the dot product of two vectors.
   *
   * @param v1 the first vector.
   * @param v2 the second vector.
   * @return the dot product.
   */
  public static double dot(Vector v1, Vector v2) {
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
  }

  /**
   * Calculates the cross product of two vectors.
   *
   * @param v1 the first vector.
   * @param v2 the second vector.
   * @return a new Vector representing the cross product (v1 x v2).
   */
  public static Vector cross(Vector v1, Vector v2) {
    double crossX = v1.y * v2.z - v1.z * v2.y;
    double crossY = v1.z * v2.x - v1.x * v2.z;
    double crossZ = v1.x * v2.y - v1.y * v2.x;
    return new Vector(crossX, crossY, crossZ);
  }

  /**
   * Calculates the Euclidean distance between two vectors.
   *
   * @param v1 the first vector.
   * @param v2 the second vector.
   * @return the distance.
   */
  public static double dist(Vector v1, Vector v2) {
    double dx = v1.x - v2.x;
    double dy = v1.y - v2.y;
    double dz = v1.z - v2.z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  public static Vector normalize(Vector v) {
    double m = v.mag();
    if (m != 0 && m != 1) {
      return div(v, m);
    }
    return v;
  }

  /**
   * Calculates the angle (in radians) between two vectors.
   *
   * @param v1 the first vector.
   * @param v2 the second vector.
   * @return the angle in radians (0 to PI). Returns NaN if either vector is zero.
   */
  public static double angleBetween(Vector v1, Vector v2) {
    double dot = dot(v1, v2);
    double magProduct = v1.mag() * v2.mag();
    if (magProduct == 0)
      return Double.NaN;
    double cosTheta = Math.max(-1.0, Math.min(1.0, dot / magProduct));
    return Math.acos(cosTheta);
  }

  /**
   * Linearly interpolates between two vectors.
   *
   * @param start the starting vector.
   * @param end   the ending vector.
   * @param amt   the amount of interpolation (0.0 is start, 1.0 is end).
   * @return a new Vector representing the interpolated vector.
   */
  public static Vector lerp(Vector start, Vector end, double amt) {
    double lerpX = lerp(start.x, end.x, amt);
    double lerpY = lerp(start.y, end.y, amt);
    double lerpZ = lerp(start.z, end.z, amt);
    return new Vector(lerpX, lerpY, lerpZ);
  }

  /**
   * Creates a new random 3D unit vector.
   * Uses spherical coordinates method for uniform distribution.
   *
   * @return a new Vector with random x, y, z components and magnitude 1.
   */
  public static Vector random3D() {
    if (random == null) {
      random = new Random();
    }
    // Using spherical coordinates for uniform distribution on sphere surface
    double u = random.nextDouble() * 2.0 - 1.0; // z component, uniform in [-1, 1]
    double theta = random.nextDouble() * 2.0 * Math.PI; // angle in xy plane

    double factor = Math.sqrt(1.0 - u * u); // radius in xy plane
    double x = factor * Math.cos(theta);
    double y = factor * Math.sin(theta);
    double z = u;

    // Construct and normalize (should already be close to 1, but normalize for
    // precision)
    return new Vector(x, y, z).normalize();
  }

  // ==========================================================================
  // Overrides
  // ==========================================================================

  @Override
  public String toString() {
    return "[ " + x + ", " + y + ", " + z + " ]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Vector vector = (Vector) o;
    return Double.compare(vector.x, x) == 0 &&
        Double.compare(vector.y, y) == 0 &&
        Double.compare(vector.z, z) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }

  // ==========================================================================
  // Getters (Setters are covered by set() methods)
  // ==========================================================================

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }
}
