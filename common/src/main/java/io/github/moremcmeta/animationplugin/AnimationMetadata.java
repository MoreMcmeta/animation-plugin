package io.github.moremcmeta.animationplugin;

import com.google.common.collect.ImmutableList;
import io.github.moremcmeta.moremcmeta.api.client.metadata.ParsedMetadata;
import it.unimi.dsi.fastutil.ints.IntIntPair;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Contains animation metadata that has been parsed.
 * @author soir20
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AnimationMetadata implements ParsedMetadata {
    private final Optional<Integer> FRAME_WIDTH;
    private final Optional<Integer> FRAME_HEIGHT;
    private final int DEFAULT_TIME;
    private final boolean INTERPOLATE;
    private final ImmutableList<IntIntPair> FRAMES;

    /**
     * Creates a new container for animation metadata.
     * @param frameWidth        width of a frame in the animation
     * @param frameHeight       height of a frame in the animation
     * @param defaultTime       default time for a frame in the animation
     * @param interpolate       whether to interpolate frames in the animation
     * @param frames            frames in the animation
     */
    public AnimationMetadata(Optional<Integer> frameWidth, Optional<Integer> frameHeight,
                             int defaultTime, boolean interpolate, List<IntIntPair> frames) {
        FRAME_WIDTH = requireNonNull(frameWidth, "Frame width optional cannot be null");
        FRAME_HEIGHT = requireNonNull(frameHeight, "Frame height optional cannot be null");
        DEFAULT_TIME = defaultTime;
        INTERPOLATE = interpolate;
        FRAMES = requireNonNull(ImmutableList.copyOf(frames), "Frames cannot be null");
    }

    @Override
    public Optional<Integer> frameWidth() {
        return FRAME_WIDTH;
    }

    @Override
    public Optional<Integer> frameHeight() {
        return FRAME_HEIGHT;
    }

    /**
     * Gets the default time for a frame in the animation.
     * @return default time for a frame in the animation
     */
    public int defaultTime() {
        return DEFAULT_TIME;
    }

    /**
     * Gets whether to interpolate frames in the animation.
     * @return whether to interpolate frames in the animation
     */
    public boolean interpolate() {
        return INTERPOLATE;
    }

    /**
     * Gets all predefined frames in the animation as (index, time) pairs. If no frames are defined,
     * then all the frames in the animation should be used with the default frame time.
     * @return all predefined frames in the animation
     */
    public ImmutableList<IntIntPair> predefinedFrames() {
        return FRAMES;
    }

}
