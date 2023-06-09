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
