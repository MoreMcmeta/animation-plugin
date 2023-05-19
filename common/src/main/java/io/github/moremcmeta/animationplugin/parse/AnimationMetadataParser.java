package io.github.moremcmeta.animationplugin.parse;

import com.google.common.collect.ImmutableList;
import io.github.moremcmeta.animationplugin.ModConstants;
import io.github.moremcmeta.moremcmeta.api.client.metadata.InvalidMetadataException;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataParser;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;
import io.github.moremcmeta.moremcmeta.api.client.metadata.ParsedMetadata;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Parses animation metadata into {@link AnimationMetadata}s.
 * @author soir20
 */
public class AnimationMetadataParser implements MetadataParser {

    @Override
    public ParsedMetadata parse(MetadataView metadata, int imageWidth, int imageHeight) throws InvalidMetadataException {
        requireNonNull(metadata, "Metadata cannot be null");

        MetadataView sectionMetadata = metadata.subView(ModConstants.SECTION_NAME).orElseThrow();

        Optional<Integer> metadataFrameWidth = sectionMetadata.integerValue("width");
        Optional<Integer> metadataFrameHeight = sectionMetadata.integerValue("height");

        int frameWidth = metadataFrameWidth.orElse(imageWidth);
        int frameHeight = metadataFrameHeight.orElse(imageHeight);
        if (metadataFrameWidth.isEmpty() && metadataFrameHeight.isEmpty()) {
            int dimension = Math.min(frameWidth, frameHeight);
            frameWidth = dimension;
            frameHeight = dimension;
        }

        int defaultTime = sectionMetadata.integerValue("frametime").orElse(1);
        if (defaultTime <= 0) {
            throw new InvalidMetadataException("Frame time must be positive but was: " + defaultTime);
        }

        boolean interpolate = sectionMetadata.booleanValue("interpolate").orElse(false);
        boolean smoothAlpha = sectionMetadata.booleanValue("smoothAlpha").orElse(false);
        boolean daytimeSync = sectionMetadata.booleanValue("daytimeSync").orElse(false);

        Optional<MetadataView> framesViewOptional = sectionMetadata.subView("frames");
        List<IntIntPair> frames;
        if (framesViewOptional.isPresent()) {
            frames = parseFrameList(framesViewOptional.get(), defaultTime);
        } else {
            frames = ImmutableList.of();
        }

        Optional<ResourceLocation> base = Optional.empty();
        int uploadX = 0;
        int uploadY = 0;
        Optional<String> baseLocation = sectionMetadata.stringValue("base");
        if (baseLocation.isPresent()) {
            base = Optional.of(parseResourceLocation(baseLocation.get()));
            uploadX = sectionMetadata.integerValue("x")
                    .orElseThrow(() -> new InvalidMetadataException("Base defined without x upload coordinate"));
            uploadY = sectionMetadata.integerValue("y")
                    .orElseThrow(() -> new InvalidMetadataException("Base defined without y upload coordinate"));

            if (uploadX < 0 || uploadY < 0) {
                throw new InvalidMetadataException("Upload x- and y-coordinates must be non-negative");
            }
        }

        int skipTicks = sectionMetadata.integerValue("skip").orElse(0);
        if (skipTicks < 0) {
            throw new InvalidMetadataException("Skip ticks cannot be negative but was: " + skipTicks);
        }

        return new AnimationMetadata(
                frameWidth,
                frameHeight,
                defaultTime,
                interpolate,
                smoothAlpha,
                frames,
                base,
                uploadX,
                uploadY,
                skipTicks,
                daytimeSync
        );
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
            throw new InvalidMetadataException("Frame index cannot be negative, but was " + index);
        }

        int frameTime = frameObj.integerValue("time").orElse(defaultTime);
        if (frameTime <= 0) {
            throw new InvalidMetadataException("Frame time must be greater than zero, but was " + frameTime);
        }

        return IntIntPair.of(index, frameTime);
    }

    /**
     * Parses a string into a {@link ResourceLocation}.
     * @param location      string to convert to a resource location
     * @return resource location based on the given string
     * @throws InvalidMetadataException if the resource location contains invalid characters
     */
    private ResourceLocation parseResourceLocation(String location) throws InvalidMetadataException {
        try {
            return new ResourceLocation(location);
        } catch (ResourceLocationException err) {
            throw new InvalidMetadataException(err.getMessage());
        }
    }

}
