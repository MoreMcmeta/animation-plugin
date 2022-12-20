package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.client.texture.ColorTransform;
import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.moremcmeta.animationplugin.AnimationComponentTest.indexToColor;

/**
 * Mock implementation of {@link CurrentFrameView}.
 * @author soir20
 */
public class MockCurrentFrameView implements CurrentFrameView {
    private final int DIMENSION = 100;
    private final int[][] PIXELS;

    public MockCurrentFrameView() {
        PIXELS = new int[DIMENSION][DIMENSION];
        replaceWith(0);
    }

    @Override
    public void generateWith(ColorTransform transform, Area applyArea, Area dependencies) {

        Map<Point, Color> oldColors = new HashMap<>();
        for (Point dependency : dependencies) {
            oldColors.put(dependency, new Color(PIXELS[dependency.y()][dependency.x()]));
        }

        for (Point applyPoint : applyArea) {
            PIXELS[applyPoint.y()][applyPoint.x()] = transform.transform(applyPoint, oldColors::get).combine();
        }
    }

    @Override
    public void replaceWith(int index) {
        int newColor = indexToColor(index).combine();

        for (int y = 0; y < DIMENSION; y++) {
            Arrays.fill(PIXELS[y], newColor);
        }
    }

    @Override
    public int width() {
        return DIMENSION;
    }

    @Override
    public int height() {
        return DIMENSION;
    }

    @Override
    public Optional<Integer> index() {
        return Optional.empty();
    }

    public Color color(int x, int y) {
        return new Color(PIXELS[y][x]);
    }
}
