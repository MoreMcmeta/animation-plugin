package io.github.moremcmeta.animationplugin.metadata;

import com.google.common.collect.ImmutableList;
import io.github.moremcmeta.animationplugin.animate.Frame;
import it.unimi.dsi.fastutil.ints.IntIntPair;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Contains animation metadata that has been parsed.
 * @author soir20
 */
public class AnimationMetadata {
    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;
    private final int DEFAULT_TIME;
    private final boolean INTERPOLATE;
    private final boolean SMOOTH_ALPHA;
    private final ImmutableList<IntIntPair> FRAMES;
    private final int SKIP_TICKS;
    private final boolean DAYTIME_SYNC;
    private final int X_IN_BASE;
    private final int Y_IN_BASE;
    private final Optional<List<Frame>> PART_FRAMES;
    private final Runnable RESOURCE_CLOSER;

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
     * @param xInBase           x-coordinate of the top-left corner of this animation within the base texture
     * @param yInBase           y-coordinate of the top-left corner of this animation within the base texture
     * @param partFrames        frames this animation should use if it should not use predefined
     *                          frames in the base texture
     * @param resourceCloser    closes resources associated with this animation
     */
    public AnimationMetadata(int frameWidth, int frameHeight, int defaultTime, boolean interpolate, boolean smoothAlpha,
                             List<IntIntPair> frames, int skipTicks, boolean daytimeSync, int xInBase, int yInBase,
                             Optional<List<Frame>> partFrames, Runnable resourceCloser) {
        FRAME_WIDTH = frameWidth;
        FRAME_HEIGHT = frameHeight;
        DEFAULT_TIME = defaultTime;
        INTERPOLATE = interpolate;
        SMOOTH_ALPHA = smoothAlpha;
        FRAMES = requireNonNull(ImmutableList.copyOf(frames), "Frames cannot be null");
        SKIP_TICKS = skipTicks;
        DAYTIME_SYNC = daytimeSync;
        X_IN_BASE = xInBase;
        Y_IN_BASE = yInBase;
        PART_FRAMES = requireNonNull(partFrames, "Part frames cannot be null");
        RESOURCE_CLOSER = requireNonNull(resourceCloser, "Resource closer cannot be null");
    }

    /**
     * Gets the width of a frame in this animation.
     * @return width of a frame in this animation
     */
    public int frameWidth() {
        return FRAME_WIDTH;
    }

    /**
     * Gets the height of a frame in this animation.
     * @return height of a frame in this animation
     */
    public int frameHeight() {
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

    /**
     * Frames this animation should use if it should not use predefined frames in the base texture.
     * @return frames this animation should use if it should not use predefined frames in the base texture
     */
    public Optional<List<Frame>> partFrames() {
        return PART_FRAMES;
    }

    /**
     * Gets the x-coordinate of the top-left corner of this animation within the base texture.
     * @return x-coordinate of the top-left corner of this animation within the base texture
     */
    public int xInBase() {
        return X_IN_BASE;
    }

    /**
     * Gets the y-coordinate of the top-left corner of this animation within the base texture.
     * @return y-coordinate of the top-left corner of this animation within the base texture
     */
    public int yInBase() {
        return Y_IN_BASE;
    }

    /**
     * Closes all resources associated with this animation.
     */
    public void close() {
        RESOURCE_CLOSER.run();
    }

}
