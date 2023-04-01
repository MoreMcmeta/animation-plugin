package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AnimationComponent}.
 * @author soir20
 */
public class AnimationComponentTest {
    private static final Interpolator INTERPOLATOR = new RGBAInterpolator();
    
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void build_MissingInterpolateArea_IllegalStateException() {
        expectedException.expect(IllegalStateException.class);
        new AnimationComponent.Builder()
                .frames(5)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();
    }

    @Test
    public void build_MissingFrames_IllegalStateException() {
        expectedException.expect(IllegalStateException.class);
        new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();
    }

    @Test
    public void build_MissingTicksUntilStart_IllegalStateException() {
        expectedException.expect(IllegalStateException.class);
        new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(5)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();
    }

    @Test
    public void build_MissingFrameTimeCalculator_IllegalStateException() {
        expectedException.expect(IllegalStateException.class);
        new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(5)
                .ticksUntilStart(0)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();
    }

    @Test
    public void build_MissingFrameIndexMapper_IllegalStateException() {
        expectedException.expect(IllegalStateException.class);
        new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(5)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .interpolator(INTERPOLATOR)
                .build();
    }

    @Test
    public void build_MissingInterpolator_IllegalStateException() {
        expectedException.expect(IllegalStateException.class);
        new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(5)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .build();
    }

    @Test
    public void build_NullInterpolateArea_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent.Builder().interpolateArea(null);
    }

    @Test
    public void build_NullFrameTimeCalculator_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent.Builder().frameTimeCalculator(null);
    }

    @Test
    public void build_NullIndexMapper_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent.Builder().frameIndexMapper(null);
    }

    @Test
    public void build_NullInterpolator_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent.Builder().interpolator(null);
    }

    @Test
    public void build_NullTimeGetter_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent.Builder().syncTicks(10, null);
    }

    @Test
    public void build_NegativeFrames_IllegalArgException() {
        expectedException.expect(IllegalArgumentException.class);
        new AnimationComponent.Builder().frames(-1);
    }

    @Test
    public void build_ZeroFrames_IllegalArgException() {
        expectedException.expect(IllegalArgumentException.class);
        new AnimationComponent.Builder().frames(-1);
    }

    @Test
    public void build_NegativeSkipTicks_IllegalArgException() {
        expectedException.expect(IllegalArgumentException.class);
        new AnimationComponent.Builder().ticksUntilStart(-1);
    }

    @Test
    public void build_NegativeSyncTicks_IllegalArgException() {
        expectedException.expect(IllegalArgumentException.class);
        new AnimationComponent.Builder().syncTicks(-1, () -> Optional.of(5L));
    }

    @Test
    @SuppressWarnings("OptionalAssignedToNull")
    public void tick_SyncedTimeGetterReturnsNull_NullPointerException() {
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(5)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(375, () -> null)
                .build();

        expectedException.expect(NullPointerException.class);
        component.onTick(new MockCurrentFrameView(), new MockPersistentFrameGroup(5));
    }

    @Test
    public void tick_NotSyncedLoop_SameAnimFrame() {
        int frames = 10;
        int animationLength = 550;
        AtomicInteger tick = new AtomicInteger(0);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        while (tick.getAndIncrement() < animationLength) {
            component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));
        }

        assertEquals(indexToColor(0), currentFrameView.color(0, 0));
    }

    @Test
    public void tick_NotSyncedPartWay_CorrectAnimFrame() {
        int frames = 10;
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        int animationLength = 330;
        for (int tick = 0; tick < animationLength; tick++) {
            component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));
        }

        assertEquals(
                INTERPOLATOR.interpolate(80, 50, indexToColor(7), indexToColor(8)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_NotSyncedPartWayWithSkipTicksLessThanFirstFrameLength_CorrectAnimFrame() {
        int frames = 10;
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(5)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        int animationLength = 330;
        for (int tick = 0; tick < animationLength; tick++) {
            component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));
        }

        assertEquals(
                INTERPOLATOR.interpolate(80, 55, indexToColor(7), indexToColor(8)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_NotSyncedPartWayWithSkipTicksMoreThanFirstFrameLength_CorrectAnimFrame() {
        int frames = 10;
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(15)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        int animationLength = 330;
        for (int tick = 0; tick < animationLength; tick++) {
            component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));
        }

        assertEquals(
                INTERPOLATOR.interpolate(80, 65, indexToColor(7), indexToColor(8)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_SyncsToSameLoopInitialTime_SameAnimFrame() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(800);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(10, 1, indexToColor(0), indexToColor(1)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_SyncsToSameNegativeInitialTime_SameAnimFrame() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(-1);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                indexToColor(0),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_SyncsForward_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(375);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(90, 15, indexToColor(8), indexToColor(9)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_SyncsBackward_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(-375);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(90, 65, indexToColor(8), indexToColor(9)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_SyncsForwardWithSkipTicksLessThanFirstFrameLength_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(375);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(5)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(90, 20, indexToColor(8), indexToColor(9)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_SyncsForwardWithSkipTicksMoreThanFirstFrameLength_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(375);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(15)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(90, 30, indexToColor(8), indexToColor(9)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_SyncsBackwardWithSkipTicksLessThanFirstFrameLength_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(-375);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(5)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(90, 70, indexToColor(8), indexToColor(9)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_SyncsBackwardWithSkipTicksMoreThanFirstFrameLength_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(-375);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(15)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(90, 81, indexToColor(8), indexToColor(9)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_VeryLargeSyncTicks_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(375);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(Integer.MAX_VALUE, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(90, 15, indexToColor(8), indexToColor(9)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_VeryLargeTime_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(Long.MAX_VALUE);
        AnimationComponent component = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .syncTicks(800, () -> Optional.of(currentTime.incrementAndGet()))
                .build();

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(60, 42, indexToColor(5), indexToColor(6)),
                currentFrameView.color(0, 0)
        );
    }

    public static int indexToColor(int index) {
        return Color.pack(indexToComp(index), indexToComp(index), indexToComp(index), indexToComp(index));
    }

    private static int indexToComp(int index) {
        return index * 10 % 256;
    }

}