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

package io.github.moremcmeta.animationplugin;

import com.mojang.datafixers.util.Pair;
import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.client.texture.ColorTransform;
import io.github.moremcmeta.moremcmeta.api.client.texture.MutableFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

/**
 * Mock implementation of a {@link MutableFrameView}.
 * @author soir20
 */
public class MockMutableFrameView implements MutableFrameView {
    private final int WIDTH = 10;
    private final int HEIGHT = 20;
    private final int[][] PIXELS = new int[HEIGHT][WIDTH];

    @SafeVarargs
    public MockMutableFrameView(Pair<Integer, Area>... colorAndArea) {
        this(Color.pack(0, 0, 0, 0), colorAndArea);
    }

    @SafeVarargs
    public MockMutableFrameView(int defaultColor, Pair<Integer, Area>... colorAndArea) {
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
    public void transform(ColorTransform transform, Area applyArea) {
        Long2IntMap oldColors = new Long2IntOpenHashMap();
        applyArea.forEach((point) -> oldColors.put(point, PIXELS[Point.y(point)][Point.x(point)]));
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

    public int color(int x, int y) {
        return PIXELS[y][x];
    }
}
