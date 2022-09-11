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

public class AnimationComponent implements TextureComponent<CurrentFrameView, UploadableFrameView> {
    private final AnimationState STATE;
    private final Interpolator INTERPOLATOR;
    private final Area INTERPOLATE_AREA;
    private final int SYNC_TICKS;
    private final Supplier<Optional<Long>> TIME_GETTER;

    public AnimationComponent(Area interpolateArea, int frames, IntUnaryOperator frameTimeCalculator,
                              int syncTicks, Supplier<Optional<Long>> timeGetter) {
        this(interpolateArea, frames, frameTimeCalculator, syncTicks, timeGetter, false);
    }

    public AnimationComponent(Area interpolateArea, int frames, IntUnaryOperator frameTimeCalculator) {
        this(interpolateArea, frames, frameTimeCalculator, -1, Optional::empty, true);
    }

    private AnimationComponent(Area interpolateArea, int frames, IntUnaryOperator frameTimeCalculator,
                              int syncTicks, Supplier<Optional<Long>> timeGetter, boolean allowNegativeSyncTicks) {
        requireNonNull(frameTimeCalculator, "Frame time calculator cannot be null");
        STATE = new AnimationState(frames, frameTimeCalculator);

        INTERPOLATOR = new RGBAInterpolator();
        INTERPOLATE_AREA = requireNonNull(interpolateArea, "Interpolate area cannot be null");

        SYNC_TICKS = syncTicks;
        if (!allowNegativeSyncTicks && SYNC_TICKS <= 0) {
            throw new IllegalArgumentException("Sync ticks cannot be zero or negative");
        }

        TIME_GETTER = requireNonNull(timeGetter, "Time getter cannot be null");
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
                (overwritePoint, dependencyFunction) -> INTERPOLATOR.interpolate(
                        STATE.frameMaxTime(),
                        STATE.frameTicks(),
                        predefinedFrames.frame(STATE.startIndex()).color(overwritePoint.x(), overwritePoint.y()),
                        predefinedFrames.frame(STATE.endIndex()).color(overwritePoint.x(), overwritePoint.y())
                ),
                INTERPOLATE_AREA,
                INTERPOLATE_AREA
        );
    }
}
