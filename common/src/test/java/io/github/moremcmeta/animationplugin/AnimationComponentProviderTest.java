package io.github.moremcmeta.animationplugin;

import com.mojang.datafixers.util.Pair;
import io.github.moremcmeta.moremcmeta.api.client.metadata.ParsedMetadata;
import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.FrameGroup;
import io.github.moremcmeta.moremcmeta.api.client.texture.MutableFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.TextureComponent;
import io.github.moremcmeta.moremcmeta.api.client.texture.UploadableFrameView;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;

import static io.github.moremcmeta.animationplugin.AnimationComponentTest.indexToColor;

/**
 * Tests the {@link AnimationComponentProvider}.
 * @author soir20
 */
public class AnimationComponentProviderTest {
    private static final MockMutableFrameGroup MOCK_FRAME_GROUP = new MockMutableFrameGroup(
            new MockMutableFrameView(0, Pair.of(Color.pack(10, 10, 10, 10), Area.of(Point.pack(0, 1), Point.pack(1, 1)))),
            new MockMutableFrameView(1, Pair.of(Color.pack(20, 20, 20, 20), Area.of(Point.pack(0, 1), Point.pack(0, 0)))),
            new MockMutableFrameView(2, Pair.of(Color.pack(30, 30, 30, 30), Area.of(Point.pack(0, 1), Point.pack(9, 19))))
    );
    private static final List<IntIntPair> MOCK_FRAME_LIST = List.of(
            IntIntPair.of(0, 1),
            IntIntPair.of(5, 5),
            IntIntPair.of(2, 27)
    );

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void construct_NullLevelSupplier_NullPointerException() {
        expectedException.expect(NullPointerException.class);
        new AnimationComponentProvider(null);
    }

    @Test
    public void assemble_NullMetadata_NullPointerException() {
        AnimationComponentProvider provider = new AnimationComponentProvider(Optional::empty);
        expectedException.expect(NullPointerException.class);
        provider.assemble(null, MOCK_FRAME_GROUP);
    }

    @Test
    public void assemble_NullFrameGroup_NullPointerException() {
        AnimationComponentProvider provider = new AnimationComponentProvider(Optional::empty);
        expectedException.expect(NullPointerException.class);
        provider.assemble(new AnimationMetadata(10, 20, 10, true, MOCK_FRAME_LIST, true), null);
    }

    @Test
    public void assemble_WrongClassMetadata_IllegalArgException() {
        AnimationComponentProvider provider = new AnimationComponentProvider(Optional::empty);
        expectedException.expect(IllegalArgumentException.class);
        provider.assemble(new ParsedMetadata() {}, MOCK_FRAME_GROUP);
    }

