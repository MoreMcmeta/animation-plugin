package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.ColorTransform;
import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

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
    }

    @Override
    public void generateWith(ColorTransform transform, Area applyArea) {

        Long2IntMap oldColors = new Long2IntOpenHashMap();
        for (long dependency : applyArea) {
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
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return HEIGHT;
    }

    public int color(int x, int y) {
        return PIXELS[y][x];
    }
}
