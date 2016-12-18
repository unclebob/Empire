package empire;

import java.awt.*;

public class ColorInterpolator {
  public static void interpolate(Color[] colors) {
    int end = 0;
    while (end < colors.length - 1) {
      int start = end;
      end = start + 1;
      for (; colors[end] == null; end++)
        ;
      double distance = end - start;
      ColorVector cs = new ColorVector(colors[start]);
      ColorVector cEnd = new ColorVector(colors[end]);
      ColorVector cStep = cEnd.subtract(cs).scale(distance);

      for (int i = start + 1; i < end; i++) {
        cs.addTo(cStep);
        colors[i] = cs.toColor();
      }
    }
  }
}

class ColorVector {
  public double r;
  public double g;
  public double b;

  public ColorVector(double r, double g, double b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public ColorVector(Color c) {
    r = c.getRed();
    g = c.getGreen();
    b = c.getBlue();
  }

  public Color toColor() {
    int r = (int)Math.round(this.r);
    int g = (int)Math.round(this.g);
    int b = (int)Math.round(this.b);
    return new Color(r, g, b);
  }

  void addTo(ColorVector v) {
    r += v.r;
    g += v.g;
    b += v.b;
  }

  public ColorVector subtract(ColorVector c) {
    return new ColorVector(r - c.r, g - c.g, b - c.b);
  }

  public ColorVector scale(double d) {
    return new ColorVector(r / d, g / d, b / d);
  }
}
