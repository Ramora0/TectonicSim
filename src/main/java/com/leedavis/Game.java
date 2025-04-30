package com.leedavis;

import com.leedavis.helpers.MathUtils;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class Game extends PApplet {
  public static final int PIXEL_SIZE = 4;
  public static boolean[] kp;
  public static World world;

  public static PApplet canvas;
  public static PGraphics projection;
  public static PShader shader;
  public static boolean useShader = false;

  public void settings() {
    size(1200, 800, P2D);

    canvas = this;
  }

  public void setup() {
    kp = new boolean[128];

    MathUtils.load();
    World.load(50);

    background(255);

    // Load sphere.frag
    shader = loadShader("sphere.frag");
    shader.set("resolution", (float) width, (float) height);
  }

  public void draw() {
    World.update();

    projection = createGraphics(1200, 800, P2D);
    projection.beginDraw();
    World.display(projection);

    // projection.background(0);
    // projection.stroke(255);
    // for (int side = 0; side < 6; side++) {
    // for (int x = 0; x < World.length; x += 3) {
    // for (int y = 0; y < World.length; y += 3) {
    // Vector pos = World.generatePosition(side, x, y);
    // float[] base = MathUtils.screenPos(pos);
    // pos.add(MathUtils.flowField(pos, this.frameCount / 1000.0).mult(10000));
    // float[] to = MathUtils.screenPos(pos);

    // if (Math.abs(to[0] - base[0]) > 100) {
    // continue;
    // }

    // projection.line(base[0], base[1], to[0], to[1]);
    // projection.circle(base[0], base[1], 3);
    // }
    // }
    // }

    projection.endDraw();

    background(0);
    image(projection, 0, 0);
    // World.display(this);

    // println(frameRate);

    if (useShader) {
      shader.set("rotation", map(mouseX, 0, width, PI, -PI), map(mouseY, 0, height, PI / 2, -PI / 2));
      shader(shader);
      // resetShader();
    } else {
      // World.display(this);
      resetShader();
    }
  }

  public void keyPressed() {
    if (key == ' ') {
      useShader = !useShader;
    }
  }

  public void keyReleased() {
  }

  public void mousePressed() {
  }

  public static void main(String[] passedArgs) {
    String[] processingArgs = { "Game" };
    Game mySketch = new Game();
    PApplet.runSketch(processingArgs, mySketch);
  }
}
