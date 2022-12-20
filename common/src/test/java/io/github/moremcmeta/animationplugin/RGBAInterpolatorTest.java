package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Tests the {@link RGBAInterpolator}.
 * @author soir20
 */
public class RGBAInterpolatorTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void interpolate_NullStartColor_NullPointerException() {
        RGBAInterpolator interpolator = new RGBAInterpolator();
        expectedException.expect(NullPointerException.class);
        interpolator.interpolate(10, 0, null, new Color(0));
    }

    @Test
    public void interpolate_NullEndColor_NullPointerException() {
        RGBAInterpolator interpolator = new RGBAInterpolator();
        expectedException.expect(NullPointerException.class);
        interpolator.interpolate(10, 0, new Color(0), null);
    }

    @Test
    public void interpolate_StepLessThanOne_IllegalArgException() {
        RGBAInterpolator interpolator = new RGBAInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 11, new Color(0), new Color(0));
    }

    @Test
    public void interpolate_StepEqualsSteps_IllegalArgException() {
        RGBAInterpolator interpolator = new RGBAInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 10, new Color(0), new Color(0));
    }

    @Test
    public void interpolate_StepGreaterThanSteps_IllegalArgException() {
        RGBAInterpolator interpolator = new RGBAInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 0, new Color(0), new Color(0));
    }

    @Test
    public void interpolate_SameStartAndEnd_IdenticalOutput() {
        Color firstColor = new Color(25, 50, 250, 255);
        Color secondColor = new Color(25, 250, 50, 150);
        Color thirdColor = new Color(250, 25, 50, 60);
        Color fourthColor = new Color(25, 50, 30, 200);

        RGBAInterpolator interpolator = new RGBAInterpolator();
        assertEquals(firstColor, interpolator.interpolate(10, 1, firstColor, firstColor));
        assertEquals(secondColor, interpolator.interpolate(10, 3, secondColor, secondColor));
        assertEquals(thirdColor, interpolator.interpolate(10, 5, thirdColor, thirdColor));
        assertEquals(fourthColor, interpolator.interpolate(10, 7, fourthColor, fourthColor));
    }

    @Test
    public void interpolate_AllTransparent_OutputTransparent() {
        Color firstStartColor = new Color(25, 50, 250, 0);
        Color secondStartColor = new Color(25, 250, 50, 0);
        Color thirdStartColor = new Color(250, 25, 50, 0);
        Color fourthStartColor = new Color(25, 50, 30, 0);

        Color firstEndColor = new Color(54, 78, 243, 0);
        Color secondEndColor = new Color(250, 25, 50, 0);
        Color thirdEndColor = new Color(25, 250, 50, 0);
        Color fourthEndColor = new Color(150, 50, 100, 0);

        RGBAInterpolator interpolator = new RGBAInterpolator();

        assertEquals(new Color(39, 64, 246, 0), interpolator.interpolate(10, 5, firstStartColor, firstEndColor));
        assertEquals(new Color(137, 137, 50, 0), interpolator.interpolate(10, 5, secondStartColor, secondEndColor));
        assertEquals(new Color(137, 137, 50, 0), interpolator.interpolate(10, 5, thirdStartColor, thirdEndColor));
        assertEquals(new Color(87, 50, 65, 0), interpolator.interpolate(10, 5, fourthStartColor, fourthEndColor));
    }

    @Test
    public void interpolate_MixedColorsNearStart_CorrectlyAveraged() {
        Color firstStartColor = new Color(184, 143, 65, 197);
        Color secondStartColor = new Color(41, 248, 80, 100);
        Color thirdStartColor = new Color(19, 159, 70, 226);
        Color fourthStartColor = new Color(216, 101, 41, 195);

        Color firstEndColor = new Color(25, 181, 119, 37);
        Color secondEndColor = new Color(106, 126, 174, 11);
        Color thirdEndColor = new Color(0, 238, 24, 122);
        Color fourthEndColor = new Color(93, 209, 60, 223);

        RGBAInterpolator interpolator = new RGBAInterpolator();

        assertEquals(new Color(152, 150, 75, 197), interpolator.interpolate(10, 2, firstStartColor, firstEndColor));
        assertEquals(new Color(54, 223, 98, 100), interpolator.interpolate(10, 2, secondStartColor, secondEndColor));
        assertEquals(new Color(15, 174, 60, 226), interpolator.interpolate(10, 2, thirdStartColor, thirdEndColor));
        assertEquals(new Color(191, 122, 44, 195), interpolator.interpolate(10, 2, fourthStartColor, fourthEndColor));
    }

    @Test
    public void interpolate_MixedColorsEven_CorrectlyAveraged() {
        Color firstStartColor = new Color(184, 143, 65, 197);
        Color secondStartColor = new Color(41, 248, 80, 100);
        Color thirdStartColor = new Color(19, 159, 70, 226);
        Color fourthStartColor = new Color(216, 101, 41, 195);

        Color firstEndColor = new Color(25, 181, 119, 37);
        Color secondEndColor = new Color(106, 126, 174, 11);
        Color thirdEndColor = new Color(0, 238, 24, 122);
        Color fourthEndColor = new Color(93, 209, 60, 223);

        RGBAInterpolator interpolator = new RGBAInterpolator();

        assertEquals(new Color(104, 162, 92, 197), interpolator.interpolate(10, 5, firstStartColor, firstEndColor));
        assertEquals(new Color(73, 187, 127, 100), interpolator.interpolate(10, 5, secondStartColor, secondEndColor));
        assertEquals(new Color(9, 198, 47, 226), interpolator.interpolate(10, 5, thirdStartColor, thirdEndColor));
        assertEquals(new Color(154, 155, 50, 195), interpolator.interpolate(10, 5, fourthStartColor, fourthEndColor));
    }

    @Test
    public void interpolate_MixedColorsNearEnd_CorrectlyAveraged() {
        Color firstStartColor = new Color(184, 143, 65, 197);
        Color secondStartColor = new Color(41, 248, 80, 100);
        Color thirdStartColor = new Color(19, 159, 70, 226);
        Color fourthStartColor = new Color(216, 101, 41, 195);

        Color firstEndColor = new Color(25, 181, 119, 37);
        Color secondEndColor = new Color(106, 126, 174, 11);
        Color thirdEndColor = new Color(0, 238, 24, 122);
        Color fourthEndColor = new Color(93, 209, 60, 223);

        RGBAInterpolator interpolator = new RGBAInterpolator();

        assertEquals(new Color(56, 173, 108, 197), interpolator.interpolate(10, 8, firstStartColor, firstEndColor));
        assertEquals(new Color(93, 150, 155, 100), interpolator.interpolate(10, 8, secondStartColor, secondEndColor));
        assertEquals(new Color(3, 222, 33, 226), interpolator.interpolate(10, 8, thirdStartColor, thirdEndColor));
        assertEquals(new Color(117, 187, 56, 195), interpolator.interpolate(10, 8, fourthStartColor, fourthEndColor));
    }

}