package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;

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
    Color interpolate(int steps, int step, Color start, Color end);

}
