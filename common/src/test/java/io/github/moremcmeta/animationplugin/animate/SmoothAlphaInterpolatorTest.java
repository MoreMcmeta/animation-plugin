package io.github.moremcmeta.animationplugin.animate;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link SmoothAlphaInterpolator}.
 * @author soir20
 */
public class SmoothAlphaInterpolatorTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void interpolate_StepLessThanOne_IllegalArgException() {
        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 11, 0, 0);
    }

    @Test
    public void interpolate_StepEqualsSteps_IllegalArgException() {
        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 10, 0, 0);
    }

    @Test
    public void interpolate_StepGreaterThanSteps_IllegalArgException() {
        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 0, 0, 0);
    }

    @Test
    public void interpolate_SameStartAndEnd_IdenticalOutput() {
        int firstColor = Color.pack(25, 50, 250, 255);
        int secondColor = Color.pack(25, 250, 50, 150);
        int thirdColor = Color.pack(250, 25, 50, 60);
        int fourthColor = Color.pack(25, 50, 30, 200);

        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();
        assertEquals(firstColor, interpolator.interpolate(10, 1, firstColor, firstColor));
        assertEquals(secondColor, interpolator.interpolate(10, 3, secondColor, secondColor));
        assertEquals(thirdColor, interpolator.interpolate(10, 5, thirdColor, thirdColor));
        assertEquals(fourthColor, interpolator.interpolate(10, 7, fourthColor, fourthColor));
    }

    @Test
    public void interpolate_AllTransparent_OutputTransparent() {
        int firstStartColor = Color.pack(25, 50, 250, 0);
        int secondStartColor = Color.pack(25, 250, 50, 0);
        int thirdStartColor = Color.pack(250, 25, 50, 0);
        int fourthStartColor = Color.pack(25, 50, 30, 0);

        int firstEndColor = Color.pack(54, 78, 243, 0);
        int secondEndColor = Color.pack(250, 25, 50, 0);
        int thirdEndColor = Color.pack(25, 250, 50, 0);
        int fourthEndColor = Color.pack(150, 50, 100, 0);

        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();

        assertEquals(Color.pack(39, 64, 246, 0), interpolator.interpolate(10, 5, firstStartColor, firstEndColor));
        assertEquals(Color.pack(137, 137, 50, 0), interpolator.interpolate(10, 5, secondStartColor, secondEndColor));
        assertEquals(Color.pack(137, 137, 50, 0), interpolator.interpolate(10, 5, thirdStartColor, thirdEndColor));
        assertEquals(Color.pack(87, 50, 65, 0), interpolator.interpolate(10, 5, fourthStartColor, fourthEndColor));
    }

    @Test
    public void interpolate_MixedColorsNearStart_CorrectlyAveraged() {
        int firstStartColor = Color.pack(184, 143, 65, 197);
        int secondStartColor = Color.pack(41, 248, 80, 100);
        int thirdStartColor = Color.pack(19, 159, 70, 226);
        int fourthStartColor = Color.pack(216, 101, 41, 195);

        int firstEndColor = Color.pack(25, 181, 119, 37);
        int secondEndColor = Color.pack(106, 126, 174, 11);
        int thirdEndColor = Color.pack(0, 238, 24, 122);
        int fourthEndColor = Color.pack(93, 209, 60, 223);

        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();

        assertEquals(Color.pack(152, 150, 75, 165), interpolator.interpolate(10, 2, firstStartColor, firstEndColor));
        assertEquals(Color.pack(54, 223, 98, 82), interpolator.interpolate(10, 2, secondStartColor, secondEndColor));
        assertEquals(Color.pack(15, 174, 60, 205), interpolator.interpolate(10, 2, thirdStartColor, thirdEndColor));
        assertEquals(Color.pack(191, 122, 44, 200), interpolator.interpolate(10, 2, fourthStartColor, fourthEndColor));
    }

    @Test
    public void interpolate_MixedColorsEven_CorrectlyAveraged() {
        int firstStartColor = Color.pack(184, 143, 65, 197);
        int secondStartColor = Color.pack(41, 248, 80, 100);
        int thirdStartColor = Color.pack(19, 159, 70, 226);
        int fourthStartColor = Color.pack(216, 101, 41, 195);

        int firstEndColor = Color.pack(25, 181, 119, 37);
        int secondEndColor = Color.pack(106, 126, 174, 11);
        int thirdEndColor = Color.pack(0, 238, 24, 122);
        int fourthEndColor = Color.pack(93, 209, 60, 223);

        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();

        assertEquals(Color.pack(104, 162, 92, 117), interpolator.interpolate(10, 5, firstStartColor, firstEndColor));
        assertEquals(Color.pack(73, 187, 127, 55), interpolator.interpolate(10, 5, secondStartColor, secondEndColor));
        assertEquals(Color.pack(9, 198, 47, 174), interpolator.interpolate(10, 5, thirdStartColor, thirdEndColor));
        assertEquals(Color.pack(154, 155, 50, 209), interpolator.interpolate(10, 5, fourthStartColor, fourthEndColor));
    }

    @Test
    public void interpolate_MixedColorsNearEnd_CorrectlyAveraged() {
        int firstStartColor = Color.pack(184, 143, 65, 197);
        int secondStartColor = Color.pack(41, 248, 80, 100);
        int thirdStartColor = Color.pack(19, 159, 70, 226);
        int fourthStartColor = Color.pack(216, 101, 41, 195);

        int firstEndColor = Color.pack(25, 181, 119, 37);
        int secondEndColor = Color.pack(106, 126, 174, 11);
        int thirdEndColor = Color.pack(0, 238, 24, 122);
        int fourthEndColor = Color.pack(93, 209, 60, 223);

        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();

        assertEquals(Color.pack(56, 173, 108, 69), interpolator.interpolate(10, 8, firstStartColor, firstEndColor));
        assertEquals(Color.pack(93, 150, 155, 28), interpolator.interpolate(10, 8, secondStartColor, secondEndColor));
        assertEquals(Color.pack(3, 222, 33, 142), interpolator.interpolate(10, 8, thirdStartColor, thirdEndColor));
        assertEquals(Color.pack(117, 187, 56, 217), interpolator.interpolate(10, 8, fourthStartColor, fourthEndColor));
    }

}