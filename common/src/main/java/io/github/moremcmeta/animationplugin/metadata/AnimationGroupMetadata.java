package io.github.moremcmeta.animationplugin.metadata;

import io.github.moremcmeta.moremcmeta.api.client.metadata.AnalyzedMetadata;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Holds all {@link AnimationMetadata} for animations within the same texture.
 * @author soir20
 */
public class AnimationGroupMetadata implements AnalyzedMetadata {
    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;
    private final List<AnimationMetadata> PARTS;

    /**
     * Creates a new group of animation metadata.
     * @param frameWidth        width of a frame in the base texture only
     * @param frameHeight       height of a frame in the base texture only
     * @param parts             all parts of the animation/members of the group
     */
    public AnimationGroupMetadata(int frameWidth, int frameHeight, List<AnimationMetadata> parts) {
        FRAME_WIDTH = frameWidth;
        FRAME_HEIGHT = frameHeight;
        PARTS = requireNonNull(parts, "Parts cannot be null");
    }

    @Override
    public Optional<Integer> frameWidth() {
        return Optional.of(FRAME_WIDTH);
    }

    @Override
    public Optional<Integer> frameHeight() {
        return Optional.of(FRAME_HEIGHT);
    }

    /**
     * Gets the list of all animations in this group/within the same texture.
     * @return all animations in this group
     */
    public List<AnimationMetadata> parts() {
        return PARTS;
    }

}
