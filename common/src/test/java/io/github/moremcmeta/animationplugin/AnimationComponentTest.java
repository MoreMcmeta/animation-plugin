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

import static org.junit.Assert.*;

/**
 * Tests the {@link AnimationComponent}.
 * @author soir20
 */
public class AnimationComponentTest {
    private static final Interpolator INTERPOLATOR = new RGBAInterpolator();
    
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void construct_NotSyncedNullInterpolateArea_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent(null, 5, (frame) -> 10, INTERPOLATOR);
    }

    @Test
    public void construct_NotSyncedNullFrameTimeCalculator_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent(Area.of(new Point(0, 0)), 5, null, INTERPOLATOR);
    }

    @Test
    public void construct_NotSyncedNullInterpolator_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent(Area.of(new Point(0, 0)), 5, (frame) -> 10, null);
    }

    @Test
    public void construct_SyncedNullInterpolateArea_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent(null, 5, (frame) -> 10, INTERPOLATOR,
                24000, () -> Optional.of(10L));
    }

    @Test
    public void construct_SyncedNullFrameTimeCalculator_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent(Area.of(new Point(0, 0)), 5, null, INTERPOLATOR,
                24000, () -> Optional.of(10L));
    }

    @Test
    public void construct_SyncedNullInterpolator_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponent(Area.of(new Point(0, 0)), 5, (frame) -> 10, null,
                24000, () -> Optional.of(10L));
    }

    @Test
    public void construct_SyncedNegativeTicks_IllegalArgException() {
        AtomicLong currentTime = new AtomicLong(800);
        expectedException.expect(IllegalArgumentException.class);
        new AnimationComponent(Area.of(new Point(0, 0)), 5, (frame) -> 10, INTERPOLATOR,
                -1, () -> Optional.of(currentTime.incrementAndGet()));
    }

    @Test
    public void construct_SyncedZeroTicks_IllegalArgException() {
        AtomicLong currentTime = new AtomicLong(800);
        expectedException.expect(IllegalArgumentException.class);
        new AnimationComponent(Area.of(new Point(0, 0)), 5, (frame) -> 10, INTERPOLATOR,
                0, () -> Optional.of(currentTime.incrementAndGet()));
    }

    @Test
    @SuppressWarnings("OptionalAssignedToNull")
    public void tick_SyncedTimeGetterReturnsNull_NullPointerException() {
        AnimationComponent component = new AnimationComponent(Area.of(new Point(0, 0)), 5, (frame) -> 10,
                INTERPOLATOR, 375, () -> null);
        expectedException.expect(NullPointerException.class);
        component.onTick(new MockCurrentFrameView(), new MockPersistentFrameGroup(5));
    }

    @Test
    public void tick_NotSyncedLoop_SameAnimFrame() {
        int frames = 10;
        int animationLength = 550;
        AtomicInteger tick = new AtomicInteger(0);
        AnimationComponent component = new AnimationComponent(
                Area.of(new Point(0, 0)),
                frames,
                (frame) -> (frame + 1) * 10,
                INTERPOLATOR
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        while (tick.getAndIncrement() < animationLength) {
            component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));
        }

        assertEquals(indexToColor(0), currentFrameView.color(0, 0));
    }

    @Test
    public void tick_NotSyncedPartWay_CorrectAnimFrame() {
        int frames = 10;
        AnimationComponent component = new AnimationComponent(
                Area.of(new Point(0, 0)),
                frames,
                (frame) -> (frame + 1) * 10,
                INTERPOLATOR
        );

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
    public void tick_SyncsToSameLoopInitialTime_SameAnimFrame() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(800);
        AnimationComponent component = new AnimationComponent(
                Area.of(new Point(0, 0)),
                frames,
                (frame) -> (frame + 1) * 10,
                INTERPOLATOR,
                800,
                () -> Optional.of(currentTime.incrementAndGet())
        );

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
        AnimationComponent component = new AnimationComponent(
                Area.of(new Point(0, 0)),
                frames,
                (frame) -> (frame + 1) * 10,
                INTERPOLATOR,
                800,
                () -> Optional.of(currentTime.incrementAndGet())
        );

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
        AnimationComponent component = new AnimationComponent(
                Area.of(new Point(0, 0)),
                frames,
                (frame) -> (frame + 1) * 10,
                INTERPOLATOR,
                800,
                () -> Optional.of(currentTime.incrementAndGet())
        );

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
        AnimationComponent component = new AnimationComponent(
                Area.of(new Point(0, 0)),
                frames,
                (frame) -> (frame + 1) * 10,
                INTERPOLATOR,
                800,
                () -> Optional.of(currentTime.incrementAndGet())
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(90, 65, indexToColor(8), indexToColor(9)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_VeryLargeSyncTicks_FrameAtTime() {
        int frames = 10;
        AtomicLong currentTime = new AtomicLong(375);
        AnimationComponent component = new AnimationComponent(
                Area.of(new Point(0, 0)),
                frames,
                (frame) -> (frame + 1) * 10,
                INTERPOLATOR,
                Integer.MAX_VALUE,
                () -> Optional.of(currentTime.incrementAndGet())
        );

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
        AnimationComponent component = new AnimationComponent(
                Area.of(new Point(0, 0)),
                frames,
                (frame) -> (frame + 1) * 10,
                INTERPOLATOR,
                800,
                () -> Optional.of(currentTime.incrementAndGet())
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        component.onTick(currentFrameView, new MockPersistentFrameGroup(frames));

        assertEquals(
                INTERPOLATOR.interpolate(60, 42, indexToColor(5), indexToColor(6)),
                currentFrameView.color(0, 0)
        );
    }

    public static Color indexToColor(int index) {
        return new Color(indexToComp(index), indexToComp(index), indexToComp(index), indexToComp(index));
    }

    private static int indexToComp(int index) {
        return index * 10 % 256;
    }

}