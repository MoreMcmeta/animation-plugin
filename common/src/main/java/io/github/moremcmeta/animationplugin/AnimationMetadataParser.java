package io.github.moremcmeta.animationplugin;

import com.google.common.collect.ImmutableList;
import io.github.moremcmeta.moremcmeta.api.client.metadata.InvalidMetadataException;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataParser;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;
import io.github.moremcmeta.moremcmeta.api.client.metadata.ParsedMetadata;
import it.unimi.dsi.fastutil.ints.IntIntPair;

import java.util.List;
import java.util.Optional;

/**
 * Parses animation metadata into {@link AnimationMetadata}s.
 * @author soir20
 */
public class AnimationMetadataParser implements MetadataParser {
    @Override
    public ParsedMetadata parse(MetadataView metadata) throws InvalidMetadataException {
        MetadataView sectionMetadata = metadata.subView(ModConstants.SECTION_NAME).orElseThrow();

        Optional<Integer> frameWidth = sectionMetadata.integerValue("width");
        Optional<Integer> frameHeight = sectionMetadata.integerValue("height");

        int defaultTime = sectionMetadata.integerValue("frametime").orElse(1);
        if (defaultTime <= 0) {
            throw new InvalidMetadataException("Frame time must be positive but was: " + defaultTime);
        }

        boolean interpolate = sectionMetadata.booleanValue("interpolate").orElse(false);

        Optional<MetadataView> framesViewOptional = sectionMetadata.subView("frames");

        List<IntIntPair> frames;
        if (framesViewOptional.isPresent()) {
            frames = parseFrameList(framesViewOptional.get(), defaultTime);
        } else {
            frames = ImmutableList.of();
        }

        return new AnimationMetadata(frameWidth, frameHeight, defaultTime, interpolate, frames);
    }

    /**
     * Parses all the frames from an array of frame metadata.
     * @param framesView        array of frame metadata
     * @param defaultTime       default time for frames in the animation
     * @return all frames in the animation as (index, time) pairs
     * @throws InvalidMetadataException if any frames within the array are missing an index
     */
    private List<IntIntPair> parseFrameList(MetadataView framesView, int defaultTime) throws InvalidMetadataException {
        ImmutableList.Builder<IntIntPair> frames = new ImmutableList.Builder<>();

        for (int index = 0; index < framesView.size(); index++) {

            // Either an integer value is present, or a sub view is present
            framesView.integerValue(index).ifPresent((frameIndex) -> frames.add(IntIntPair.of(frameIndex, defaultTime)));

            Optional<MetadataView> frameObjOptional = framesView.subView(index);
            if (frameObjOptional.isPresent()) {
                MetadataView frameObj = frameObjOptional.get();
                frames.add(parseFrameObj(frameObj, defaultTime));
            }

        }

        return frames.build();
    }

    /**
     * Parses a single frame from an array of frame metadata.
     * @param frameObj      frame object to parser
     * @param defaultTime   default time for frames in the animation
     * @return pair of the frame index and its time
     * @throws InvalidMetadataException if the frame index is missing
     */
    private IntIntPair parseFrameObj(MetadataView frameObj, int defaultTime) throws InvalidMetadataException {
        int index = frameObj.integerValue("index").orElseThrow(
                () -> new InvalidMetadataException("Missing required property \"index\" for")
        );
        if (index < 0) {
            throw new RuntimeException();
        }

        int frameTime = frameObj.integerValue("time").orElse(defaultTime);
        return IntIntPair.of(index, frameTime);
    }

}
