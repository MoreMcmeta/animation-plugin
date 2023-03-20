package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.FrameGroup;
import io.github.moremcmeta.moremcmeta.api.client.texture.PersistentFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.TextureComponent;
import io.github.moremcmeta.moremcmeta.api.client.texture.UploadableFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;

import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * {@link TextureComponent} that animates a texture.
 * @author soir20
 */
public class AnimationComponent implements TextureComponent<CurrentFrameView, UploadableFrameView> {
    private final AnimationState STATE;
    private final int TICKS_UNTIL_START;
    private final IntUnaryOperator FRAME_INDEX_MAPPER;
    private final Interpolator INTERPOLATOR;
    private final Area INTERPOLATE_AREA;
    private final int SYNC_TICKS;
    private final Supplier<Optional<Long>> TIME_GETTER;

    /**
     * Creates a new animation component for an animation that is synchronized to the level time.
     * @param interpolateArea           pixels to interpolate/modify during the animation
     * @param frames                    number of predefined frames in the animation
     * @param ticksUntilStart           ticks between the first tick in the first frame and the start of the animation
     * @param frameTimeCalculator       calculates the duration of each frame in ticks
     * @param frameIndexMapper          maps frame indices to the index of the corresponding predefined frame
     * @param interpolator              interpolates between colors
     * @param syncTicks                 number of ticks to sync to; e.g. 24000 to sync to a Minecraft day
     * @param timeGetter                retrieves the current time in the world, if any
     */
    public AnimationComponent(Area interpolateArea, int frames, int ticksUntilStart,
                              IntUnaryOperator frameTimeCalculator, IntUnaryOperator frameIndexMapper,
                              Interpolator interpolator, int syncTicks, Supplier<Optional<Long>> timeGetter) {
        this(
                interpolateArea,
                frames,
                ticksUntilStart,
                frameTimeCalculator,
                frameIndexMapper,
                interpolator,
                syncTicks,
                timeGetter,
                false
        );
    }

    /**
     * Creates a new animation component for an animation that is not synchronized to the level time.
     * @param interpolateArea           pixels to interpolate/modify during the animation
     * @param frames                    number of predefined frames in the animation
     * @param ticksUntilStart           ticks between the first tick in the first frame and the start of the animation
     * @param frameTimeCalculator       calculates the duration of each frame in ticks
     * @param frameIndexMapper          maps frame indices to the index of the corresponding predefined frame
     * @param interpolator              interpolates between colors
     */
    public AnimationComponent(Area interpolateArea, int frames, int ticksUntilStart,
                              IntUnaryOperator frameTimeCalculator, IntUnaryOperator frameIndexMapper,
                              Interpolator interpolator) {
        this(
                interpolateArea,
                frames,
                ticksUntilStart,
                frameTimeCalculator,
                frameIndexMapper,
                interpolator,
                -1,
                Optional::empty,
                true
        );
    }

    @Override
    public void onTick(CurrentFrameView currentFrame, FrameGroup<PersistentFrameView> predefinedFrames) {
        Optional<Long> timeOptional = TIME_GETTER.get();

        if (timeOptional.isPresent()) {
            long currentTime = timeOptional.get();
            int ticksToAdd = Math.floorMod(currentTime - STATE.ticks(), SYNC_TICKS) + TICKS_UNTIL_START;

            STATE.tick(ticksToAdd);
        } else {
            STATE.tick(1);
        }

        int startIndex = FRAME_INDEX_MAPPER.applyAsInt(STATE.startIndex());
        int endIndex = FRAME_INDEX_MAPPER.applyAsInt(STATE.endIndex());

        if (startIndex == endIndex) {
            currentFrame.replaceWith(startIndex);
            return;
        }

        currentFrame.generateWith(
                (overwriteX, overwriteY, dependencyFunction) -> INTERPOLATOR.interpolate(
                        STATE.frameMaxTime(),
                        STATE.frameTicks(),
                        predefinedFrames.frame(startIndex).color(overwriteX, overwriteY),
                        predefinedFrames.frame(endIndex).color(overwriteX, overwriteY)
                ),
                INTERPOLATE_AREA,
                INTERPOLATE_AREA
        );
    }

    /**
     * Creates a new animation component.
     * @param interpolateArea           pixels to interpolate/modify during the animation
     * @param frames                    number of predefined frames in the animation
     * @param ticksUntilStart           ticks between the first tick in the first frame and the start of the animation
     * @param frameTimeCalculator       calculates the duration of each frame in ticks
     * @param frameIndexMapper          maps frame indices to the index of the corresponding predefined frame
     * @param interpolator              interpolates between colors
     * @param syncTicks                 number of ticks to sync to; e.g. 24000 to sync to a Minecraft day
     * @param timeGetter                retrieves the current time in the world, if any
     * @param allowNegativeSyncTicks    whether sync ticks can be negative; true when the animation is not synchronized
     */
    private AnimationComponent(Area interpolateArea, int frames, int ticksUntilStart,
                               IntUnaryOperator frameTimeCalculator, IntUnaryOperator frameIndexMapper,
                               Interpolator interpolator, int syncTicks, Supplier<Optional<Long>> timeGetter,
                               boolean allowNegativeSyncTicks) {
        requireNonNull(frameTimeCalculator, "Frame time calculator cannot be null");
        STATE = new AnimationState(frames, frameTimeCalculator);

        if (ticksUntilStart < 0) {
            throw new IllegalArgumentException("Ticks until start cannot be negative but was: " + ticksUntilStart);
        }
        TICKS_UNTIL_START = ticksUntilStart;
        STATE.tick(TICKS_UNTIL_START);

        FRAME_INDEX_MAPPER = requireNonNull(frameIndexMapper, "Frame index mapper cannot be null");

        INTERPOLATOR = requireNonNull(interpolator, "Interpolator cannot be null");
        INTERPOLATE_AREA = requireNonNull(interpolateArea, "Interpolate area cannot be null");

        SYNC_TICKS = syncTicks;
        if (!allowNegativeSyncTicks && SYNC_TICKS <= 0) {
            throw new IllegalArgumentException("Sync ticks cannot be zero or negative");
        }

        TIME_GETTER = requireNonNull(timeGetter, "Time getter cannot be null");
    }

}
