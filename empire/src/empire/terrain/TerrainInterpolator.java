package empire.terrain;

import java.util.ArrayList;
import java.util.List;

public class TerrainInterpolator {
  private double[][] array;
  private int size;
  protected double randomAmplitude;
  private double offset;

  public void interpolate(double[][] array, int size, double randomAmplitude, double offset) {
    this.array = array;
    this.size = size;
    this.randomAmplitude = randomAmplitude;
    this.offset = offset;
    if (size == 1)
      return;
    if (!isPowerOfTwo(size - 1))
      throw new BadSize();

    diamondSquare();
  }

  void interpolate(double[][] array, int size) {
    interpolate(array, size, 0, 0);
  }

  void diamondSquare() {
    for (int cellSize = size - 1; cellSize > 1; cellSize /= 2) {
      doSquares(cellSize);
      doDiamonds(cellSize);
      offset /= 2;
      randomAmplitude/=2;
    }
  }

  private void doDiamonds(int cellSize) {
    for (int x = 0; x < size - 1; x += cellSize) {
      for (int y = 0; y < size - 1; y += cellSize) {
        doDiamond(x, y, cellSize + 1);
      }
    }
  }

  private void doSquares(int cellSize) {
    for (int x = 0; x < size - 1; x += cellSize) {
      for (int y = 0; y < size - 1; y += cellSize) {
        doSquare(x, y, cellSize + 1);
      }
    }
  }

  void doSquare(int x, int y, int size) {
    int len = size - 1;
    int mid = len / 2;

    set(x + mid, y + mid, average(x, y, x + len, y, x, y + len, x + len, y + len));
  }

  void doDiamond(int x, int y, int size) {
    int len = size - 1;
    int d = len / 2;
    setDiamondPoint(x + d, y, d);
    setDiamondPoint(x, y + d, d);
    setDiamondPoint(x + d, y + len, d);
    setDiamondPoint(x + len, y + d, d);
  }

  private void setDiamondPoint(int x, int y, int d) {
    List<Integer> points = new ArrayList<>();
    addIfValid(points, x - d, y);
    addIfValid(points, x + d, y);
    addIfValid(points, x, y - d);
    addIfValid(points, x, y + d);

    Integer[] pointsArray = points.toArray(new Integer[1]);
    set(x, y, average(pointsArray));
  }

  private void addIfValid(List<Integer> points, int x, int y) {
    if (x >= 0 && x < size && y >= 0 && y < size) {
      points.add(x);
      points.add(y);
    }
  }

  void set(int x, int y, double value) {
    array[x][y]=value;
  }

  double average(Integer... points) {
    if (points.length == 0)
      return 0;
    double sum = 0;
    for (int i = 0; i < points.length; i += 2)
      sum += get(points[i], points[i + 1]);
    return sum / (points.length / 2) + random() + offset;
  }

  double random() {
    return (Math.random()+Math.random()-1)*randomAmplitude;
  }

  double get(int x, int y) {
    return array[x][y];
  }

  boolean isPowerOfTwo(int p) {
    if (p<=1)
      return false;
    int n;
    for (n=p; n%2 == 0; n/=2)
      ;
    return n==1;
  }

  public class BadSize extends RuntimeException {
  }
}
