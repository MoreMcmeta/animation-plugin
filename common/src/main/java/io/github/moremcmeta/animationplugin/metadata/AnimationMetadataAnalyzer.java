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
import com.mojang.blaze3d.platform.NativeImage;
import io.github.moremcmeta.animationplugin.animate.Frame;
import io.github.moremcmeta.moremcmeta.api.client.metadata.AnalyzedMetadata;
import io.github.moremcmeta.moremcmeta.api.client.metadata.InvalidMetadataException;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;
import it.unimi.dsi.fastutil.ints.IntIntPair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Parses animation metadata into {@link AnimationMetadata}s.
 * @author soir20
 */
public final class AnimationMetadataAnalyzer implements MetadataAnalyzer {

    @Override
    public AnalyzedMetadata analyze(MetadataView metadata, int imageWidth, int imageHeight) throws InvalidMetadataException {
        requireNonNull(metadata, "Metadata cannot be null");

        List<AnimationMetadata> animations = new ArrayList<>();
        int frameWidth;
        int frameHeight;

        Optional<MetadataView> partsViewOptional = metadata.subView("parts");
        if (partsViewOptional.isPresent()) {
            frameWidth = imageWidth;
            frameHeight = imageHeight;
            MetadataView partsView = partsViewOptional.get();

            for (int index = 0; index < partsView.size(); index++) {
                Optional<MetadataView> singleAnimViewOptional = partsView.subView(index);

                if (singleAnimViewOptional.isPresent()) {
                    MetadataView singleAnimView = singleAnimViewOptional.get();
                    NativeImage texture = readTexture(singleAnimView);

                    AnimationMetadata part = readAnimationProperties(
                            singleAnimView,
                            texture.getWidth(),
                            texture.getHeight(),
                            texture
                    );

                    boolean isOutsideX = part.xInBase() + part.frameWidth() > frameWidth;
                    boolean isOutsideY = part.yInBase() + part.frameHeight() > frameHeight;
                    if (isOutsideX || isOutsideY) {
                        throw new InvalidMetadataException(String.format(
                                "Part (%sx%s) extends outside base texture (%sx%s)",
                                part.frameWidth(), part.frameHeight(), frameWidth, frameHeight
                        ));
                    }

                    animations.add(part);
                }
            }
        } else {
            AnimationMetadata baseAnimation = readAnimationProperties(metadata, imageWidth, imageHeight, null);
            frameWidth = baseAnimation.frameWidth();
            frameHeight = baseAnimation.frameHeight();
            animations.add(baseAnimation);
        }

        return new AnimationGroupMetadata(frameWidth, frameHeight, animations);
    }

    /**
     * Reads a texture from the "texture" key in the given view.
     * @param animationView     view with all animation properties
     * @return the read texture
     * @throws InvalidMetadataException if the texture is missing or not valid
     */
    private NativeImage readTexture(MetadataView animationView) throws InvalidMetadataException {
        InputStream textureData = animationView.byteStreamValue("texture")
                .orElseThrow(() -> new InvalidMetadataException("Animation part has no texture defined"));

        NativeImage texture;
        try {
            texture = NativeImage.read(textureData);
        } catch (IOException err) {
            throw new InvalidMetadataException("Part texture is not a valid texture");
        }

        return texture;
    }

