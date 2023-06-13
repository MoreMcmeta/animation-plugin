/*
 * MoreMcmeta is a Minecraft mod expanding texture configuration capabilities.
 * Copyright (C) 2023 soir20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.moremcmeta.animationplugin.animate;

import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;

import java.util.List;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Manages a single animation in a group of animations.
 * @author soir20
 */
public final class AnimationComponent {
    private final AnimationState STATE;
    private final int TICKS_UNTIL_START;
    private final IntUnaryOperator FRAME_INDEX_MAPPER;
    private final Interpolator INTERPOLATOR;
    private final Area INTERPOLATE_AREA;
    private final int SYNC_TICKS;
    private final Supplier<Optional<Long>> TIME_GETTER;
    private final int X_IN_BASE;
    private final int Y_IN_BASE;

    /**
     * Updates the animation on tick.
     * @param currentFrame          current frame of the animated texture (to which all animations write)
     * @param predefinedFrames      predefined frames in the base texture
     */
    public void onTick(CurrentFrameView currentFrame, List<Frame> predefinedFrames) {
        Optional<Long> timeOptional = TIME_GETTER.get();

        if (timeOptional.isPresent()) {
            long currentTime = timeOptional.get();
            int ticksToAdd = (int) Math.floorMod(currentTime - STATE.ticks(), (long) SYNC_TICKS) + TICKS_UNTIL_START;

            STATE.tick(ticksToAdd);
        } else {
            STATE.tick(1);
        }

        int startIndex = FRAME_INDEX_MAPPER.applyAsInt(STATE.startIndex());
        int endIndex = FRAME_INDEX_MAPPER.applyAsInt(STATE.endIndex());

        currentFrame.generateWith(
                (overwriteX, overwriteY, dependencyFunction) -> INTERPOLATOR.interpolate(
                        STATE.frameMaxTime(),
                        STATE.frameTicks(),
                        predefinedFrames.get(startIndex).color(overwriteX - X_IN_BASE, overwriteY - Y_IN_BASE),
                        predefinedFrames.get(endIndex).color(overwriteX - X_IN_BASE, overwriteY - Y_IN_BASE)
                ),
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
     * @param xInBase                   x-coordinate of the top-left corner of this animation within the base texture
     * @param yInBase                   y-coordinate of the top-left corner of this animation within the base texture
     */
    private AnimationComponent(Area interpolateArea, int frames, int ticksUntilStart,
                               IntUnaryOperator frameTimeCalculator, IntUnaryOperator frameIndexMapper,
                               Interpolator interpolator, int syncTicks, Supplier<Optional<Long>> timeGetter,
                               int xInBase, int yInBase) {
        STATE = new AnimationState(frames, frameTimeCalculator);
        TICKS_UNTIL_START = ticksUntilStart;
        STATE.tick(TICKS_UNTIL_START);

        FRAME_INDEX_MAPPER = frameIndexMapper;
        INTERPOLATOR = interpolator;
        INTERPOLATE_AREA = interpolateArea;

        SYNC_TICKS = syncTicks;
        TIME_GETTER = timeGetter;

        X_IN_BASE = xInBase;
        Y_IN_BASE = yInBase;
    }

    /**
     * Builder to create new {@link AnimationComponent}s.
     * @author soir20
     */
    public static final class Builder {
        private Area interpolateArea;
        private int frames = -1;
        private int ticksUntilStart = -1;
        private IntUnaryOperator frameTimeCalculator;
        private IntUnaryOperator frameIndexMapper;
        private Interpolator interpolator;
        private int syncTicks = -1;
        private Supplier<Optional<Long>> timeGetter = Optional::empty;
        private int xInBase;
        private int yInBase;

        /**
         * Sets the interpolate area for this builder (required).
         * @param interpolateArea       pixels to interpolate/modify during the animation
         * @return this builder
         */
        public Builder interpolateArea(Area interpolateArea) {
            this.interpolateArea = requireNonNull(interpolateArea, "Interpolate area cannot be null");
            return this;
        }

        /**
         * Sets the number of frames for this builder (required).
         * @param frames                number of predefined frames in the animation
         * @return this builder
         */
        public Builder frames(int frames) {
            if (frames <= 0) {
                throw new IllegalArgumentException("Frames cannot be zero or negative but was: " + frames);
            }
            this.frames = frames;
            return this;
        }

        /**
         * Sets the number of ticks until the start of the animation for this builder (required).
         * @param ticks                ticks between the first tick in the first frame and the start of the animation
         * @return this builder
         */
        public Builder ticksUntilStart(int ticks) {
            if (ticks < 0) {
                throw new IllegalArgumentException("Ticks until start cannot be negative but was: " + ticksUntilStart);
            }
            this.ticksUntilStart = ticks;
            return this;
        }

        /**
         * Sets the frame time calculator for this builder (required).
         * @param frameTimeCalculator   calculates the duration of each frame in ticks
         * @return this builder
         */
        public Builder frameTimeCalculator(IntUnaryOperator frameTimeCalculator) {
            this.frameTimeCalculator = requireNonNull(frameTimeCalculator, "Frame time calculator cannot be null");
            return this;
        }

        /**
         * Sets the frame index mapper for this builder (required).
         * @param frameIndexMapper      maps frame indices to the index of the corresponding predefined frame
         * @return this builder
         */
        public Builder frameIndexMapper(IntUnaryOperator frameIndexMapper) {
            this.frameIndexMapper = requireNonNull(frameIndexMapper, "Frame index mapper cannot be null");
            return this;
        }

        /**
         * Sets the interpolator for this builder (required).
         * @param interpolator          interpolates between colors
         * @return this builder
         */
        public Builder interpolator(Interpolator interpolator) {
            this.interpolator = requireNonNull(interpolator, "Interpolator cannot be null");
            return this;
        }

        /**
         * Sets the number of sync ticks and time getter for this builder (optional).
         * @param syncTicks             number of ticks to sync to; e.g. 24000 to sync to a Minecraft day
         * @param timeGetter            retrieves the current time in the world, if any
         * @return this builder
         */
        public Builder syncTicks(int syncTicks, Supplier<Optional<Long>> timeGetter) {
            if (syncTicks <= 0) {
                throw new IllegalArgumentException("Sync ticks cannot be zero or negative but was: " + syncTicks);
            }
            this.syncTicks = syncTicks;
            this.timeGetter = requireNonNull(timeGetter, "Time getter cannot be null");

            return this;
        }

        /**
         * Sets the coordinate of the top-left corner of this animation within the base texture.
         * @param x     x-coordinate of the top-left corner of this animation within the base texture
         * @param y     y-coordinate of the top-left corner of this animation within the base texture
         * @return this builder
         */
        public Builder coordinateInBase(int x, int y) {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Coordinate in base cannot be negative: " + ticksUntilStart);
            }
            this.xInBase = x;
            this.yInBase = y;
            return this;
        }

        /**
         * Builds an {@link AnimationComponent} from the values provided to the builder. The interpolate area,
         * frames, ticks until start, frame time calculator, frame index mapper, and interpolator must have
         * been provided.
         * @return an {@link AnimationComponent} based on the provided values
         */
        public AnimationComponent build() {
            if (interpolateArea == null) {
                throw new IllegalStateException("Interpolate area is required");
            }

            if (frames < 0) {
                throw new IllegalStateException("Frame count is required");
            }

            if (ticksUntilStart < 0) {
                throw new IllegalStateException("Number of ticks until start is required");
            }

            if (frameTimeCalculator == null) {
                throw new IllegalStateException("Frame time calculator is required");
            }

            if (frameIndexMapper == null) {
                throw new IllegalStateException("Frame index mapper is required");
            }

            if (interpolator == null) {
                throw new IllegalStateException("Interpolator is required");
            }

            return new AnimationComponent(
                    interpolateArea,
                    frames,
                    ticksUntilStart,
                    frameTimeCalculator,
                    frameIndexMapper,
                    interpolator,
                    syncTicks,
                    timeGetter,
                    xInBase,
                    yInBase
            );
        }

    }

}
