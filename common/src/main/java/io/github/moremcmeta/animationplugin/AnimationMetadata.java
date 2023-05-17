package io.github.moremcmeta.animationplugin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import io.github.moremcmeta.moremcmeta.api.client.metadata.ParsedMetadata;
import io.github.moremcmeta.moremcmeta.api.client.texture.TextureHandle;
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
    private final ImmutableList<TextureHandle> BASES;
    private final int UPLOAD_X;
    private final int UPLOAD_Y;
    private final int SKIP_TICKS;
    private final boolean DAYTIME_SYNC;

    /**
     * Creates a new container for animation metadata.
     * @param frameWidth        width of a frame in the animation
     * @param frameHeight       height of a frame in the animation
     * @param defaultTime       default time for a frame in the animation
     * @param interpolate       whether to interpolate frames in the animation
     * @param smoothAlpha       whether to interpolate alpha smoothly throughout the animation
     * @param frames            frames in the animation
     * @param bases             textures that the animation will be uploaded to
     * @param uploadX           x-coordinate where the top-left corner of the animation should be uploaded
     * @param uploadY           y-coordinate where the top-left corner of the animation should be uploaded
     * @param skipTicks         ticks to skip before the animation starts
     * @param daytimeSync       whether to synchronize the animation to the time of day
     */
    public AnimationMetadata(int frameWidth, int frameHeight, int defaultTime, boolean interpolate, boolean smoothAlpha,
                             List<IntIntPair> frames, Collection<TextureHandle> bases, int uploadX, int uploadY,
                             int skipTicks, boolean daytimeSync) {
        FRAME_WIDTH = frameWidth;
        FRAME_HEIGHT = frameHeight;
        DEFAULT_TIME = defaultTime;
        INTERPOLATE = interpolate;
        SMOOTH_ALPHA = smoothAlpha;
        FRAMES = requireNonNull(ImmutableList.copyOf(frames), "Frames cannot be null");
        BASES = requireNonNull(ImmutableList.copyOf(bases), "Bases cannot be null");
        UPLOAD_X = uploadX;
        UPLOAD_Y = uploadY;
        SKIP_TICKS = skipTicks;
        DAYTIME_SYNC = daytimeSync;
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
     * Gets all textures that the animation will be uploaded to
     * @return textures that the animation will be uploaded to
     */
    public ImmutableCollection<TextureHandle> bases() {
        return BASES;
    }

    /**
     * Gets the x-coordinate where the top-left corner of the animation should be uploaded.
     * @return x-coordinate of the top-left corner of the uploaded animation
     */
    public int uploadX() {
        return UPLOAD_X;
    }

    /**
     * Gets the y-coordinate where the top-left corner of the animation should be uploaded.
     * @return y-coordinate of the top-left corner of the uploaded animation
     */
    public int uploadY() {
        return UPLOAD_Y;
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