    @Test
    public void assemble_NotSyncedNoPredefinedFrames_DefaultFrameTimeUsed() {
        AnimationComponentProvider provider = new AnimationComponentProvider(Optional::empty);
        int time = 33;
        TextureComponent<CurrentFrameView, UploadableFrameView> component = provider.assemble(
                new AnimationMetadata(10, 20, time, true, List.of(), false),
                MOCK_FRAME_GROUP
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, new MockPersistentFrameGroup(MOCK_FRAME_GROUP.frames()));
            }
            assertEquals(indexToColor(expectedFrame % MOCK_FRAME_GROUP.frames()), currentFrameView.color(0, 0));
        }
    }

    @Test
    public void assemble_NotSyncedHasPredefinedFrames_PredefinedFrameTimeUsed() {
        AnimationComponentProvider provider = new AnimationComponentProvider(Optional::empty);

        TextureComponent<CurrentFrameView, UploadableFrameView> component = provider.assemble(
                new AnimationMetadata(10, 20, 33, true, MOCK_FRAME_LIST, false),
                MOCK_FRAME_GROUP
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            int time = MOCK_FRAME_LIST.get((expectedFrame - 1) % MOCK_FRAME_GROUP.frames()).rightInt();
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, new MockPersistentFrameGroup(MOCK_FRAME_GROUP.frames()));
            }
            assertEquals(indexToColor(expectedFrame % MOCK_FRAME_GROUP.frames()), currentFrameView.color(0, 0));
        }
    }

    @Test
    public void assemble_SyncedNoPredefinedFrames_DefaultFrameTimeUsed() {
        AnimationComponentProvider provider = new AnimationComponentProvider(Optional::empty);
        int time = 33;
        TextureComponent<CurrentFrameView, UploadableFrameView> component = provider.assemble(
                new AnimationMetadata(10, 20, time, true, List.of(), true),
                MOCK_FRAME_GROUP
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, new MockPersistentFrameGroup(MOCK_FRAME_GROUP.frames()));
            }
            assertEquals(indexToColor(expectedFrame % MOCK_FRAME_GROUP.frames()), currentFrameView.color(0, 0));
        }
    }

    @Test
    public void assemble_SyncedHasPredefinedFrames_PredefinedFrameTimeUsed() {
        AnimationComponentProvider provider = new AnimationComponentProvider(Optional::empty);

        TextureComponent<CurrentFrameView, UploadableFrameView> component = provider.assemble(
                new AnimationMetadata(10, 20, 33, true, MOCK_FRAME_LIST, true),
                MOCK_FRAME_GROUP
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        for (int expectedFrame = 1; expectedFrame < 6; expectedFrame++) {
            int time = MOCK_FRAME_LIST.get((expectedFrame - 1) % MOCK_FRAME_GROUP.frames()).rightInt();
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, new MockPersistentFrameGroup(MOCK_FRAME_GROUP.frames()));
            }
            assertEquals(indexToColor(expectedFrame % MOCK_FRAME_GROUP.frames()), currentFrameView.color(0, 0));
        }
    }

    @Test
    public void assemble_DiffPartsChangedInEachFrame_InterpolateAreaCombined() {
        checkChangedPoints(
                MOCK_FRAME_GROUP,
                Set.of(Point.pack(0, 1), Point.pack(1, 1), Point.pack(0, 0), Point.pack(9, 19))
        );
    }

    @Test
    public void assemble_DiffRgbButAlphaZero_InvisibleColorsIgnored() {
        checkChangedPoints(
                new MockMutableFrameGroup(
                        new MockMutableFrameView(0, Pair.of(Color.pack(10, 10, 10, 0), Area.of(Point.pack(0, 1), Point.pack(1, 1)))),
                        new MockMutableFrameView(1, Pair.of(Color.pack(20, 20, 20, 0), Area.of(Point.pack(0, 1), Point.pack(0, 0)))),
                        new MockMutableFrameView(2, Pair.of(Color.pack(10, 10, 10, 30), Area.of(Point.pack(0, 1), Point.pack(9, 19))))
                ),
                Set.of(Point.pack(0, 1), Point.pack(9, 19))
        );
    }

    @Test
    public void assemble_AllColorsTheSame_NoPointsFound() {
        checkChangedPoints(
                new MockMutableFrameGroup(
                        new MockMutableFrameView(0, Color.pack(10, 10, 10, 10)),
                        new MockMutableFrameView(1, Color.pack(10, 10, 10, 10)),
                        new MockMutableFrameView(2, Color.pack(10, 10, 10, 10))
                ),
                Set.of()
        );
    }

    @Test
    public void assemble_NoFrames_IllegalArgExceptionFromState() {
        expectedException.expect(IllegalArgumentException.class);
        checkChangedPoints(
                new MockMutableFrameGroup(),
                Set.of()
        );
    }

    private static void checkChangedPoints(FrameGroup<MutableFrameView> frameGroup, Set<Long> expectedPoints) {
        AnimationComponentProvider provider = new AnimationComponentProvider(Optional::empty);
        int time = 33;
        TextureComponent<CurrentFrameView, UploadableFrameView> component = provider.assemble(
                new AnimationMetadata(10, 20, time, true, List.of(), false),
                frameGroup
        );

        MockCurrentFrameView currentFrameView = new MockCurrentFrameView();

        Set<Long> changedPoints = new HashSet<>();
        Set<Integer> nonInterpolatedColors = Set.of(
                Color.pack(0, 0, 0, 0),
                Color.pack(10, 10, 10, 10),
                Color.pack(20, 20, 20, 20),
                Color.pack(30, 30, 30, 30)
        );
        for (int frame = 0; frame < frameGroup.frames(); frame++) {
            for (int tick = 0; tick < time; tick++) {
                component.onTick(currentFrameView, new MockPersistentFrameGroup(frameGroup.frames()));

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
