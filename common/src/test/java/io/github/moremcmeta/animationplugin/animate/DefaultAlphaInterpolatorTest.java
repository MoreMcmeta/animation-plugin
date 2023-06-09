/*
 * MoreMcmeta is a Minecraft mod expanding texture configuration capabilities.
 * Copyright (C) 2023 soir20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.moremcmeta.animationplugin.animate;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link DefaultAlphaInterpolator}.
 * @author soir20
 */
public final class DefaultAlphaInterpolatorTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void interpolate_StepLessThanOne_IllegalArgException() {
        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 11, 0, 0);
    }

    @Test
    public void interpolate_StepEqualsSteps_IllegalArgException() {
        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 10, 0, 0);
    }

    @Test
    public void interpolate_StepGreaterThanSteps_IllegalArgException() {
        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();
        expectedException.expect(IllegalArgumentException.class);
        interpolator.interpolate(10, 11, 0, 0);
    }

    @Test
    public void interpolate_SameStartAndEnd_IdenticalOutput() {
        int firstColor = Color.pack(25, 50, 250, 255);
        int secondColor = Color.pack(25, 250, 50, 150);
        int thirdColor = Color.pack(250, 25, 50, 60);
        int fourthColor = Color.pack(25, 50, 30, 200);

        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();
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

        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();

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

        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();

        assertEquals(Color.pack(152, 150, 75, 197), interpolator.interpolate(10, 2, firstStartColor, firstEndColor));
        assertEquals(Color.pack(54, 223, 98, 100), interpolator.interpolate(10, 2, secondStartColor, secondEndColor));
        assertEquals(Color.pack(15, 174, 60, 226), interpolator.interpolate(10, 2, thirdStartColor, thirdEndColor));
        assertEquals(Color.pack(191, 122, 44, 195), interpolator.interpolate(10, 2, fourthStartColor, fourthEndColor));
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

        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();

        assertEquals(Color.pack(104, 162, 92, 197), interpolator.interpolate(10, 5, firstStartColor, firstEndColor));
        assertEquals(Color.pack(73, 187, 127, 100), interpolator.interpolate(10, 5, secondStartColor, secondEndColor));
        assertEquals(Color.pack(9, 198, 47, 226), interpolator.interpolate(10, 5, thirdStartColor, thirdEndColor));
        assertEquals(Color.pack(154, 155, 50, 195), interpolator.interpolate(10, 5, fourthStartColor, fourthEndColor));
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

        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();

        assertEquals(Color.pack(56, 173, 108, 197), interpolator.interpolate(10, 8, firstStartColor, firstEndColor));
        assertEquals(Color.pack(93, 150, 155, 100), interpolator.interpolate(10, 8, secondStartColor, secondEndColor));
        assertEquals(Color.pack(3, 222, 33, 226), interpolator.interpolate(10, 8, thirdStartColor, thirdEndColor));
        assertEquals(Color.pack(117, 187, 56, 195), interpolator.interpolate(10, 8, fourthStartColor, fourthEndColor));
    }

}