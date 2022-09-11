package io.github.moremcmeta.animationplugin;

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
    private int allTimeTicks;

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

    public int startIndex() {
        return currentFrameIndex;
    }

    public int endIndex() {
        return ticksInThisFrame == 0 ? currentFrameIndex : (currentFrameIndex + 1) % FRAMES;
    }

    public int frameTicks() {
        return ticksInThisFrame;
    }

    public int frameMaxTime() {
        return currentFrameMaxTime;
    }

    public int ticks() {
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