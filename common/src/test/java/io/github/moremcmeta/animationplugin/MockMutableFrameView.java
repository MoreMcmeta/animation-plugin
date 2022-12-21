package io.github.moremcmeta.animationplugin;

import com.mojang.datafixers.util.Pair;
import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.client.texture.ColorTransform;
import io.github.moremcmeta.moremcmeta.api.client.texture.MutableFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock implementation of a {@link MutableFrameView}.
 * @author soir20
 */
public class MockMutableFrameView implements MutableFrameView {
    private final int WIDTH = 10;
    private final int HEIGHT = 20;
    private final int[][] PIXELS = new int[HEIGHT][WIDTH];
    private final int INDEX;

    @SafeVarargs
    public MockMutableFrameView(int index, Pair<Color, Area>... colorAndArea) {
        this(index, new Color(0, 0, 0, 0), colorAndArea);
    }

    @SafeVarargs
    public MockMutableFrameView(int index, Color defaultColor, Pair<Color, Area>... colorAndArea) {
        INDEX = index;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                PIXELS[y][x] = defaultColor.combine();
            }
        }

        for (Pair<Color, Area> pair : colorAndArea) {
            int color = pair.getFirst().combine();
            for (Point point : pair.getSecond()) {
                PIXELS[point.y()][point.x()] = color;
            }
        }
    }

    @Override
    public void transform(ColorTransform transform, Area applyArea, Area dependencies) {
        Map<Point, Color> oldColors = new HashMap<>();
        dependencies.forEach((point) -> oldColors.put(point, new Color(PIXELS[point.y()][point.x()])));
        applyArea.forEach((point) -> PIXELS[point.y()][point.x()] = transform.transform(point, oldColors::get).combine());
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return HEIGHT;
    }

    @Override
    public Optional<Integer> index() {
        return Optional.of(INDEX);
    }
}
