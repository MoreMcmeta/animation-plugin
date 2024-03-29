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

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.moremcmeta.animationplugin.MockMetadataView;
import io.github.moremcmeta.moremcmeta.api.client.metadata.InvalidMetadataException;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link AnimationMetadataAnalyzer}.
 * @author soir20
 */
public final class AnimationMetadataAnalyzerTest {
    private static final AnimationMetadataAnalyzer ANALYZER = new AnimationMetadataAnalyzer();
    @SuppressWarnings("resource")
    private static final BiFunction<Integer, Integer, InputStream> MOCK_TEXTURE = (w, h) -> {
        try {
            return new ByteArrayInputStream(
                    new NativeImage(w, h, false).asByteArray()
            );
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    };

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void analyze_NoPartsNullMetadata_NullPointerException() throws InvalidMetadataException {
        expectedException.expect(NullPointerException.class);
        ANALYZER.analyze(null, 10, 20);
    }

    @Test
    public void analyze_NoPartsOnlyFrameWidthProvided_UsesImageHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("width", 7)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(7, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void analyze_NoPartsOnlyFrameHeightProvided_UsesImageHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("height", 7)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(7, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void analyze_NoPartsFrameHeightOtherType_UsesImageHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("width", 7, "height", "13")
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(7, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void analyze_NoPartsFrameWidthOtherType_UsesImageHeight() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("height", 7, "width", "13")
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(7, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void analyze_NoPartsFrameWidthNegative_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("width", -13, "height", 7)
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsFrameHeightNegative_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("width", 13, "height", -7)
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsFrameWidthLargerThanImage_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("width", 21, "height", 7)
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsFrameHeightLargerThanImage_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("width", 7, "height", 21)
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsFrameWidthAndHeightProvided_UsesProvidedValues() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("width", 7, "height", 13)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(7, (int) metadata.frameWidth().orElseThrow());
        assertEquals(13, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void analyze_NoPartsNeitherFrameWidthNorHeightProvidedWidthSmaller_UsesMinSquare() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of()
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(10, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void analyze_NoPartsNeitherFrameWidthNorHeightProvidedHeightSmaller_UsesMinSquare() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of()
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 20, 10);

        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(10, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void analyze_NoPartsNoDefaultTime_Uses1Tick() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of()
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(1, metadata.parts().get(0).defaultTime());
    }

    @Test
    public void analyze_NoPartsZeroDefaultTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
               ImmutableMap.of("frametime", 0d)
        );
        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsLessThanOneDefaultTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("frametime", 0.6d)
        );
        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsNegativeDefaultTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("frametime", -1d)
        );
        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsPositiveDefaultTime_UsesProvidedTime() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("frametime", 7d)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(7, metadata.parts().get(0).defaultTime());
    }

    @Test
    public void analyze_NoPartsPositiveDefaultTime_RoundsTimeDown() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("frametime", 7.7d)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(7, metadata.parts().get(0).defaultTime());
    }

    @Test
    public void analyze_NoPartsDefaultTimeOtherType_Uses1Tick() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("frametime", "7")
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(1, metadata.parts().get(0).defaultTime());
    }

