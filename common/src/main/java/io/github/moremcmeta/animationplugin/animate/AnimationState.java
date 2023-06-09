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

import java.util.function.IntUnaryOperator;

import static java.util.Objects.requireNonNull;

/**
 * Keeps track of the current frame in an animation.
 * @author soir20
 */
public class AnimationState {
    private final int FRAMES;
    private final IntUnaryOperator FRAME_TIME_CALCULATOR;

    private int ticksInThisFrame;
    private int currentFrameIndex;
    private int currentFrameMaxTime;
    private long allTimeTicks;

    /**
     * Creates an animation state.
     * @param frames                number of frames in the animation. Must be positive.
     * @param frameTimeCalculator   calculates the frame time for a given frame.
     *                              Only called once per frame per loop of the animation.
     *                              Must return values greater than 0 for all frames.
     *                              In most cases, pass a function that gets the
     *                              time from the frame or returns a default value.
     */
    public AnimationState(int frames, IntUnaryOperator frameTimeCalculator) {
        if (frames <= 0) {
            throw new IllegalArgumentException("Frames cannot have no frames");
        }

        FRAMES = frames;
        FRAME_TIME_CALCULATOR = requireNonNull(frameTimeCalculator, "Frame time calculator cannot be null");

        currentFrameMaxTime = calcMaxFrameTime(0);
    }

    /**
     * Gets the index of the current frame, which is the frame to start interpolation at.
     * @return index of the current frame
     */
    public int startIndex() {
        return currentFrameIndex;
    }

    /**
     * Gets the index of the frame to end interpolation at. If the animation is at the first tick of the
     * current frame, the current frame is returned, since there should be no interpolation.
     * @return index of the frame to end interpolation at
     */
    public int endIndex() {
        return (currentFrameIndex + 1) % FRAMES;
    }

    /**
     * Gets the number of ticks in the current frame so far.
     * @return number of ticks in the current frame
     */
    public int frameTicks() {
        return ticksInThisFrame;
    }

    /**
     * Gets the maximum number of ticks in the current frame.
     * @return maximum number of ticks in the current frame
     */
    public int frameMaxTime() {
        return currentFrameMaxTime;
    }

    /**
     * Gets the total number of ticks in the lifetime of this state.
     * @return total number of ticks
     */
    public long ticks() {
        return allTimeTicks;
    }

    /**
     * Ticks the current animation by several ticks.
     * @param ticks      how many ticks ahead to put the animation
     */
    public void tick(int ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Ticks cannot be less than zero");
        }

        allTimeTicks += ticks;

        // Calculate the predefined frame in the animation at the given tick
        int timeLeftUntilTick = ticksInThisFrame + ticks;
        int frameIndex = currentFrameIndex;
        int frameTime = currentFrameMaxTime;

        // When the frame time is equal to the time left, the tick is at the start of the next frame
        while (frameTime <= timeLeftUntilTick) {
            timeLeftUntilTick -= frameTime;
            frameIndex = (frameIndex + 1) % FRAMES;
            frameTime = calcMaxFrameTime(frameIndex);
        }

        currentFrameIndex = frameIndex;
        currentFrameMaxTime = frameTime;
        ticksInThisFrame = timeLeftUntilTick;
    }

    /**
     * Calculates the maximum time for a frame at a certain index.
     * @param frameIndex    the index of the frame
     * @return  the maximum time of this frame
     */
    private int calcMaxFrameTime(int frameIndex) {
        int maxTime = FRAME_TIME_CALCULATOR.applyAsInt(frameIndex);

        if (maxTime <= 0) {
            throw new UnsupportedOperationException("Frame times must be greater than 0");
        }

        return maxTime;
    }

}