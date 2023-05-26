package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.ColorTransform;
import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import java.util.Arrays;
import java.util.Optional;

import static io.github.moremcmeta.animationplugin.animate.AnimationComponentTest.indexToColor;

/**
 * Mock implementation of {@link CurrentFrameView}.
 * @author soir20
 */
public class MockCurrentFrameView implements CurrentFrameView {
    private final int WIDTH = 10;
    private final int HEIGHT = 20;
    private final int[][] PIXELS;

    public MockCurrentFrameView() {
        PIXELS = new int[HEIGHT][WIDTH];
        replaceWith(0);
    }

    @Override
    public void generateWith(ColorTransform transform, Area applyArea, Area dependencies) {

        Long2IntMap oldColors = new Long2IntOpenHashMap();
        for (long dependency : dependencies) {
            oldColors.put(dependency, PIXELS[Point.y(dependency)][Point.x(dependency)]);
        }

        for (long applyPoint : applyArea) {
            int x = Point.x(applyPoint);
            int y = Point.y(applyPoint);
            PIXELS[y][x] = transform.transform(
                    x,
                    y,
                    (depX, depY) -> oldColors.get(Point.pack(depX, depY))
            );
        }
    }

    @Override
    public void replaceWith(int index) {
        int newColor = indexToColor(index);

        for (int y = 0; y < HEIGHT; y++) {
            Arrays.fill(PIXELS[y], newColor);
        }
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
        return Optional.empty();
    }

    public int color(int x, int y) {
        return PIXELS[y][x];
    }
}
