package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.PersistentFrameView;

import static io.github.moremcmeta.animationplugin.animate.AnimationComponentTest.indexToColor;

/**
 * Mock implementation of {@link PersistentFrameView}.
 * @author soir20
 */
public class MockPersistentFrameView implements PersistentFrameView {
    private final int INDEX;

    public MockPersistentFrameView(int index) {
        INDEX = index;
    }

    @Override
    public int color(int x, int y) {
        return indexToColor(INDEX);
    }

    @Override
    public int width() {
        return 10;
    }

    @Override
    public int height() {
        return 20;
    }
}
