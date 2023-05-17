package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;

/**
 * Generates an interpolated color in between two other colors.
 * Color format: AAAA AAAA RRRR RRRR GGGG GGGG BBBB BBBB in binary, stored as an integer (32 bits total)
 * @author soir20
 */
public abstract class RGBAInterpolator implements Interpolator {

    /**
     * Generates an interpolated color between two other colors.
     * @param steps     the number of steps it should take from the start color to reach the end color
     * @param step      the current step in the interpolation. Start at 1, and end at steps - 1.
     * @param start     the color to start interpolation at
     * @param end       the color to end interpolation at
     * @return  the interpolated frame at this step
     */
    public int interpolate(int steps, int step, int start, int end) {
        if (step < 1 || step >= steps) {
            throw new IllegalArgumentException("Step must be between 1 and steps - 1 (inclusive)");
        }

        double ratio = 1.0 - (step / (double) steps);
        return mixPixel(ratio, start, end);
    }

    /**
     * Mixes the alpha component from two RGB colors.
     * @param startProportion   proportion of start color to mix (1 - proportion of end color)
     * @param startColor        value of the first color's component
     * @param endColor          value of the second color's component
     * @return the resultant mixed component
     */
    protected abstract int mixAlpha(double startProportion, int startColor, int endColor);

    /**
     * Mixes one component from two RGB colors.
     * @param startProportion   proportion of start color to mix (1 - proportion of end color)
     * @param startColor        value of the first color's component
     * @param endColor          value of the second color's component
     * @return  the resultant mixed component
     */
    protected int mixComponent(double startProportion, int startColor, int endColor) {
        return (int) (startProportion * startColor + (1.0 - startProportion) * endColor);
    }

    /**
     * Mixes the colors of two pixels into a single color.
     * @param startProportion   proportion of start color to mix (1 - proportion of end color)
     * @param startColor        color of the first pixel
     * @param endColor          color of the second pixel
     * @return  the resultant mixed color
     */
    private int mixPixel(double startProportion, int startColor, int endColor) {
        int red = mixComponent(startProportion, Color.red(startColor), Color.red(endColor));
        int green = mixComponent(startProportion, Color.green(startColor), Color.green(endColor));
        int blue = mixComponent(startProportion, Color.blue(startColor), Color.blue(endColor));

        return Color.pack(red, green, blue, mixAlpha(startProportion, startColor, endColor));
    }

}
