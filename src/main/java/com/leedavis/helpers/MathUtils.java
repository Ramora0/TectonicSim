package com.leedavis.helpers;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import processing.core.PApplet;

public class MathUtils {
  private static PerlinNoiseGenerator[] noiseGenerators;

  public static void load() {
    noiseGenerators = new PerlinNoiseGenerator[3];
    for (int i = 0; i < noiseGenerators.length; i++) {
      noiseGenerators[i] = PerlinNoiseGenerator.newBuilder()
          .setInterpolation(Interpolation.CUBIC).build();
    }
  }

  public static double noise(double x, double y) {
    return noiseGenerators[0].evaluateNoise(x, y);
  }

  // ...existing code...

  public static final double FLOW_SCALE = 1;
  public static final double FLOW_SPEED = 0.00001; // You might need to adjust this scaling factor for the gradient
  public static final double EPS = 1e-4; // Epsilon for finite difference calculation

  public static Vector flowField(Vector position, double time) {
    // Use the first noise generator for the 4D field
    PerlinNoiseGenerator noiseGen = noiseGenerators[0];

    double px = position.x * FLOW_SCALE;
    double py = position.y * FLOW_SCALE;
    double pz = position.z * FLOW_SCALE;

    // Calculate partial derivatives using central differences
    double gradX = (noiseGen.evaluateNoise(px + EPS, py, pz, time) -
        noiseGen.evaluateNoise(px - EPS, py, pz, time)) / (2 * EPS);

    double gradY = (noiseGen.evaluateNoise(px, py + EPS, pz, time) -
        noiseGen.evaluateNoise(px, py - EPS, pz, time)) / (2 * EPS);

    double gradZ = (noiseGen.evaluateNoise(px, py, pz + EPS, time) -
        noiseGen.evaluateNoise(px, py, pz - EPS, time)) / (2 * EPS);

    Vector flow = new Vector(gradX, gradY, gradZ);
    flow.mult(FLOW_SPEED); // Apply scaling
    flow.sub(Vector.mult(position.copy().normalize(), Vector.dot(position.copy().normalize(), flow)));

    return flow;
  }

  // ...existing code...

  public static float[] screenPos(Vector position) {
    if (Math.abs(position.magSq() - 1) > 0.001) {
      position = Vector.normalize(position);
    }

    double longitude = Math.atan2(position.y, position.x);
    double latitude = Math.asin(position.z);

    float screenX = PApplet.map((float) longitude, -PApplet.PI, PApplet.PI, 0, 1200);
    float screenY = PApplet.map((float) latitude, -PApplet.HALF_PI, PApplet.HALF_PI, 800, 0);

    return new float[] { screenX, screenY };
  }
}
