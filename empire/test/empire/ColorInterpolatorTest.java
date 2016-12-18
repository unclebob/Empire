package empire;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class ColorInterpolatorTest {
  private Color colors[];
  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void singleStepInterpolation () throws Exception {
    colors = new Color[3];
    colors[0] = new Color(0,0,0);
    colors[2] = new Color(255, 255, 255);
    ColorInterpolator.interpolate(colors);
    assertEquals(new Color(128, 128, 128), colors[1]);
  }

  @Test
  public void multipleStepInterpolation() throws Exception {
    colors = new Color[6];
    colors[0] = new Color(0,0,0);
    colors[2] = new Color(50,60,70);
    colors[5] = new Color(100,100,100);
    ColorInterpolator.interpolate(colors);
    assertEquals(new Color(25, 30, 35), colors[1]);
    assertEquals(new Color(67,73,80), colors[3]);
    assertEquals(new Color(83,87,90), colors[4]);
  }

  @Test
  public void noGap() throws Exception {
    colors=new Color[2];
    colors[0] = new Color(0,0,0);
    colors[1] = new Color(2,2,2);
    ColorInterpolator.interpolate(colors);
    assertEquals(new Color(0, 0, 0), colors[0]);
    assertEquals(new Color(2, 2, 2), colors[1]);
  }
}