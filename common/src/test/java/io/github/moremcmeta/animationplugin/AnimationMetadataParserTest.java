package io.github.moremcmeta.animationplugin;

import com.google.common.collect.ImmutableMap;
import io.github.moremcmeta.moremcmeta.api.client.metadata.InvalidMetadataException;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link AnimationMetadataParser}.
 * @author soir20
 */
public class AnimationMetadataParserTest {
    private static final AnimationMetadataParser PARSER = new AnimationMetadataParser();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void parse_NullMetadata_NullPointerException() throws InvalidMetadataException {
        expectedException.expect(NullPointerException.class);
        PARSER.parse(null, 10, 20);
    }

    @Test
    public void parse_OnlyFrameWidthProvided_UsesImageHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("width", 7)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(7, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_OnlyFrameHeightProvided_UsesImageHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("height", 7)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(7, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_FrameHeightOtherType_UsesImageHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("width", 7, "height", "13")))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(7, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_FrameWidthOtherType_UsesImageHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("height", 7, "width", "13")))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(7, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_FrameWidthNegative_UsesProvidedWidth() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("width", -13, "height", 7)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(-13, (int) metadata.frameWidth().orElseThrow());
        assertEquals(7, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_FrameHeightNegative_UsesProvidedHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("width", 13, "height", -7)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(13, (int) metadata.frameWidth().orElseThrow());
        assertEquals(-7, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_FrameWidthLargerThanImage_UsesProvidedWidth() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("width", 21, "height", 7)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(21, (int) metadata.frameWidth().orElseThrow());
        assertEquals(7, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_FrameHeightLargerThanImage_UsesProvidedHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("width", 13, "height", 11)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(13, (int) metadata.frameWidth().orElseThrow());
        assertEquals(11, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_FrameWidthAndHeightProvided_UsesProvidedValues() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("width", 7, "height", 13)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(7, (int) metadata.frameWidth().orElseThrow());
        assertEquals(13, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_NeitherFrameWidthNorHeightProvidedWidthSmaller_UsesMinSquare() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of()))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(10, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_NeitherFrameWidthNorHeightProvidedHeightSmaller_UsesMinSquare() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of()))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 20, 10);

        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(10, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void parse_NoDefaultTime_Uses1Tick() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of()))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(1, metadata.defaultTime());
    }

    @Test
    public void parse_ZeroDefaultTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("frametime", 0)))
        );
        expectedException.expect(InvalidMetadataException.class);
        PARSER.parse(metadataView, 10, 20);
    }

    @Test
    public void parse_NegativeDefaultTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("frametime", -1)))
        );
        expectedException.expect(InvalidMetadataException.class);
        PARSER.parse(metadataView, 10, 20);
    }

    @Test
    public void parse_PositiveDefaultTime_UsesProvidedTime() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("frametime", 7)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(7, metadata.defaultTime());
    }

    @Test
    public void parse_DefaultTimeOtherType_Uses1Tick() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("frametime", "7")))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(1, metadata.defaultTime());
    }

    @Test
    public void parse_NoInterpolate_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of()))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertFalse(metadata.interpolate());
    }

    @Test
    public void parse_TrueInterpolate_UsesProvidedInterpolate() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("interpolate", true)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertTrue(metadata.interpolate());
    }

    @Test
    public void parse_FalseInterpolate_UsesProvidedInterpolate() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("interpolate", false)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertFalse(metadata.interpolate());
    }

    @Test
    public void parse_InterpolateOtherType_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("interpolate", "7")))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertFalse(metadata.interpolate());
    }

    @Test
    public void parse_NoDaytimeSync_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of()))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertFalse(metadata.daytimeSync());
    }

    @Test
    public void parse_TrueDaytimeSync_UsesProvidedInterpolate() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("daytimeSync", true)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertTrue(metadata.daytimeSync());
    }

    @Test
    public void parse_FalseDaytimeSync_UsesProvidedInterpolate() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("daytimeSync", false)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertFalse(metadata.daytimeSync());
    }

    @Test
    public void parse_DaytimeSyncOtherType_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("daytimeSync", "7")))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertFalse(metadata.daytimeSync());
    }

    @Test
    public void parse_NoPredefinedFrames_UsesEmpty() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of()))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertTrue(metadata.predefinedFrames().isEmpty());
    }

    @Test
    public void parse_PredefinedFramesOnlyIndices_UsesDefaultTimeForAllFrames() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", 5,
                                        "1", 2,
                                        "2", 0
                                ))
                        ))
                )
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(
                List.of(IntIntPair.of(5, 1), IntIntPair.of(2, 1), IntIntPair.of(0, 1)),
                metadata.predefinedFrames()
        );
    }

    @Test
    public void parse_PredefinedFramesOnlyObjects_UsesProvidedTimeForAllFrames() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", new MockMetadataView(ImmutableMap.of("index", 2, "time", 4)),
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 4), IntIntPair.of(0, 7)),
                metadata.predefinedFrames()
        );
    }

    @Test
    public void parse_PredefinedFramesObjectMissingIndex_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", new MockMetadataView(ImmutableMap.of( "time", 4)),
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        PARSER.parse(metadataView, 10, 20);
    }

    @Test
    public void parse_PredefinedFramesObjectNegativeIndex_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", new MockMetadataView(ImmutableMap.of( "index", -2, "time", 4)),
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        PARSER.parse(metadataView, 10, 20);
    }

    @Test
    public void parse_PredefinedFramesObjectNegativeTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", new MockMetadataView(ImmutableMap.of( "index", 2, "time", -4)),
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        PARSER.parse(metadataView, 10, 20);
    }

    @Test
    public void parse_PredefinedFramesObjectZeroTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", new MockMetadataView(ImmutableMap.of( "index", 2, "time", 0)),
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        PARSER.parse(metadataView, 10, 20);
    }

    @Test
    public void parse_PredefinedFramesObjectMissingTimeNoDefault_Uses1Tick() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", new MockMetadataView(ImmutableMap.of("index", 2)),
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 1), IntIntPair.of(0, 7)),
                metadata.predefinedFrames()
        );
    }

    @Test
    public void parse_PredefinedFramesObjectMissingTimeHasDefault_UsesDefault() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frametime", 13,
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", new MockMetadataView(ImmutableMap.of("index", 2)),
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 13), IntIntPair.of(0, 7)),
                metadata.predefinedFrames()
        );
    }

    @Test
    public void parse_PredefinedFramesMixedIndicesObjects_HandlesBoth() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frametime", 13,
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", 2,
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 13), IntIntPair.of(0, 7)),
                metadata.predefinedFrames()
        );
    }

    @Test
    public void parse_PredefinedFramesOtherTypeMixedIn_HandlesIgnoresOtherType() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frametime", 13,
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", 2,
                                        "2", "7",
                                        "3", new MockMetadataView(ImmutableMap.of("index", 8, "time", 2))
                                ))
                        ))
                )
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 13), IntIntPair.of(8, 2)),
                metadata.predefinedFrames()
        );
    }

    @Test
    public void parse_PredefinedFramesOtherType_UsesEmpty() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frametime", 13,
                                "frames", "7"
                        ))
                )
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertTrue(metadata.predefinedFrames().isEmpty());
    }

    @Test
    public void parse_ZeroSkipTicks_UsesProvidedTicks() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("skip", 0)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(0, metadata.skipTicks());
    }

    @Test
    public void parse_NegativeSkipTicks_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("skip", -1)))
        );
        expectedException.expect(InvalidMetadataException.class);
        PARSER.parse(metadataView, 10, 20);
    }

    @Test
    public void parse_PositiveSkipTicks_UsesProvidedTicks() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("animation", new MockMetadataView(ImmutableMap.of("skip", 7)))
        );
        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);

        assertEquals(7, metadata.skipTicks());
    }

    @Test
    public void parse_AllFieldsPresent_AllUsed() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "width", 18,
                                "height", 27,
                                "frametime", 5,
                                "interpolate", true,
                                "daytimeSync", false,
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", 2,
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                )),
                                "skip", 12
                        ))
                )
        );

        AnimationMetadata metadata = (AnimationMetadata) PARSER.parse(metadataView, 10, 20);
        assertEquals(18, (int) metadata.frameWidth().orElseThrow());
        assertEquals(27, (int) metadata.frameHeight().orElseThrow());
        assertEquals(5, metadata.defaultTime());
        assertEquals(12, metadata.skipTicks());
        assertTrue(metadata.interpolate());
        assertFalse(metadata.daytimeSync());
        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 5), IntIntPair.of(0, 7)),
                metadata.predefinedFrames()
        );
    }

    @Test
    public void parse_NoAnimationSection_NoSuchElementException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animations",
                        new MockMetadataView(ImmutableMap.of(
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                        "1", new MockMetadataView(ImmutableMap.of( "index", 2, "time", 0)),
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                                ))
                        ))
                )
        );

        expectedException.expect(NoSuchElementException.class);
        PARSER.parse(metadataView, 10, 20);
    }

}