    /**
     * Reads all animation properties from a given view.
     * @param metadata      metadata containing animation properties
     * @param imageWidth    width of the texture containing this animation's frames
     * @param imageHeight   height of the texture containing this animation's frames
     * @param partTexture   texture containing this animation's frames
     * @return the read animation metadata
     * @throws InvalidMetadataException if the metadata is not valid
     */
    private AnimationMetadata readAnimationProperties(MetadataView metadata, int imageWidth, int imageHeight,
                                                      NativeImage partTexture) throws InvalidMetadataException {
        Optional<Integer> metadataFrameWidth = metadata.integerValue("width");
        Optional<Integer> metadataFrameHeight = metadata.integerValue("height");

        int frameWidth = metadataFrameWidth.orElse(imageWidth);
        int frameHeight = metadataFrameHeight.orElse(imageHeight);
        if (metadataFrameWidth.isEmpty() && metadataFrameHeight.isEmpty()) {
            int dimension = Math.min(frameWidth, frameHeight);
            frameWidth = dimension;
            frameHeight = dimension;
        }

        if (frameWidth <= 0 || frameHeight <= 0) {
            throw new InvalidMetadataException("Frame width and height must be positive");
        }
        if (frameWidth > imageWidth) {
            throw new InvalidMetadataException("Frame width " + frameWidth + " cannot be greater than image width "
                    + imageWidth);
        }
        if (frameHeight > imageHeight) {
            throw new InvalidMetadataException("Frame height " + frameHeight + " cannot be greater than image height "
                    + imageHeight);
        }

        int defaultTime = metadata.integerValue("frametime").orElse(1);
        if (defaultTime <= 0) {
            throw new InvalidMetadataException("Frame time must be positive but was: " + defaultTime);
        }

        boolean interpolate = metadata.booleanValue("interpolate").orElse(false);
        boolean smoothAlpha = metadata.booleanValue("smoothAlpha").orElse(false);
        boolean daytimeSync = metadata.booleanValue("daytimeSync").orElse(false);

        Optional<MetadataView> framesViewOptional = metadata.subView("frames");
        List<IntIntPair> frames;
        int maxIndex = (imageWidth / frameWidth) * (imageHeight / frameHeight) - 1;
        if (framesViewOptional.isPresent()) {
            frames = parseFrameList(framesViewOptional.get(), defaultTime);
        } else {
            frames = ImmutableList.of();
        }

        Optional<Integer> outOfBoundsIndex = frames.stream()
                .map(IntIntPair::firstInt)
                .filter((index) -> index > maxIndex)
                .findAny();
        if (outOfBoundsIndex.isPresent()) {
            throw new InvalidMetadataException("Frame index must be no more than " + maxIndex + ", but was "
                    + outOfBoundsIndex.get());
        }

        int xInBase = 0;
        int yInBase = 0;
        if (partTexture != null) {
            xInBase = metadata.integerValue("x")
                    .orElseThrow(() -> new InvalidMetadataException("Part defined without x coordinate"));
            yInBase = metadata.integerValue("y")
                    .orElseThrow(() -> new InvalidMetadataException("Part defined without y coordinate"));
        }

        if (xInBase < 0) {
            throw new InvalidMetadataException("Negative x coordinate: " + xInBase);
        }
        if (yInBase < 0) {
            throw new InvalidMetadataException("Negative y coordinate: " + yInBase);
        }

        int skipTicks = metadata.integerValue("skip").orElse(0);
        if (skipTicks < 0) {
            throw new InvalidMetadataException("Skip ticks cannot be negative but was: " + skipTicks);
        }

        Optional<List<Frame>> partFrames;
        if (partTexture == null) {
            partFrames = Optional.empty();
        } else {
            partFrames = Optional.of(findFrames(partTexture, frameWidth, frameHeight));
        }

        return new AnimationMetadata(
                frameWidth,
                frameHeight,
                defaultTime,
                interpolate,
                smoothAlpha,
                frames,
                skipTicks,
                daytimeSync,
                xInBase,
                yInBase,
                partFrames,
                partTexture == null ? () -> {} : partTexture::close
        );
    }

    /**
     * Generates a list of animation frames from a given texture. Frames are read by row.
     * @param texture       texture to read frames from
     * @param frameWidth    width of a frame in the texture
     * @param frameHeight   height of a frame in the texture
     * @return all frames found in the texture
     */
    private List<Frame> findFrames(NativeImage texture, int frameWidth, int frameHeight) {
        List<Frame> frames = new ArrayList<>();

        for (int minY = 0; minY < texture.getHeight(); minY += frameHeight) {
            for (int minX = 0; minX < texture.getWidth(); minX += frameWidth) {
                int finalMinX = minX;
                int finalMinY = minY;
                frames.add((x, y) -> texture.getPixelRGBA(finalMinX + x, finalMinY + y));
            }
        }

        return frames;
    }

    /**
     * Parses all the frames from an array of frame metadata.
     * @param framesView        array of frame metadata
     * @param defaultTime       default time for frames in the animation
     * @return all frames in the animation as (index, time) pairs
     * @throws InvalidMetadataException if any frames within the array are missing an index
     */
    private List<IntIntPair> parseFrameList(MetadataView framesView, int defaultTime)
            throws InvalidMetadataException {
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

}
