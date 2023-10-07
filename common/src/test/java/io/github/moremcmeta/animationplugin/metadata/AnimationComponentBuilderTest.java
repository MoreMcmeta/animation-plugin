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

package io.github.moremcmeta.animationplugin.metadata;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.moremcmeta.animationplugin.MockCurrentFrameView;
import io.github.moremcmeta.animationplugin.MockMutableFrameGroup;
import io.github.moremcmeta.animationplugin.MockMutableFrameView;
import io.github.moremcmeta.animationplugin.MockPersistentFrameGroup;
import io.github.moremcmeta.animationplugin.animate.DefaultAlphaInterpolator;
import io.github.moremcmeta.animationplugin.animate.SmoothAlphaInterpolator;
import io.github.moremcmeta.moremcmeta.api.client.metadata.AnalyzedMetadata;
import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.FrameGroup;
import io.github.moremcmeta.moremcmeta.api.client.texture.MutableFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.TextureComponent;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static io.github.moremcmeta.animationplugin.animate.AnimationComponentTest.indexToColor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link AnimationComponentBuilder}.
 * @author soir20
 */
public final class AnimationComponentBuilderTest {
    private static final Supplier<MockMutableFrameGroup> MOCK_FRAME_GROUP = () -> new MockMutableFrameGroup(
            new MockMutableFrameView(Pair.of(Color.pack(10, 10, 10, 10), Area.of(Point.pack(0, 1), Point.pack(1, 1)))),
            new MockMutableFrameView(Pair.of(Color.pack(20, 20, 20, 20), Area.of(Point.pack(0, 1), Point.pack(0, 0)))),
            new MockMutableFrameView(Pair.of(Color.pack(30, 30, 30, 30), Area.of(Point.pack(0, 1), Point.pack(9, 19))))
    );
    private static final List<Pair<Integer, Integer>> LARGE_MOCK_FRAME_LIST = List.of(
            Pair.of(0, 1),
            Pair.of(2, 5),
            Pair.of(1, 27),
            Pair.of(2, 3)
    );
    private static final List<Pair<Integer, Integer>> SMALL_MOCK_FRAME_LIST = List.of(
            Pair.of(2, 5),
            Pair.of(0, 1)
    );

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void construct_NullLevelSupplier_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponentBuilder(null);
    }

    @Test
    public void build_NullMetadata_NullPointerException() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        expectedException.expect(NullPointerException.class);
        builder.build(null, MOCK_FRAME_GROUP.get());
    }

    @Test
    public void build_NullFrameGroup_NullPointerException() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        expectedException.expect(NullPointerException.class);
        builder.build(new AnimationGroupMetadata(10, 20, ImmutableList.of()), null);
    }

    @Test
    public void build_WrongClassMetadata_IllegalArgException() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        expectedException.expect(IllegalArgumentException.class);
        builder.build(new AnalyzedMetadata() {}, MOCK_FRAME_GROUP.get());
    }

    @Test
    public void build_NotSyncedNoPredefinedFrames_DefaultFrameTimeUsed() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        int time = 33;
        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, time, true, false, ImmutableList.of(),
                                        0, false, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames());

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, persistentFrameGroup, 1);
            }
            assertEquals(indexToColor(expectedFrame % MOCK_FRAME_GROUP.get().frames()), currentFrameView.color(0, 0));
        }
    }

    @Test
    public void build_NotSyncedHasMorePredefinedFramesThanActualFrames_PredefinedFrameTimeUsed() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);

        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, 33, true, false, LARGE_MOCK_FRAME_LIST,
                                        0, false, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames());

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            int time = LARGE_MOCK_FRAME_LIST.get((expectedFrame - 1) % LARGE_MOCK_FRAME_LIST.size()).getSecond();
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, persistentFrameGroup, 1);
            }
            assertEquals(
                    indexToColor(LARGE_MOCK_FRAME_LIST.get(expectedFrame % LARGE_MOCK_FRAME_LIST.size()).getFirst()),
                    currentFrameView.color(0, 0)
            );
        }
    }

    @Test
    public void build_NotSyncedHasFewerPredefinedFramesThanActualFrames_PredefinedFrameTimeUsed() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);

        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, 33, true, false, SMALL_MOCK_FRAME_LIST,
                                        0, false, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames());

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            int time = SMALL_MOCK_FRAME_LIST.get((expectedFrame - 1) % SMALL_MOCK_FRAME_LIST.size()).getSecond();
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, persistentFrameGroup, 1);
            }
            assertEquals(
                    indexToColor(SMALL_MOCK_FRAME_LIST.get(expectedFrame % SMALL_MOCK_FRAME_LIST.size()).getFirst()),
                    currentFrameView.color(0, 0)
            );
        }
    }

    @Test
    public void build_SyncedNoPredefinedFrames_DefaultFrameTimeUsed() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        int time = 33;
        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, time, true, false, ImmutableList.of(),
                                        0, true, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames()), 1);
            }
            assertEquals(indexToColor(expectedFrame % MOCK_FRAME_GROUP.get().frames()), currentFrameView.color(0, 0));
        }
    }

    @Test
    public void build_SyncedHasMorePredefinedFramesThanActualFrames_PredefinedFrameTimeUsed() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);

        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, 33, true, false, LARGE_MOCK_FRAME_LIST,
                                        0, true, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames());

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            int time = LARGE_MOCK_FRAME_LIST.get((expectedFrame - 1) % LARGE_MOCK_FRAME_LIST.size()).getSecond();
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, persistentFrameGroup, 1);
            }
            assertEquals(
                    indexToColor(LARGE_MOCK_FRAME_LIST.get(expectedFrame % LARGE_MOCK_FRAME_LIST.size()).getFirst()),
                    currentFrameView.color(0, 0)
            );
        }
    }

    @Test
    public void build_SyncedHasFewerPredefinedFramesThanActualFrames_PredefinedFrameTimeUsed() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);

        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, 33, true, false, SMALL_MOCK_FRAME_LIST,
                                        0, true, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames());

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            int time = SMALL_MOCK_FRAME_LIST.get((expectedFrame - 1) % SMALL_MOCK_FRAME_LIST.size()).getSecond();
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, persistentFrameGroup, 1);
            }
            assertEquals(
                    indexToColor(SMALL_MOCK_FRAME_LIST.get(expectedFrame % SMALL_MOCK_FRAME_LIST.size()).getFirst()),
                    currentFrameView.color(0, 0)
            );
        }
    }

    @Test
    public void build_DiffPartsChangedInEachFrame_InterpolateAreaCombined() {
        checkChangedPoints(
                MOCK_FRAME_GROUP.get(),
                Set.of(Point.pack(0, 1), Point.pack(1, 1), Point.pack(0, 0), Point.pack(9, 19))
        );
    }

    @Test
    public void build_DiffRgbButAlphaZero_InvisibleColorsIgnored() {
        checkChangedPoints(
                new MockMutableFrameGroup(
                        new MockMutableFrameView(Pair.of(Color.pack(10, 10, 10, 0), Area.of(Point.pack(0, 1), Point.pack(1, 1)))),
                        new MockMutableFrameView(Pair.of(Color.pack(20, 20, 20, 0), Area.of(Point.pack(0, 1), Point.pack(0, 0)))),
                        new MockMutableFrameView(Pair.of(Color.pack(10, 10, 10, 30), Area.of(Point.pack(0, 1), Point.pack(9, 19))))
                ),
                Set.of(Point.pack(0, 1), Point.pack(9, 19))
        );
    }

    @Test
    public void build_AllColorsTheSame_NoPointsFound() {
        checkChangedPoints(
                new MockMutableFrameGroup(
                        new MockMutableFrameView(Color.pack(10, 10, 10, 10)),
                        new MockMutableFrameView(Color.pack(10, 10, 10, 10)),
                        new MockMutableFrameView(Color.pack(10, 10, 10, 10))
                ),
                Set.of()
        );
    }

    @Test
    public void build_NoFrames_IllegalArgExceptionFromState() {
        expectedException.expect(IllegalArgumentException.class);
        checkChangedPoints(
                new MockMutableFrameGroup(),
                Set.of()
        );
    }

    @Test
    public void build_InterpolationDisabled_NoInterpolation() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        int time = 33;
        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, time, false, false, ImmutableList.of(),
                                        0, false, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        for (int tick = 1; tick < time; tick++) {
            component.onTick(currentFrameView, new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames()));
            assertEquals(
                    indexToColor(0),
                    currentFrameView.color(0, 1)
            );
        }
    }

    @Test
    public void build_AlphaSmoothDisabled_AlphaNotSmoothed() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        int time = 33;
        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, time, true, false, ImmutableList.of(),
                                        0, false, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames());
        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();

        for (int tick = 1; tick < time; tick++) {
            component.onTick(currentFrameView, persistentFrameGroup, 1);
            assertEquals(
                    interpolator.interpolate(time, tick, indexToColor(0), indexToColor(1)),
                    currentFrameView.color(0, 1)
            );
        }
    }

    @Test
    public void build_AlphaSmoothEnabled_AlphaSmoothed() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        int time = 33;
        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, time, true, true, ImmutableList.of(),
                                        0, false, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames());
        SmoothAlphaInterpolator interpolator = new SmoothAlphaInterpolator();

        for (int tick = 1; tick < time; tick++) {
            component.onTick(currentFrameView, persistentFrameGroup, 1);
            assertEquals(
                    interpolator.interpolate(time, tick, indexToColor(0), indexToColor(1)),
                    currentFrameView.color(0, 1)
            );
        }
    }

    @Test
    public void build_AnimationHasMultipleParts_PartsAppliedInSizeOrderBeforeTicks() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        MockMutableFrameGroup frameGroup = MOCK_FRAME_GROUP.get();

        int time = 33;
        builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 2, 1,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(14),
                                                (x, y) -> indexToColor(15)
                                        )),
                                        () -> {}
                                ),
                                new AnimationMetadata(
                                        3, 5, time, true, false, ImmutableList.of(),
                                        0, false, 3, 3,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(16),
                                                (x, y) -> indexToColor(17)
                                        )),
                                        () -> {}
                                ),
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 1, 2,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(12),
                                                (x, y) -> indexToColor(13)
                                        )),
                                        () -> {}
                                ),
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 1, 1,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(10),
                                                (x, y) -> indexToColor(11)
                                        )),
                                        () -> {}
                                )
                        )
                ),
                frameGroup
        );

        MockMutableFrameView firstFrame = ((MockMutableFrameView) frameGroup.frame(0));
        assertEquals(
                indexToColor(10),
                firstFrame.color(1, 1)
        );
        assertEquals(
                indexToColor(12),
                firstFrame.color(1, 2)
        );
        assertEquals(
                indexToColor(14),
                firstFrame.color(2, 1)
        );
        assertEquals(
                indexToColor(16),
                firstFrame.color(4, 4)
        );
    }

    @Test
    public void build_AnimationHasMultipleParts_PartsAppliedInSizeOrderAfterTicks() {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        int time = 33;
        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 2, 1,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(14),
                                                (x, y) -> indexToColor(15)
                                        )),
                                        () -> {}
                                ),
                                new AnimationMetadata(
                                        3, 5, time, true, false, ImmutableList.of(),
                                        0, false, 3, 3,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(16),
                                                (x, y) -> indexToColor(17)
                                        )),
                                        () -> {}
                                ),
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 1, 2,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(12),
                                                (x, y) -> indexToColor(13)
                                        )),
                                        () -> {}
                                ),
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 1, 1,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(10),
                                                (x, y) -> indexToColor(11)
                                        )),
                                        () -> {}
                                )
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames());
        DefaultAlphaInterpolator interpolator = new DefaultAlphaInterpolator();

        for (int tick = 1; tick < time; tick++) {
            component.onTick(currentFrameView, persistentFrameGroup, 1);
            assertEquals(
                    interpolator.interpolate(time, tick, indexToColor(10), indexToColor(11)),
                    currentFrameView.color(1, 1)
            );
            assertEquals(
                    interpolator.interpolate(time, tick, indexToColor(12), indexToColor(13)),
                    currentFrameView.color(1, 2)
            );
            assertEquals(
                    interpolator.interpolate(time, tick, indexToColor(14), indexToColor(15)),
                    currentFrameView.color(2, 1)
            );
            assertEquals(
                    interpolator.interpolate(time, tick, indexToColor(16), indexToColor(17)),
                    currentFrameView.color(4, 4)
            );
        }
    }

    @Test
    public void build_AnimationHasMultipleParts_AllPartsClosed() {
        AtomicBoolean closer1 = new AtomicBoolean();
        AtomicBoolean closer2 = new AtomicBoolean();
        AtomicBoolean closer3 = new AtomicBoolean();
        AtomicBoolean closer4 = new AtomicBoolean();

        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        int time = 33;
        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 2, 1,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(14),
                                                (x, y) -> indexToColor(15)
                                        )),
                                        () -> closer3.set(true)
                                ),
                                new AnimationMetadata(
                                        3, 5, time, true, false, ImmutableList.of(),
                                        0, false, 3, 3,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(16),
                                                (x, y) -> indexToColor(17)
                                        )),
                                        () -> closer4.set(true)
                                ),
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 1, 2,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(12),
                                                (x, y) -> indexToColor(13)
                                        )),
                                        () -> closer2.set(true)
                                ),
                                new AnimationMetadata(
                                        5, 5, time, true, false, ImmutableList.of(),
                                        0, false, 1, 1,
                                        Optional.of(ImmutableList.of(
                                                (x, y) -> indexToColor(10),
                                                (x, y) -> indexToColor(11)
                                        )),
                                        () -> closer1.set(true)
                                )
                        )
                ),
                MOCK_FRAME_GROUP.get()
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        component.onClose(currentFrameView, new MockPersistentFrameGroup(MOCK_FRAME_GROUP.get().frames()));

        assertTrue(closer1.get());
        assertTrue(closer2.get());
        assertTrue(closer3.get());
        assertTrue(closer4.get());
    }

    private static void checkChangedPoints(FrameGroup<MutableFrameView> frameGroup, Set<Long> expectedPoints) {
        AnimationComponentBuilder builder = new AnimationComponentBuilder(Optional::empty);
        int time = 33;
        TextureComponent<? super CurrentFrameView> component = builder.build(
                new AnimationGroupMetadata(
                        10, 20,
                        ImmutableList.of(
                                new AnimationMetadata(10, 20, time, true, false, ImmutableList.of(),
                                        0, false, 0, 0, Optional.empty(), () -> {})
                        )
                ),
                frameGroup
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();
        MockPersistentFrameGroup persistentFrameGroup = new MockPersistentFrameGroup(frameGroup.frames());

        Set<Long> changedPoints = new HashSet<>();
        Set<Integer> nonInterpolatedColors = Set.of(
                Color.pack(0, 0, 0, 0),
                Color.pack(10, 10, 10, 10),
                Color.pack(20, 20, 20, 20),
                Color.pack(30, 30, 30, 30)
        );
        for (int frame = 0; frame < frameGroup.frames(); frame++) {
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, persistentFrameGroup, 1);

                for (int y = 0; y < currentFrameView.height(); y++) {
                    for (int x = 0; x < currentFrameView.width(); x++) {
                        if (!nonInterpolatedColors.contains(currentFrameView.color(x, y))) {
                            changedPoints.add(Point.pack(x, y));
                        }
                    }
                }
            }
        }

        assertEquals(expectedPoints, changedPoints);
    }
}
