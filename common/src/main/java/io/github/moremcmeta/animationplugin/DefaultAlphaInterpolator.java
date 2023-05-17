package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;

/**
 * Generates an interpolated color in between two other colors using
 * Minecraft's default alpha interpolation (always uses the alpha value of the
 * start color and ignores the end color).
 * @author soir20
 */
public class DefaultAlphaInterpolator extends RGBAInterpolator {

    @Override
    protected int mixAlpha(double startProportion, int startColor, int endColor) {
        return Color.alpha(startColor);
    }

}
