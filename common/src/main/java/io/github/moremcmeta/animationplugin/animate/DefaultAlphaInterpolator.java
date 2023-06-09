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

/**
 * Generates an interpolated color in between two other colors using
 * Minecraft's default alpha interpolation (always uses the alpha value of the
 * start color and ignores the end color).
 * @author soir20
 */
public final class DefaultAlphaInterpolator extends RGBAInterpolator {

    @Override
    protected int mixAlpha(double startProportion, int startColor, int endColor) {
        return Color.alpha(startColor);
    }

}
