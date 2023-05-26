package io.github.moremcmeta.animationplugin.parse;

import io.github.moremcmeta.animationplugin.animate.AnimationComponent;
import io.github.moremcmeta.animationplugin.animate.DefaultAlphaInterpolator;
import io.github.moremcmeta.animationplugin.animate.Interpolator;
import io.github.moremcmeta.animationplugin.animate.SmoothAlphaInterpolator;
import io.github.moremcmeta.animationplugin.animate.WobbleFunction;
import io.github.moremcmeta.moremcmeta.api.client.metadata.ParsedMetadata;
import io.github.moremcmeta.moremcmeta.api.client.texture.Color;
import io.github.moremcmeta.moremcmeta.api.client.texture.ComponentProvider;
import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.FrameGroup;
import io.github.moremcmeta.moremcmeta.api.client.texture.MutableFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.TextureComponent;
import io.github.moremcmeta.moremcmeta.api.math.Area;
import io.github.moremcmeta.moremcmeta.api.math.Point;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Generates {@link AnimationComponent}s from {@link AnimationMetadata}.
 * @author soir20
 */
public class AnimationComponentProvider implements ComponentProvider {
    private static final int TICKS_PER_DAY = 24000;
    private static final WobbleFunction WOBBLE_FUNCTION = new WobbleFunction();

    private final Supplier<Optional<ClientLevel>> LEVEL_SUPPLIER;

    /**
     * Creates a new animation component provider.
     * @param levelSupplier     supplies the
     */
    public AnimationComponentProvider(Supplier<Optional<ClientLevel>> levelSupplier) {
        LEVEL_SUPPLIER = requireNonNull(levelSupplier, "Level supplier cannot be null");
    }

    @Override
    public TextureComponent<CurrentFrameView>
    assemble(ParsedMetadata metadata, FrameGroup<? extends MutableFrameView> frames) {
        requireNonNull(metadata, "Metadata cannot be null");
        requireNonNull(frames, "Frame group cannot be null");

        // This cast is guaranteed to work since the metadata object is the same as was generated by the parser
        if (!(metadata instanceof AnimationMetadata animationMetadata)) {
            throw new IllegalArgumentException("Metadata provided to animation component provider is not animation " +
                    "metadata. Something is wrong with the core MoreMcmeta mod.");
        }

        Area changedArea = animationMetadata.interpolate() ? findChangedArea(frames) : Area.of();
        List<IntIntPair> predefinedFrames = animationMetadata.predefinedFrames();

        // Number of frames
        int frameCount = frames.frames();
        if (!predefinedFrames.isEmpty()) {
            frameCount = predefinedFrames.size();
        }

        // Frame time calculation
        IntUnaryOperator frameTimeCalculator = (index) -> {
            if (predefinedFrames.isEmpty()) {
                return animationMetadata.defaultTime();
            }

            return predefinedFrames.get(index).rightInt();
        };

        // Index mapping
        IntUnaryOperator frameIndexMapper = (index) -> {
            if (predefinedFrames.isEmpty()) {
                return index;
            }

            return predefinedFrames.get(index).leftInt();
        };

        // Time retrieval
        Supplier<Optional<Long>> timeGetter = () -> {
            Optional<ClientLevel> levelOptional = LEVEL_SUPPLIER.get();
            if (levelOptional.isEmpty()) {
                return Optional.empty();
            }

            ClientLevel level = levelOptional.get();
            long time = WOBBLE_FUNCTION.calculate(level.dayTime(), level.getGameTime(), level.dimensionType().natural());
            return Optional.of(time);
        };

        Interpolator interpolator;
        if (animationMetadata.smoothAlpha()) {
            interpolator = new SmoothAlphaInterpolator();
        } else {
            interpolator = new DefaultAlphaInterpolator();
        }

        AnimationComponent.Builder componentBuilder = new AnimationComponent.Builder();
        componentBuilder.interpolateArea(changedArea)
                .frames(frameCount)
                .ticksUntilStart(animationMetadata.skipTicks())
                .frameTimeCalculator(frameTimeCalculator)
                .frameIndexMapper(frameIndexMapper)
                .interpolator(interpolator);

        if (animationMetadata.daytimeSync()) {
            componentBuilder.syncTicks(TICKS_PER_DAY, timeGetter);
        }

        return componentBuilder.build();
    }

    /**
     * Gets the pixels that will change throughout the animation. If the image
     * is empty, an empty area will be returned.
     * @param frames        frames to analyze
     * @return area representing pixels that change throughout the animation
     */
    private static Area findChangedArea(FrameGroup<? extends MutableFrameView> frames) {
        requireNonNull(frames, "Image cannot be null");
        if (frames.frames() == 0) {
            return Area.of();
        }

        MutableFrameView firstFrame = frames.frame(0);
        Area fullFrameArea = new Area(0, 0, firstFrame.width(), firstFrame.height());
        Area.Builder areaBuilder = new Area.Builder();

        // For every point in the first frame, check that point in the other frames
        firstFrame.transform(
                (overwriteX, overwriteY, dependencyFunction) -> {
                    int firstColor = dependencyFunction.color(overwriteX, overwriteY);

                    // Check the point in each of the other frames
                    AtomicBoolean isDifferent = new AtomicBoolean();
                    for (int frameIndex = 1; frameIndex < frames.frames(); frameIndex++) {
                        frames.frame(frameIndex).transform(
                                (otherOverwriteX, otherOverwriteY, otherDependencyFunction) -> {
                                    int otherColor = otherDependencyFunction.color(otherOverwriteX, otherOverwriteY);
                                    if (!Color.equalsOrBothInvisible(firstColor, otherColor)) {
                                        areaBuilder.addPixel(overwriteX, overwriteY);
                                        isDifferent.set(true);
                                    }

                                    return otherDependencyFunction.color(otherOverwriteX, otherOverwriteY);
                                },
                                Area.of(Point.pack(overwriteX, overwriteY)),
                                Area.of(Point.pack(overwriteX, overwriteY))
                        );

                        // If a different point is found, checking the other frames is unnecessary
                        if (isDifferent.get()) {
                            break;
                        }

                    }

                    return dependencyFunction.color(overwriteX, overwriteY);
                },
                fullFrameArea,
                fullFrameArea
        );

        return areaBuilder.build();
    }

}
