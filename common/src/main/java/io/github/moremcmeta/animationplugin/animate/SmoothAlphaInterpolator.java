package io.github.moremcmeta.animationplugin.animate;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;

/**
 * Generates an interpolated color in between two other colors with a smooth
 * transition in alpha values between the start color and end color.
 * @author soir20
 */
public class SmoothAlphaInterpolator extends RGBAInterpolator {

    @Override
    protected int mixAlpha(double startProportion, int startColor, int endColor) {
        return mixComponent(startProportion, Color.alpha(startColor), Color.alpha(endColor));
    }

}
