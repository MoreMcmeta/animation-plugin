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
