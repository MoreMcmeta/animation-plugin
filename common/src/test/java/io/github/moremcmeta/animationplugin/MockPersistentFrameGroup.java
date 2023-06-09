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

import io.github.moremcmeta.moremcmeta.api.client.texture.FrameGroup;
import io.github.moremcmeta.moremcmeta.api.client.texture.PersistentFrameView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Mock implementation of {@link FrameGroup}.
 * @author soir20
 */
public class MockPersistentFrameGroup implements FrameGroup<PersistentFrameView> {
    private final List<PersistentFrameView> FRAMES;

    public MockPersistentFrameGroup(int frames) {
        FRAMES = new ArrayList<>();
        for (int frameIndex = 0; frameIndex < frames; frameIndex++) {
            FRAMES.add(new MockPersistentFrameView(frameIndex));
        }
    }

    @Override
    public PersistentFrameView frame(int index) {
        return FRAMES.get(index);
    }

    @Override
    public int frames() {
        return FRAMES.size();
    }

    @NotNull
    @Override
    public Iterator<PersistentFrameView> iterator() {
        return FRAMES.iterator();
    }
}
