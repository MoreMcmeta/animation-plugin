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
 * A single animation frame in a texture.
 * @author soir20
 */
public interface Frame {

    /**
     * Retrieves the color at the provided coordinate.
     * @param x     x-coordinate of the pixel to retrieve
     * @param y     y-coordinate of the pixel to retrieve
     * @return color at the provided coordinate
     */
    int color(int x, int y);

}