    @Test
    public void analyze_NoPartsNoInterpolate_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of()
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).interpolate());
    }

    @Test
    public void analyze_NoPartsTrueInterpolate_UsesProvidedInterpolate() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("interpolate", true)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertTrue(metadata.parts().get(0).interpolate());
    }

    @Test
    public void analyze_NoPartsFalseInterpolate_UsesProvidedInterpolate() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("interpolate", false)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).interpolate());
    }

    @Test
    public void analyze_NoPartsInterpolateOtherType_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("interpolate", "7")
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).interpolate());
    }

    @Test
    public void analyze_NoPartsNoSmoothAlpha_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of()
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).smoothAlpha());
    }

    @Test
    public void analyze_NoPartsTrueSmoothAlpha_UsesProvidedSmoothAlpha() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("smoothAlpha", true)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertTrue(metadata.parts().get(0).smoothAlpha());
    }

    @Test
    public void analyze_NoPartsFalseSmoothAlpha_UsesProvidedSmoothAlpha() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("smoothAlpha", false)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).smoothAlpha());
    }

    @Test
    public void analyze_NoPartsSmoothAlphaOtherType_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("smoothAlpha", "7")
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).smoothAlpha());
    }

    @Test
    public void analyze_NoPartsNoDaytimeSync_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of()
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).daytimeSync());
    }

    @Test
    public void analyze_NoPartsTrueDaytimeSync_UsesProvidedInterpolate() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("daytimeSync", true)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertTrue(metadata.parts().get(0).daytimeSync());
    }

    @Test
    public void analyze_NoPartsFalseDaytimeSync_UsesProvidedInterpolate() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("daytimeSync", false)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).daytimeSync());
    }

    @Test
    public void analyze_NoPartsDaytimeSyncOtherType_UsesFalse() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("daytimeSync", "7")
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertFalse(metadata.parts().get(0).daytimeSync());
    }

    @Test
    public void analyze_NoPartsNoPredefinedFrames_UsesEmpty() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of()
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertTrue(metadata.parts().get(0).predefinedFrames().isEmpty());
    }

    @Test
    public void analyze_NoPartsPredefinedFramesOnlyIndices_UsesDefaultTimeForAllFrames() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", 5,
                                "1", 2,
                                "2", 0
                        ))
                )
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 100);

        assertEquals(
                List.of(IntIntPair.of(5, 1), IntIntPair.of(2, 1), IntIntPair.of(0, 1)),
                metadata.parts().get(0).predefinedFrames()
        );
    }

    @Test
    public void analyze_NoPartsPredefinedFramesOnlyIndicesTooLargeFrameIndex_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "width", 5,
                        "height", 5,
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", 8,
                                "1", 2,
                                "2", 0
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsPredefinedFramesOnlyObjects_UsesProvidedTimeForAllFrames() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12d)),
                                "1", new MockMetadataView(ImmutableMap.of("index", 2, "time", 4d)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7d))
                        ))
                )
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 100);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 4), IntIntPair.of(0, 7)),
                metadata.parts().get(0).predefinedFrames()
        );
    }

    @Test
    public void analyze_NoPartsPredefinedFramesOnlyObjects_RoundsTimeForAllFrames() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12.1d)),
                                "1", new MockMetadataView(ImmutableMap.of("index", 2, "time", 4.8d)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7d))
                        ))
                )
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 100);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 4), IntIntPair.of(0, 7)),
                metadata.parts().get(0).predefinedFrames()
        );
    }

    @Test
    public void analyze_NoPartsPredefinedFramesOnlyObjectsTooLargeFrameIndex_InvalidMetadataException()
            throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "width", 5,
                        "height", 5,
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 8, "time", 12)),
                                "1", new MockMetadataView(ImmutableMap.of("index", 2, "time", 4)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsPredefinedFramesObjectMissingIndex_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                "1", new MockMetadataView(ImmutableMap.of( "time", 4)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsPredefinedFramesObjectNegativeIndex_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                "1", new MockMetadataView(ImmutableMap.of( "index", -2, "time", 4)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsPredefinedFramesObjectNegativeTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                "1", new MockMetadataView(ImmutableMap.of( "index", 2, "time", -4)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsPredefinedFramesObjectZeroTime_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                "1", new MockMetadataView(ImmutableMap.of( "index", 2, "time", 0)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsPredefinedFramesObjectMissingTimeNoDefault_Uses1Tick() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12d)),
                                "1", new MockMetadataView(ImmutableMap.of("index", 2)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7d))
                        ))
                )
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 100);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 1), IntIntPair.of(0, 7)),
                metadata.parts().get(0).predefinedFrames()
        );
    }

    @Test
    public void analyze_NoPartsPredefinedFramesObjectMissingTimeHasDefault_UsesDefault() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frametime", 13d,
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12d)),
                                "1", new MockMetadataView(ImmutableMap.of("index", 2)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7d))
                        ))
                )
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 100);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 13), IntIntPair.of(0, 7)),
                metadata.parts().get(0).predefinedFrames()
        );
    }

    @Test
    public void analyze_NoPartsPredefinedFramesMixedIndicesObjects_HandlesBoth() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frametime", 13d,
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12d)),
                                "1", 2,
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7d))
                        ))
                )
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 100);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 13), IntIntPair.of(0, 7)),
                metadata.parts().get(0).predefinedFrames()
        );
    }

    @Test
    public void analyze_NoPartsPredefinedFramesOtherTypeMixedIn_HandlesIgnoresOtherType() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frametime", 13d,
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12d)),
                                "1", 2,
                                "2", "7",
                                "3", new MockMetadataView(ImmutableMap.of("index", 8, "time", 2d))
                        ))
                )
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 100);

        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 13), IntIntPair.of(8, 2)),
                metadata.parts().get(0).predefinedFrames()
        );
    }

    @Test
    public void analyze_NoPartsPredefinedFramesOtherType_UsesEmpty() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "animation",
                        new MockMetadataView(ImmutableMap.of(
                                "frametime", 13,
                                "frames", "7"
                        ))
                )
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertTrue(metadata.parts().get(0).predefinedFrames().isEmpty());
    }

    @Test
    public void analyze_NoPartsZeroSkipTicks_UsesProvidedTicks() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("skip", 0)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(0, metadata.parts().get(0).skipTicks());
    }

    @Test
    public void analyze_NoPartsNegativeSkipTicks_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("skip", -1)
        );
        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_NoPartsPositiveSkipTicks_UsesProvidedTicks() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("skip", 7)
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertEquals(7, metadata.parts().get(0).skipTicks());
    }

    @Test
    public void analyze_NoPartsAllFieldsPresent_AllUsed() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "width", 18,
                        "height", 27,
                        "frametime", 5d,
                        "interpolate", true,
                        "smoothAlpha", true,
                        "daytimeSync", false,
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12d)),
                                "1", 2,
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7d))
                        )),
                        "skip", 12
                )
        );

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 100, 100);
        assertEquals(18, (int) metadata.frameWidth().orElseThrow());
        assertEquals(27, (int) metadata.frameHeight().orElseThrow());
        assertEquals(5, metadata.parts().get(0).defaultTime());
        assertEquals(12, metadata.parts().get(0).skipTicks());
        assertTrue(metadata.parts().get(0).interpolate());
        assertTrue(metadata.parts().get(0).smoothAlpha());
        assertFalse(metadata.parts().get(0).daytimeSync());
        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 5), IntIntPair.of(0, 7)),
                metadata.parts().get(0).predefinedFrames()
        );
    }

    @Test
    public void analyze_NoPartsNoAnimationSection_NoSuchElementException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of(
                        "frames",
                        new MockMetadataView(ImmutableMap.of(
                                "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12)),
                                "1", new MockMetadataView(ImmutableMap.of( "index", 2, "time", 0)),
                                "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7))
                        ))
                )
        );

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_EmptyParts_UsesImageSizeAsFrame() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(
                ImmutableMap.of("parts", new MockMetadataView(
                        ImmutableMap.of()
                ))
        );
        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);

        assertTrue(metadata.parts().isEmpty());
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
    }

    @Test
    public void analyze_HasPartsPartWidthLargerThanBase_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 11,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(11, 10),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsPartHeightLargerThanBase_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 21,
                                "texture", MOCK_TEXTURE.apply(5, 21),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsNegativeXInBase_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", -1,
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsNegativeYInBase_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", 0,
                                "y", -1
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsTooLargeXInBase_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", 6,
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsTooLargeYInBase_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", 0,
                                "y", 11
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsPartAtTopLeft_NoException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
        assertEquals(0, metadata.parts().get(0).xInBase());
        assertEquals(0, metadata.parts().get(0).yInBase());
    }

    @Test
    public void analyze_HasPartsPartAtTopRight_NoException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", 5,
                                "y", 0
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
        assertEquals(5, metadata.parts().get(0).xInBase());
        assertEquals(0, metadata.parts().get(0).yInBase());
    }

    @Test
    public void analyze_HasPartsPartAtBottomLeft_NoException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", 0,
                                "y", 10
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
        assertEquals(0, metadata.parts().get(0).xInBase());
        assertEquals(10, metadata.parts().get(0).yInBase());
    }

    @Test
    public void analyze_HasPartsPartAtBottomRight_NoException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", 5,
                                "y", 10
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());
        assertEquals(5, metadata.parts().get(0).xInBase());
        assertEquals(10, metadata.parts().get(0).yInBase());
    }

    @Test
    public void analyze_HasPartsMissingX_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsMissingY_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(5, 10),
                                "x", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsMissingTextureData_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsBadTextureData_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", new ByteArrayInputStream("bad texture".getBytes()),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsFrameWidthLargerThanPart_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 11,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(10, 20),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsFrameHeightLargerThanPart_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 21,
                                "texture", MOCK_TEXTURE.apply(10, 20),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadataView, 10, 20);
    }

    @Test
    public void analyze_HasPartsFrameSizeMultipleOfPartSize_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(10, 20),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());

        assertEquals(5, metadata.parts().get(0).frameWidth());
        assertEquals(10, metadata.parts().get(0).frameHeight());
        assertEquals(0, metadata.parts().get(0).xInBase());
        assertEquals(0, metadata.parts().get(0).yInBase());
    }

    @Test
    public void analyze_HasPartsFrameWidthNotMultipleOfPartSize_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 4,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(10, 20),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());

        assertEquals(4, metadata.parts().get(0).frameWidth());
        assertEquals(10, metadata.parts().get(0).frameHeight());
        assertEquals(0, metadata.parts().get(0).xInBase());
        assertEquals(0, metadata.parts().get(0).yInBase());
    }

    @Test
    public void analyze_HasPartsFrameHeightNotMultipleOfPartSize_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 9,
                                "texture", MOCK_TEXTURE.apply(10, 20),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());

        assertEquals(5, metadata.parts().get(0).frameWidth());
        assertEquals(9, metadata.parts().get(0).frameHeight());
        assertEquals(0, metadata.parts().get(0).xInBase());
        assertEquals(0, metadata.parts().get(0).yInBase());
    }

    @Test
    public void analyze_HasPartsAndRegularAnimation_RegularAnimationIgnored() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
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
                "skip", 12,
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(10, 20),
                                "x", 0,
                                "y", 0
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 10, 20);
        assertEquals(10, (int) metadata.frameWidth().orElseThrow());
        assertEquals(20, (int) metadata.frameHeight().orElseThrow());

        assertEquals(5, metadata.parts().get(0).frameWidth());
        assertEquals(10, metadata.parts().get(0).frameHeight());
        assertEquals(0, metadata.parts().get(0).xInBase());
        assertEquals(0, metadata.parts().get(0).yInBase());
    }

    @Test
    public void analyze_HasPartsAllFieldsPresent_AllUsed() throws InvalidMetadataException {
        MetadataView metadataView = new MockMetadataView(ImmutableMap.of(
                "parts", new MockMetadataView(ImmutableMap.of(
                        "0", new MockMetadataView(ImmutableMap.of(
                                "width", 5,
                                "height", 10,
                                "texture", MOCK_TEXTURE.apply(10, 20),
                                "x", 0,
                                "y", 0
                        )),
                        "1", new MockMetadataView(ImmutableMap.of(
                                "texture", MOCK_TEXTURE.apply(108, 27),
                                "x", 1,
                                "y", 2,
                                "width", 18,
                                "height", 27,
                                "frametime", 5d,
                                "interpolate", true,
                                "smoothAlpha", true,
                                "frames",
                                new MockMetadataView(ImmutableMap.of(
                                        "0", new MockMetadataView(ImmutableMap.of("index", 5, "time", 12d)),
                                        "1", 2,
                                        "2", new MockMetadataView(ImmutableMap.of("index", 0, "time", 7d))
                                )),
                                "skip", 12
                        )),
                        "2", new MockMetadataView(ImmutableMap.of(
                                "texture", MOCK_TEXTURE.apply(10, 60),
                                "x", 4,
                                "y", 4,
                                "frametime", 15d,
                                "interpolate", true,
                                "daytimeSync", true
                        ))
                ))
        ));

        AnimationGroupMetadata metadata = (AnimationGroupMetadata) ANALYZER.analyze(metadataView, 100, 200);
        assertEquals(100, (int) metadata.frameWidth().orElseThrow());
        assertEquals(200, (int) metadata.frameHeight().orElseThrow());

        assertEquals(5, metadata.parts().get(0).frameWidth());
        assertEquals(10, metadata.parts().get(0).frameHeight());
        assertEquals(0, metadata.parts().get(0).xInBase());
        assertEquals(0, metadata.parts().get(0).yInBase());
        assertEquals(1, metadata.parts().get(0).defaultTime());
        assertEquals(0, metadata.parts().get(0).skipTicks());
        assertFalse(metadata.parts().get(0).interpolate());
        assertFalse(metadata.parts().get(0).smoothAlpha());
        assertFalse(metadata.parts().get(0).daytimeSync());
        assertTrue(metadata.parts().get(0).predefinedFrames().isEmpty());

        assertEquals(18, metadata.parts().get(1).frameWidth());
        assertEquals(27, metadata.parts().get(1).frameHeight());
        assertEquals(1, metadata.parts().get(1).xInBase());
        assertEquals(2, metadata.parts().get(1).yInBase());
        assertEquals(5, metadata.parts().get(1).defaultTime());
        assertEquals(12, metadata.parts().get(1).skipTicks());
        assertTrue(metadata.parts().get(1).interpolate());
        assertTrue(metadata.parts().get(1).smoothAlpha());
        assertFalse(metadata.parts().get(1).daytimeSync());
        assertEquals(
                List.of(IntIntPair.of(5, 12), IntIntPair.of(2, 5), IntIntPair.of(0, 7)),
                metadata.parts().get(1).predefinedFrames()
        );

        assertEquals(10, metadata.parts().get(2).frameWidth());
        assertEquals(10, metadata.parts().get(2).frameHeight());
        assertEquals(4, metadata.parts().get(2).xInBase());
        assertEquals(4, metadata.parts().get(2).yInBase());
        assertEquals(15, metadata.parts().get(2).defaultTime());
        assertEquals(0, metadata.parts().get(2).skipTicks());
        assertTrue(metadata.parts().get(2).interpolate());
        assertFalse(metadata.parts().get(2).smoothAlpha());
        assertTrue(metadata.parts().get(2).daytimeSync());
        assertTrue(metadata.parts().get(2).predefinedFrames().isEmpty());
    }

}
