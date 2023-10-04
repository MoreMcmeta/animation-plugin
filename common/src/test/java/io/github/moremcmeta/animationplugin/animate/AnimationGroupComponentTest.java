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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.moremcmeta.animationplugin.MockCurrentFrameView;
import io.github.moremcmeta.animationplugin.MockPersistentFrameGroup;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.moremcmeta.animationplugin.animate.AnimationComponentTest.indexToColor;
import static org.junit.Assert.*;

/**
 * Tests the {@link AnimationGroupComponent}.
 * @author soir20
 */
public final class AnimationGroupComponentTest {
    private static final Interpolator INTERPOLATOR = new DefaultAlphaInterpolator();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void construct_NullComponents_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationGroupComponent(null, ImmutableList.of());
    }

    @Test
    public void construct_NullClosers_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationGroupComponent(ImmutableList.of(), null);
    }

    @Test
    public void construct_ComponentsClosersEmpty_NoException() {
        AnimationGroupComponent component = new AnimationGroupComponent(ImmutableList.of(), ImmutableList.of());
        MockPersistentFrameGroup persistentFrames = new MockPersistentFrameGroup(5);
        component.onTick(new MockCurrentFrameView(), persistentFrames);
        component.onClose(new MockCurrentFrameView(), persistentFrames);
    }

    @Test
    public void tick_OneComponentUsesPredefined_ComponentTicked() {
        int frames = 10;
        AnimationComponent component1 = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();

        AnimationGroupComponent groupComponent = new AnimationGroupComponent(
                ImmutableList.of(Pair.of(component1, Optional.empty())),
                ImmutableList.of(() -> {}, () -> {})
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrames = new MockPersistentFrameGroup(frames);

        int animationLength = 330;
        for (int tick = 0; tick < animationLength; tick++) {
            groupComponent.onTick(currentFrameView, persistentFrames, 1);
        }

        assertEquals(
                INTERPOLATOR.interpolate(80, 50, indexToColor(7), indexToColor(8)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_OneComponentUsesPartFrames_ComponentTicked() {
        int frames = 10;
        AnimationComponent component1 = new AnimationComponent.Builder()
                .interpolateArea(Area.of(Point.pack(0, 0)))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();

        // Use different color values to differentiate from the predefined frames
        AnimationGroupComponent groupComponent = new AnimationGroupComponent(
                ImmutableList.of(Pair.of(component1, Optional.of(ImmutableList.of(
                        (x, y) -> indexToColor(10),
                        (x, y) -> indexToColor(11),
                        (x, y) -> indexToColor(12),
                        (x, y) -> indexToColor(13),
                        (x, y) -> indexToColor(14),
                        (x, y) -> indexToColor(15),
                        (x, y) -> indexToColor(16),
                        (x, y) -> indexToColor(17),
                        (x, y) -> indexToColor(18),
                        (x, y) -> indexToColor(19)
                )))),
                ImmutableList.of(() -> {}, () -> {})
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrames = new MockPersistentFrameGroup(frames);

        int animationLength = 330;
        for (int tick = 0; tick < animationLength; tick++) {
            groupComponent.onTick(currentFrameView, persistentFrames, 1);
        }

        assertEquals(
                INTERPOLATOR.interpolate(80, 50, indexToColor(17), indexToColor(18)),
                currentFrameView.color(0, 0)
        );
    }

    @Test
    public void tick_MixedComponents_ComponentsTickedInOrder() {
        int frames = 10;
        AnimationComponent component1 = new AnimationComponent.Builder()
                .interpolateArea(new Area(0, 0, 10, 20))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();
        AnimationComponent component2 = new AnimationComponent.Builder()
                .interpolateArea(new Area(5, 10, 5, 5))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .coordinateInBase(5, 10)
                .build();
        AnimationComponent component3 = new AnimationComponent.Builder()
                .interpolateArea(new Area(0, 0, 7, 13))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .build();
        AnimationComponent component4 = new AnimationComponent.Builder()
                .interpolateArea(new Area(8, 14, 2, 5))
                .frames(frames)
                .ticksUntilStart(0)
                .frameTimeCalculator((frame) -> (frame + 1) * 10)
                .frameIndexMapper((frame) -> frame)
                .interpolator(INTERPOLATOR)
                .coordinateInBase(8, 14)
                .build();

        // Use different color values to differentiate from the predefined frames
        AnimationGroupComponent groupComponent = new AnimationGroupComponent(
                ImmutableList.of(
                        Pair.of(component1, Optional.empty()),
                        Pair.of(component2, Optional.of(ImmutableList.of(
                                (x, y) -> indexToColor(10),
                                (x, y) -> indexToColor(11),
                                (x, y) -> indexToColor(12),
                                (x, y) -> indexToColor(13),
                                (x, y) -> indexToColor(14),
                                (x, y) -> indexToColor(15),
                                (x, y) -> indexToColor(16),
                                (x, y) -> indexToColor(17),
                                (x, y) -> indexToColor(18),
                                (x, y) -> indexToColor(19)
                        ))),
                        Pair.of(component3, Optional.empty()),
                        Pair.of(component4, Optional.of(ImmutableList.of(
                                (x, y) -> indexToColor(20),
                                (x, y) -> indexToColor(21),
                                (x, y) -> indexToColor(22),
                                (x, y) -> indexToColor(23),
                                (x, y) -> indexToColor(24),
                                (x, y) -> indexToColor(25),
                                (x, y) -> indexToColor(26),
                                (x, y) -> indexToColor(27),
                                (x, y) -> indexToColor(28),
                                (x, y) -> indexToColor(29)
                        )))
                ),
                ImmutableList.of(() -> {}, () -> {})
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrames = new MockPersistentFrameGroup(frames);

        int animationLength = 330;
        for (int tick = 0; tick < animationLength; tick++) {
            groupComponent.onTick(currentFrameView, persistentFrames, 1);
        }

        assertEquals(
                INTERPOLATOR.interpolate(80, 50, indexToColor(7), indexToColor(8)),
                currentFrameView.color(9, 19)
        );
        assertEquals(
                INTERPOLATOR.interpolate(80, 50, indexToColor(17), indexToColor(18)),
                currentFrameView.color(7, 13)
        );
        assertEquals(
                INTERPOLATOR.interpolate(80, 50, indexToColor(7), indexToColor(8)),
                currentFrameView.color(6, 12)
        );
        assertEquals(
                INTERPOLATOR.interpolate(80, 50, indexToColor(27), indexToColor(28)),
                currentFrameView.color(8, 14)
        );
    }

    @Test
    public void close_MultipleClosers_AllClosersRun() {
        AtomicBoolean closer1 = new AtomicBoolean();
        AtomicBoolean closer2 = new AtomicBoolean();
        AtomicBoolean closer3 = new AtomicBoolean();
        AtomicBoolean closer4 = new AtomicBoolean();

        // Use different color values to differentiate from the predefined frames
        AnimationGroupComponent groupComponent = new AnimationGroupComponent(
                ImmutableList.of(),
                ImmutableList.of(
                        () -> closer1.set(true),
                        () -> closer2.set(true),
                        () -> closer3.set(true),
                        () -> closer4.set(true)
                )
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrames = new MockPersistentFrameGroup(5);

        groupComponent.onClose(currentFrameView, persistentFrames);

        assertTrue(closer1.get());
        assertTrue(closer2.get());
        assertTrue(closer3.get());
        assertTrue(closer4.get());
    }

}