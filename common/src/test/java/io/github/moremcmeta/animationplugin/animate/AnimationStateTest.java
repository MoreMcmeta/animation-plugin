package io.github.moremcmeta.animationplugin.animate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AnimationState}.
 * @author soir20
 */
public class AnimationStateTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void construct_FrameTimeZero_UnsupportedOpException() {
        expectedException.expect(UnsupportedOperationException.class);
        new AnimationState(5, (frame) -> 0);
    }

    @Test
    public void construct_FrameTimeNegative_UnsupportedOpException() {
        expectedException.expect(UnsupportedOperationException.class);
        new AnimationState(5, (frame) -> -1);
    }

    @Test
    public void construct_NoFrames_IllegalArgException() {
        expectedException.expect(IllegalArgumentException.class);
        new AnimationState(0, (frame) -> 10);
    }
    @Test
    public void construct_NullTimeCalculator_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationState(3, null);
    }
    
    @Test
    public void tickAnimation_FrameTimeZero_UnsupportedOpException() {
        AnimationState state = new AnimationState(5, (frame) -> frame == 0 ? 1 : 0);
        expectedException.expect(UnsupportedOperationException.class);
        state.tick(1);
    }

    @Test
    public void tickAnimation_FrameTimeNegative_UnsupportedOpException() {
        expectedException.expect(UnsupportedOperationException.class);
        new AnimationState(5, (frame) -> frame == 1 ? 1 : -1);
    }

    @Test
    public void tickAnimation_NoTicks_FirstFrame() {
        AnimationState state = new AnimationState(5, (frame) -> 10);
        assertEquals(0, state.startIndex());
        assertEquals(1, state.endIndex());
        assertEquals(0, state.frameTicks());
        assertEquals(10, state.frameMaxTime());
        assertEquals(0, state.ticks());
    }

    @Test
    public void tickAnimation_MaxTicksInEachFrame_FrameChanges() {
        int frames = 5;
        int frameLength = 10;
        AnimationState state = new AnimationState(frames, (frame) -> frameLength);

        for (int nextFrame = 1; nextFrame < frames; nextFrame++) {
            for (int tick = 0; tick < frameLength; tick++) {
                state.tick(1);
            }
            assertEquals(nextFrame, state.startIndex());
            assertEquals((nextFrame + 1) % frames, state.endIndex());
            assertEquals(0, state.frameTicks());
            assertEquals(frameLength, state.frameMaxTime());
            assertEquals(nextFrame * frameLength, state.ticks());
        }
    }

    @Test
    public void tickAnimation_FrameTimeVaries_SecondFrameLastsCorrectTime() {
        int frameLength = 10;
        AnimationState state = new AnimationState(5, (frame) -> (frame + 1) * frameLength);

        for (int tick = 0; tick < frameLength * 3; tick++) {
            state.tick(1);
        }
        assertEquals(2, state.startIndex());
        assertEquals(3, state.endIndex());
        assertEquals(0, state.frameTicks());
        assertEquals(30, state.frameMaxTime());
        assertEquals(30, state.ticks());
    }

    @Test
    public void tickAnimation_MaxTicksInAnimation_AnimationResetsAtBeginning() {
        int frames = 5;
        int frameLength = 10;
        AnimationState state = new AnimationState(frames, (frame) -> frameLength);
        for (int tick = 0; tick < frameLength * frames; tick++) {
            state.tick(1);
        }

        assertEquals(0, state.startIndex());
        assertEquals(1, state.endIndex());
        assertEquals(0, state.frameTicks());
        assertEquals(10, state.frameMaxTime());
        assertEquals(frames * frameLength, state.ticks());
    }

    @Test
    public void tickAnimation_MaxTicksInSingleFrameAnimation_AnimationResetsAtBeginning() {
        int frames = 1;
        int frameLength = 10;
        AnimationState state = new AnimationState(frames, (frame) -> frameLength);
        for (int tick = 0; tick < frameLength * frames; tick++) {
            state.tick(1);
            assertEquals(0, state.endIndex());
        }

        assertEquals(0, state.startIndex());
        assertEquals(0, state.endIndex());
        assertEquals(0, state.frameTicks());
        assertEquals(10, state.frameMaxTime());
        assertEquals(frames * frameLength, state.ticks());
    }

    @Test
    public void tickAnimation_AfterFirstTickInEachFrame_HasCorrectInterpolationValues() {
        int frames = 5;
        int frameLength = 10;
        AnimationState state = new AnimationState(frames, (frame) -> frameLength);

        for (int frame = 0; frame < frames; frame++) {
            for (int tick = 1; tick < frameLength; tick++) {
                state.tick(1);
                assertEquals(frame, state.startIndex());
                assertEquals((frame + 1) % frames, state.endIndex());
                assertEquals(tick, state.frameTicks());
                assertEquals(frameLength, state.frameMaxTime());
                assertEquals(frame * frameLength + tick, state.ticks());
            }

            state.tick(1);
        }
    }

    @Test
    public void tickAnimationSeveral_TickNegative_IllegalArgumentException() {
        AnimationState state = new AnimationState(5, (frame) -> frame == 0 ? 1 : 0);
        expectedException.expect(IllegalArgumentException.class);
        state.tick(-1);
    }

    @Test
    public void tickAnimationSeveral_FrameTimeZero_UnsupportedOpException() {
        AnimationState state = new AnimationState(5, (frame) -> frame == 0 ? 5 : 0);
        expectedException.expect(UnsupportedOperationException.class);
        state.tick(5);
    }

    @Test
    public void tickAnimationSeveral_FrameTimeNegative_UnsupportedOpException() {
        AnimationState state = new AnimationState(5, (frame) -> frame == 0 ? 1 : -1);
        expectedException.expect(UnsupportedOperationException.class);
        state.tick(5);
    }

    @Test
    public void tickAnimationSeveral_TickZero_NoChange() {
        int frameTime = 10;
        AnimationState state = new AnimationState(5, (frame) -> frameTime);

        state.tick(15);
        state.tick(0);
        assertEquals(1, state.startIndex());
        assertEquals(2, state.endIndex());
        assertEquals(5, state.frameTicks());
        assertEquals(frameTime, state.frameMaxTime());
        assertEquals(15, state.ticks());
    }

    @Test
    public void tickAnimationSeveral_MaxTicksInEachFrame_FrameChanges() {
        int frames = 5;
        int frameLength = 10;
        AnimationState state = new AnimationState(frames, (frame) -> frameLength);

        for (int nextFrame = 1; nextFrame < frames; nextFrame++) {
            int ticksPerIteration = 5;
            for (int tick = 0; tick < frameLength / ticksPerIteration; tick++) {
                state.tick(ticksPerIteration);
            }
            assertEquals(nextFrame, state.startIndex());
            assertEquals((nextFrame + 1) % frames, state.endIndex());
            assertEquals(0, state.frameTicks());
            assertEquals(frameLength, state.frameMaxTime());
            assertEquals(frameLength * nextFrame, state.ticks());
        }
    }

    @Test
    public void tickAnimationSeveral_FrameTimeVaries_SecondFrameLastsCorrectTime() {
        int frameLength = 10;
        AnimationState state = new AnimationState(5, (frame) -> (frame + 1) * frameLength);

        int ticksPerIteration = 5;
        for (int tick = 0; tick < (frameLength * 3) / ticksPerIteration; tick++) {
            state.tick(ticksPerIteration);
        }

        assertEquals(2, state.startIndex());
        assertEquals(3, state.endIndex());
        assertEquals(0, state.frameTicks());
        assertEquals(30, state.frameMaxTime());
        assertEquals(30, state.ticks());
    }

    @Test
    public void tickAnimationSeveral_MaxTicksInAnimation_AnimationResetsAtBeginning() {
        int frames = 5;
        int frameLength = 10;
        AnimationState state = new AnimationState(frames, (frame) -> frameLength);

        int ticksPerIteration = 5;
        for (int tick = 0; tick < frameLength * frames / ticksPerIteration; tick++) {
            state.tick(ticksPerIteration);
        }

        assertEquals(0, state.startIndex());
        assertEquals(1, state.endIndex());
        assertEquals(0, state.frameTicks());
        assertEquals(frameLength, state.frameMaxTime());
        assertEquals(frames * frameLength, state.ticks());
    }

    @Test
    public void tickAnimationSeveral_MaxTicksInSingleFrameAnimation_AnimationResetsAtBeginning() {
        int frames = 1;
        int frameLength = 10;
        AnimationState state = new AnimationState(frames, (frame) -> frameLength);

        int ticksPerIteration = 5;
        for (int tick = 0; tick < frameLength * frames / ticksPerIteration; tick++) {
            state.tick(ticksPerIteration);
            assertEquals(0, state.endIndex());
        }

        assertEquals(0, state.startIndex());
        assertEquals(0, state.endIndex());
        assertEquals(0, state.frameTicks());
        assertEquals(frameLength, state.frameMaxTime());
        assertEquals(frames * frameLength, state.ticks());
    }

    @Test
    public void tickAnimationSeveral_AfterFirstTickInEachFrame_HasCorrectInterpolationValues() {
        int frames = 5;
        int frameLength = 100;
        AnimationState state = new AnimationState(frames, (frame) -> frameLength);

        for (int frame = 0; frame < frames; frame++) {
            int ticksPerIteration = 5;
            for (int tick = 1; tick < frameLength / ticksPerIteration; tick++) {
                state.tick(ticksPerIteration);

                int ticksInFrame = tick * ticksPerIteration;
                assertEquals(frame, state.startIndex());
                assertEquals((frame + 1) % frames, state.endIndex());
                assertEquals(ticksInFrame, state.frameTicks());
                assertEquals(frameLength, state.frameMaxTime());
                assertEquals(frame * frameLength + ticksInFrame, state.ticks());
            }

            state.tick(ticksPerIteration);
        }
    }

}