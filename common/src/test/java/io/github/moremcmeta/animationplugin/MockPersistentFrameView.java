package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.client.texture.PersistentFrameView;

import java.util.Optional;

import static io.github.moremcmeta.animationplugin.AnimationComponentTest.indexToColor;

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
    public Color color(int x, int y) {
        return indexToColor(INDEX);
    }

    @Override
    public int width() {
        return 100;
    }

    @Override
    public int height() {
        return 100;
    }

    @Override
    public Optional<Integer> index() {
        return Optional.of(INDEX);
    }
}
