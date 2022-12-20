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
