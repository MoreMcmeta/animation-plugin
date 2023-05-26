package io.github.moremcmeta.animationplugin.parse;

import com.google.common.collect.ImmutableList;
import io.github.moremcmeta.moremcmeta.api.client.metadata.Base;
import io.github.moremcmeta.moremcmeta.api.client.metadata.ParsedMetadata;
import it.unimi.dsi.fastutil.ints.IntIntPair;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Contains animation metadata that has been parsed.
 * @author soir20
 */
public class AnimationMetadata implements ParsedMetadata {
    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;
    private final int DEFAULT_TIME;
    private final boolean INTERPOLATE;
    private final boolean SMOOTH_ALPHA;
    private final ImmutableList<IntIntPair> FRAMES;
    private final int SKIP_TICKS;
    private final boolean DAYTIME_SYNC;
    private final Collection<Base> BASES;

    /**
     * Creates a new container for animation metadata.
     * @param frameWidth        width of a frame in the animation
     * @param frameHeight       height of a frame in the animation
     * @param defaultTime       default time for a frame in the animation
     * @param interpolate       whether to interpolate frames in the animation
     * @param smoothAlpha       whether to interpolate alpha smoothly throughout the animation
     * @param frames            frames in the animation
     * @param skipTicks         ticks to skip before the animation starts
     * @param daytimeSync       whether to synchronize the animation to the time of day
     * @param bases             base textures of the animation
     */
    public AnimationMetadata(int frameWidth, int frameHeight, int defaultTime, boolean interpolate, boolean smoothAlpha,
                             List<IntIntPair> frames, int skipTicks, boolean daytimeSync, Collection<Base> bases) {
        FRAME_WIDTH = frameWidth;
        FRAME_HEIGHT = frameHeight;
        DEFAULT_TIME = defaultTime;
        INTERPOLATE = interpolate;
        SMOOTH_ALPHA = smoothAlpha;
        FRAMES = requireNonNull(ImmutableList.copyOf(frames), "Frames cannot be null");
        SKIP_TICKS = skipTicks;
        DAYTIME_SYNC = daytimeSync;
        BASES = requireNonNull(bases, "Bases cannot be null");
    }

    @Override
    public Optional<Integer> frameWidth() {
        return Optional.of(FRAME_WIDTH);
    }

    @Override
    public Optional<Integer> frameHeight() {
        return Optional.of(FRAME_HEIGHT);
    }

    @Override
    public Collection<Base> bases() {
        return BASES;
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
     * Gets whether to smoothly transition between alpha values in the animation.
     * @return whether to smoothly transition between alpha values in the animation
     */
    public boolean smoothAlpha() {
        return SMOOTH_ALPHA;
    }

    /**
     * Gets all predefined frames in the animation as (index, time) pairs. If no frames are defined,
     * then all the frames in the animation should be used with the default frame time.
     * @return all predefined frames in the animation
     */
    public ImmutableList<IntIntPair> predefinedFrames() {
        return FRAMES;
    }

    /**
     * Gets the number of ticks to skip before the animation starts.
     * @return number of ticks to skip before the animation starts
     */
    public int skipTicks() {
        return SKIP_TICKS;
    }

    /**
     * Gets whether to synchronize the animation to the time of day.
     * @return whether to synchronize the animation to the time of day
     */
    public boolean daytimeSync() {
        return DAYTIME_SYNC;
    }

}
