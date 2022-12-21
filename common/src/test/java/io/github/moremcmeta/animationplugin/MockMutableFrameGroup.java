package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.FrameGroup;
import io.github.moremcmeta.moremcmeta.api.client.texture.MutableFrameView;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Mock implementation of a {@link FrameGroup} for {@link MutableFrameView}s.
 * @author soir20
 */
public class MockMutableFrameGroup implements FrameGroup<MutableFrameView> {
    private final List<MutableFrameView> FRAMES;

    public MockMutableFrameGroup(MockMutableFrameView... frames) {
        FRAMES = Arrays.asList(frames);
    }

    @Override
    public MutableFrameView frame(int index) {
        return FRAMES.get(index);
    }

    @Override
    public int frames() {
        return FRAMES.size();
    }

    @NotNull
    @Override
    public Iterator<MutableFrameView> iterator() {
        return FRAMES.iterator();
    }
}
