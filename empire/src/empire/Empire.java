package empire;

import empire.terrain.TerrainInterpolator;
import processing.core.PApplet;

import java.awt.*;
import java.util.Random;

public class Empire extends PApplet {
  private Point center = new Point(0, 0);
  private int cellSize = 1;
  private Random r = new Random();
  private Color[] altitudeColor = new Color[256];
  double[][] land = new double[1025][1025];
  private TerrainInterpolator ti;

  public void settings() {
    size(displayWidth, displayHeight);
    altitudeColor[0] = new Color(0, 0, 80);
    altitudeColor[31] = new Color(32, 32, 255);
    altitudeColor[32] = new Color(200,200,90);
    altitudeColor[33] = new Color(0, 150, 0);
    altitudeColor[65] = new Color(0,100,0);
    altitudeColor[75] = new Color(120, 80, 0);
    altitudeColor[120] = new Color(50, 20, 4);
    altitudeColor[125] = new Color(50,50,50);
    altitudeColor[147] = new Color(150,150,150);
    altitudeColor[150] = new Color(255, 255, 255);
    altitudeColor[255] = new Color(255, 255, 255);
    ColorInterpolator.interpolate(altitudeColor);

    land[0][0] = 180;
    land[1024][0]=200;
    ti = new TerrainInterpolator();
    generateTerrain();
  }

  private void generateTerrain() {
    ti.interpolate(land,1025,100,-10);
  }

  public void draw() {
    background(220);
    for (int x = 0; x < 1025; x++)
      for (int y = 0; y < 1025; y++)
        drawMapCell(x, y);
  }

  public void mousePressed() {
    generateTerrain();
  }

  private void drawMapCell(int x, int y) {
    rectMode(CORNER);
    noStroke();
    fill(0);
    int cellWidth = displayWidth / cellSize;
    int cellHeight = displayHeight / cellSize;
    Point originCell = new Point(0,0);

    int cell = getCell(x, y);
    fill(cellColor(cell).getRGB());

    rect((originCell.x + x) * cellSize, (originCell.y + y) * cellSize, cellSize, cellSize);
  }

  private int getCell(int x, int y) {
    return max((int)land[x][y], 0);
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