package io.github.moremcmeta.animationplugin;

import com.mojang.datafixers.util.Pair;
import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.client.texture.ColorTransform;
import io.github.moremcmeta.moremcmeta.api.client.texture.MutableFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

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
    public MockMutableFrameView(int index, Pair<Integer, Area>... colorAndArea) {
        this(index, Color.pack(0, 0, 0, 0), colorAndArea);
    }

    @SafeVarargs
    public MockMutableFrameView(int index, int defaultColor, Pair<Integer, Area>... colorAndArea) {
        INDEX = index;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                PIXELS[y][x] = defaultColor;
            }
        }

        for (Pair<Integer, Area> pair : colorAndArea) {
            int color = pair.getFirst();
            for (long point : pair.getSecond()) {
                PIXELS[Point.y(point)][Point.x(point)] = color;
            }
        }
    }

    @Override
    public void transform(ColorTransform transform, Area applyArea, Area dependencies) {
        Long2IntMap oldColors = new Long2IntOpenHashMap();
        dependencies.forEach((point) -> oldColors.put(point, PIXELS[Point.y(point)][Point.x(point)]));
        applyArea.forEach((point) -> {
            int x = Point.x(point);
            int y = Point.y(point);
            PIXELS[y][x] = transform.transform(x, y, (depX, depY) -> oldColors.get(Point.pack(depX, depY)));
        });
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
