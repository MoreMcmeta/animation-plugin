package io.github.moremcmeta.animationplugin.animate;

/**
 * Interpolates between two colors.
 * @author soir20
 */
public interface Interpolator {

    /**
     * Calculates a color between two other colors at a certain step.
     * @param steps     total number of steps to interpolate
     * @param step      current step of the interpolation (between 1 and steps - 1)
     * @param start     color to start interpolation from
     * @param end       color to end interpolation at
     * @return  the interpolated color at the given step
     */
    int interpolate(int steps, int step, int start, int end);

}
