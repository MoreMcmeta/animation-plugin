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
    private final Interpolator INTERPOLATOR;
    private final Area INTERPOLATE_AREA;
    private final int SYNC_TICKS;
    private final Supplier<Optional<Long>> TIME_GETTER;

    /**
     * Creates a new animation component for an animation that is synchronized to the level time.
     * @param interpolateArea           pixels to interpolate/modify during the animation
     * @param frames                    number of predefined frames in the animation
     * @param frameTimeCalculator       calculates the duration of each frame in ticks
     * @param interpolator              interpolates between colors
     * @param syncTicks                 number of ticks to sync to; e.g. 24000 to sync to a Minecraft day
     * @param timeGetter                retrieves the current time in the world, if any
     */
    public AnimationComponent(Area interpolateArea, int frames, IntUnaryOperator frameTimeCalculator,
                              Interpolator interpolator, int syncTicks, Supplier<Optional<Long>> timeGetter) {
        this(interpolateArea, frames, frameTimeCalculator, interpolator, syncTicks, timeGetter, false);
    }

    /**
     * Creates a new animation component for an animation that is not synchronized to the level time.
     * @param interpolateArea           pixels to interpolate/modify during the animation
     * @param frames                    number of predefined frames in the animation
     * @param frameTimeCalculator       calculates the duration of each frame in ticks
     * @param interpolator              interpolates between colors
     */
    public AnimationComponent(Area interpolateArea, int frames, IntUnaryOperator frameTimeCalculator,
                              Interpolator interpolator) {
        this(interpolateArea, frames, frameTimeCalculator, interpolator, -1, Optional::empty, true);
    }

    @Override
    public void onTick(CurrentFrameView currentFrame, FrameGroup<PersistentFrameView> predefinedFrames) {
        Optional<Long> timeOptional = TIME_GETTER.get();

        if (timeOptional.isPresent()) {
            long currentTime = timeOptional.get();
            int ticksToAdd = Math.floorMod(currentTime - STATE.ticks(), SYNC_TICKS);

            STATE.tick(ticksToAdd);
        } else {
            STATE.tick(1);
        }

        int startIndex = STATE.startIndex();
        int endIndex = STATE.endIndex();

        if (startIndex == endIndex) {
            currentFrame.replaceWith(startIndex);
            return;
        }

        currentFrame.generateWith(
                (overwriteX, overwriteY, dependencyFunction) -> INTERPOLATOR.interpolate(
                        STATE.frameMaxTime(),
                        STATE.frameTicks(),
                        predefinedFrames.frame(STATE.startIndex()).color(overwriteX, overwriteY),
                        predefinedFrames.frame(STATE.endIndex()).color(overwriteX, overwriteY)
                ),
                INTERPOLATE_AREA,
                INTERPOLATE_AREA
        );
    }

    /**
     * Creates a new animation component.
     * @param interpolateArea           pixels to interpolate/modify during the animation
     * @param frames                    number of predefined frames in the animation
     * @param frameTimeCalculator       calculates the duration of each frame in ticks
     * @param interpolator              interpolates between colors
     * @param syncTicks                 number of ticks to sync to; e.g. 24000 to sync to a Minecraft day
     * @param timeGetter                retrieves the current time in the world, if any
     * @param allowNegativeSyncTicks    whether sync ticks can be negative; true when the animation is not synchronized
     */
    private AnimationComponent(Area interpolateArea, int frames, IntUnaryOperator frameTimeCalculator,
                               Interpolator interpolator, int syncTicks, Supplier<Optional<Long>> timeGetter,
                               boolean allowNegativeSyncTicks) {
        requireNonNull(frameTimeCalculator, "Frame time calculator cannot be null");
        STATE = new AnimationState(frames, frameTimeCalculator);

        INTERPOLATOR = requireNonNull(interpolator, "Interpolator cannot be null");
        INTERPOLATE_AREA = requireNonNull(interpolateArea, "Interpolate area cannot be null");

        SYNC_TICKS = syncTicks;
        if (!allowNegativeSyncTicks && SYNC_TICKS <= 0) {
            throw new IllegalArgumentException("Sync ticks cannot be zero or negative");
        }

        TIME_GETTER = requireNonNull(timeGetter, "Time getter cannot be null");
    }

}
