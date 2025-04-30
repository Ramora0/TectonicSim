package com.leedavis.helpers;

public class Matrix {

  // Matrix elements (row-major order)
  public double m00, m01, m02;
  public double m10, m11, m12;
  public double m20, m21, m22;

  /**
   * Creates an identity matrix.
   */
  public Matrix() {
    setIdentity();
  }

  /**
   * Creates a rotation matrix from a rotation vector.
   * The vector's direction is the axis of rotation.
   * The vector's magnitude is the angle of rotation in radians.
   *
   * @param rotationVector The vector defining the rotation.
   */
  public Matrix(Vector rotationVector) {
    setRotation(rotationVector);
  }

  /**
   * Sets this matrix to an identity matrix.
   */
  public void setIdentity() {
    m00 = 1;
    m01 = 0;
    m02 = 0;
    m10 = 0;
    m11 = 1;
    m12 = 0;
    m20 = 0;
    m21 = 0;
    m22 = 1;
  }

  /**
   * Sets this matrix to represent a rotation defined by a rotation vector.
   * The vector's direction is the axis of rotation.
   * The vector's magnitude is the angle of rotation in radians.
   * Uses Rodrigues' rotation formula.
   * If the vector is zero, sets to identity matrix.
   *
   * @param rotationVector The vector defining the rotation.
   */
  public void setRotation(Vector rotationVector) {
    double angle = rotationVector.mag();

    Vector k = Vector.normalize(rotationVector);
    double x = k.x;
    double y = k.y;
    double z = k.z;

    double c = Math.cos(angle);
    double s = Math.sin(angle);
    double omc = 1.0 - c; // one minus cosine

    m00 = c + x * x * omc;
    m01 = x * y * omc - z * s;
    m02 = x * z * omc + y * s;

    m10 = y * x * omc + z * s;
    m11 = c + y * y * omc;
    m12 = y * z * omc - x * s;

    m20 = z * x * omc - y * s;
    m21 = z * y * omc + x * s;
    m22 = c + z * z * omc;
  }

  /**
   * Multiplies this matrix by a vector, returning a new transformed vector.
   * Does not modify the original vector.
   *
   * @param v The vector to multiply.
   */
  public void mult(Vector v) {
    double newX = m00 * v.x + m01 * v.y + m02 * v.z;
    double newY = m10 * v.x + m11 * v.y + m12 * v.z;
    double newZ = m20 * v.x + m21 * v.y + m22 * v.z;
    v.set(newX, newY, newZ);
  }

  @Override
  public String toString() {
    return "[ " + m00 + ", " + m01 + ", " + m02 + " ]\n" +
        "[ " + m10 + ", " + m11 + ", " + m12 + " ]\n" +
        "[ " + m20 + ", " + m21 + ", " + m22 + " ]";
  }
}
