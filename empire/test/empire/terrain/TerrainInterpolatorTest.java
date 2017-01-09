package empire.terrain;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class TerrainInterpolatorTest {
  private String actions = "";
  private double[][] dummy;
  private TerrainInterpolator interpolator;

  public class SimpleValidations {
    @Before
    public void setUp() throws Exception {
      dummy = new double[1][1];
      interpolator = new TerrainInterpolatorSpy();
    }

    @Test
    public void terminalCondition_sizeOne() throws Exception {
      interpolator.interpolate(dummy, 1);
      assertThat(actions, isEmptyString());
    }

    @Test(expected = TerrainInterpolator.BadSize.class)
    public void sizeMustBePowerOfTwoPlus1() throws Exception {
      interpolator.interpolate(dummy, 2);
    }

    @Test
    public void Check_isPowerOfTwo() throws Exception {
      assertThat(interpolator.isPowerOfTwo(2), is(true));
      assertThat(interpolator.isPowerOfTwo(4), is(true));
      assertThat(interpolator.isPowerOfTwo(8), is(true));

      assertThat(interpolator.isPowerOfTwo(1), is(false));
      assertThat(interpolator.isPowerOfTwo(7), is(false));
      assertThat(interpolator.isPowerOfTwo(18), is(false));
    }

    public class SquareDiamondCoordinateCalculations {
      @Test
      public void simpleThreeByThree_SquarePass() throws Exception {
        interpolator.interpolate(dummy, 3);
        assertThat(actions, startsWith("Square(0,0,3): A([0,0],[2,0],[0,2],[2,2])->[1,1]."));
      }

      @Test
      public void simpleThreeByThree_DiamondPass() throws Exception {
        interpolator.interpolate(dummy, 3);
        assertThat(actions, endsWith("Diamond(0,0,3): " +
          "A([0,0],[2,0],[1,1])->[1,0]. " +
          "A([1,1],[0,0],[0,2])->[0,1]. " +
          "A([0,2],[2,2],[1,1])->[1,2]. " +
          "A([1,1],[2,0],[2,2])->[2,1]. "));
      }

      @Test
      public void DiamondSquare_FirstPass() throws Exception {
        interpolator.interpolate(dummy, 5);
        assertThat(actions, startsWith(
          "Square(0,0,5): A([0,0],[4,0],[0,4],[4,4])->[2,2]. " +
            "Diamond(0,0,5): " +
            "A([0,0],[4,0],[2,2])->[2,0]. " +
            "A([2,2],[0,0],[0,4])->[0,2]. " +
            "A([0,4],[4,4],[2,2])->[2,4]. " +
            "A([2,2],[4,0],[4,4])->[4,2]. "));
      }

    }

    public class SquareDiamondRepetition {
      @Before
      public void setup() {
        dummy = new double[1][1];
        interpolator = new TerrainInterpolatorDiamondSquareSpy();
      }

      @Test
      public void FiveByFive() throws Exception {
        interpolator.interpolate(dummy, 5);
        assertThat(actions, is(
          "" +
            "Square(0,0,5) Diamond(0,0,5) " +
            "Square(0,0,3) Square(0,2,3) Square(2,0,3) Square(2,2,3) " +
            "Diamond(0,0,3) Diamond(0,2,3) Diamond(2,0,3) Diamond(2,2,3) "
        ));
      }
    }

    public class Averages {
      @Before
      public void setup() {
        dummy = new double[3][3];
        interpolator = new TerrainInterpolator();
      }

      @Test
      public void zero() throws Exception {
        interpolator.interpolate(dummy, 3);
        assertThat(dummy, is(new double[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}));
      }

      @Test
      public void allOnes() throws Exception {
        dummy[0][0] = dummy[2][0] = dummy[0][2] = dummy[2][2] = 1;
        interpolator.interpolate(dummy, 3);
        assertThat(dummy, is(new double[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}));
      }

      @Test
      public void ramp() throws Exception {
        dummy[0][0] = 0;
        dummy[2][0] = 12;
        dummy[0][2] = 12;
        dummy[2][2] = 24;
        interpolator.interpolate(dummy,3);
        assertThat(dummy, is(new double[][]{{0, 8, 12}, {8, 12, 16}, {12, 16, 24}}));
      }
    }

    public class RandomsAndOffsets {
      @Before
      public void setup() {
        dummy = new double[5][5];
        interpolator = new TerrainInterpolatorWithFixedRandom();
      }

      @Test
      public void volcano() throws Exception {
        interpolator.interpolate(dummy, 5, 2,4);
        assertThat(dummy, is(new double[][]{
          {0,8.5,8,8.5,0},
          {8.5,8.5,10.75,8.5,8.5},
          {8,10.75,6,10.75,8},
          {8.5,8.5,10.75,8.5,8.5},
          {0,8.5,8,8.5,0}
        }));
      }
    }
  }

  private class TerrainInterpolatorSpy extends TerrainInterpolator {
    void doSquare(int x, int y, int size) {
      actions += String.format("Square(%d,%d,%d): ", x, y, size);
      super.doSquare(x, y, size);
    }

    void doDiamond(int x, int y, int size) {
      actions += String.format("Diamond(%d,%d,%d): ", x, y, size);
      super.doDiamond(x, y, size);
    }

    void set(int x, int y, double value) {
      actions += String.format("->[%d,%d]. ", x, y);
    }

    double get(int x, int y) {
      return -1;
    }

    double average(Integer... points) {
      actions += "A(";
      for (int i = 0; i < points.length; i += 2)
        actions += String.format("[%d,%d],", points[i], points[i + 1]);
      actions = actions.substring(0, actions.length() - 1) + ")";
      return 0;
    }
  }

  private class TerrainInterpolatorDiamondSquareSpy extends TerrainInterpolator {
    void doSquare(int x, int y, int size) {
      actions += String.format("Square(%d,%d,%d) ", x, y, size);
    }

    void doDiamond(int x, int y, int size) {
      actions += String.format("Diamond(%d,%d,%d) ", x, y, size);

    }
  }

  private class TerrainInterpolatorWithFixedRandom extends TerrainInterpolator {
    double random() {
      return randomAmplitude;
    }
  }
}