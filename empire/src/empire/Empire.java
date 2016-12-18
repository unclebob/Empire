package empire;

import processing.core.PApplet;

import java.awt.*;
import java.util.Random;

public class Empire extends PApplet {
  private Point center = new Point(0, 0);
  private int cellSize = 3;
  private Random r = new Random();
  private Color[] altitudeColor = new Color[256];

  public void settings() {
    size(displayWidth, displayHeight);
    altitudeColor[0] = new Color(0, 0, 80);
    altitudeColor[32] = new Color(32, 32, 255);
    altitudeColor[33] = new Color(0, 203, 0);
    altitudeColor[50] = new Color(232, 175, 13);
    altitudeColor[70] = new Color(153, 95, 8);
    altitudeColor[80] = new Color(144, 144, 144);
    altitudeColor[100] = new Color(255, 255, 255);
    altitudeColor[255] = new Color(255, 255, 255);
    ColorInterpolator.interpolate(altitudeColor);
  }

  public void draw() {
    background(220);
    for (int x = -50; x < 50; x++)
      for (int y = -50; y < 50; y++)
        drawMapCell(x, y);

  }

  private void drawMapCell(int x, int y) {
    rectMode(CORNER);
    noStroke();
    fill(0);
    int cellWidth = displayWidth / cellSize;
    int cellHeight = displayHeight / cellSize;
    Point originCell = new Point(cellWidth / 2, cellHeight / 2);

    int cell = getCell(x, y);
    fill(cellColor(cell).getRGB());

    rect((originCell.x + x) * cellSize, (originCell.y + y) * cellSize, cellSize, cellSize);
  }

  private int getCell(int x, int y) {
    double dx = Math.abs(x*3);
    double dy = Math.abs(y*3);
    return (int) Math.abs((Math.cos(Math.toRadians(dx)) * Math.cos(Math.toRadians(dy))*100));
  }

  private Color cellColor(int cell) {
    return altitudeColor[cell];
  }

  public static void main(String... args) {
    PApplet.main("empire.Empire");
  }
}

class Point {
  public int x, y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
}