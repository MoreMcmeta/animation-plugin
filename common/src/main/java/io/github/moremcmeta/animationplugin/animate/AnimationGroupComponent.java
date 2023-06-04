package io.github.moremcmeta.animationplugin.animate;

import com.mojang.datafixers.util.Pair;
import io.github.moremcmeta.moremcmeta.api.client.texture.CurrentFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.FrameGroup;
import io.github.moremcmeta.moremcmeta.api.client.texture.PersistentFrameView;
import io.github.moremcmeta.moremcmeta.api.client.texture.TextureComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Updates several separate animations within one texture.
 * @author soir20
 */
public class AnimationGroupComponent implements TextureComponent<CurrentFrameView> {
    private final Collection<Pair<AnimationComponent, Optional<List<Frame>>>> COMPONENTS;
    private final Collection<Runnable> RESOURCE_CLOSERS;
    private List<Frame> predefinedFrameCache;

    /**
     * Creates a new group component.
     * @param components        components and their frames, if they should not use the base texture's frames
     * @param resourceClosers   closes resources used by all the components
     */
    public AnimationGroupComponent(Collection<Pair<AnimationComponent, Optional<List<Frame>>>> components,
                                   Collection<Runnable> resourceClosers) {
        COMPONENTS = requireNonNull(components, "Components cannot be null");
        RESOURCE_CLOSERS = requireNonNull(resourceClosers, "Resource closers cannot be null");
    }

    @Override
    public void onTick(CurrentFrameView currentFrame, FrameGroup<PersistentFrameView> predefinedFrames) {
        if (predefinedFrameCache == null) {
            predefinedFrameCache = wrapFrames(predefinedFrames);
        }

        COMPONENTS.forEach((pair) ->
            pair.getSecond().ifPresentOrElse(
                    (frames) -> pair.getFirst().onTick(currentFrame, frames),
                    () -> pair.getFirst().onTick(currentFrame, predefinedFrameCache)
            )
        );
    }

    @Override
    public void onClose(CurrentFrameView currentFrame, FrameGroup<PersistentFrameView> predefinedFrames) {
        RESOURCE_CLOSERS.forEach(Runnable::run);
    }

    /**
     * Wraps predefined farms so that they conform to the {@link Frame} interface.
     * @param frames    frames to wrap
     * @return wrapped frames
     */
    private static List<Frame> wrapFrames(FrameGroup<PersistentFrameView> frames) {
        List<Frame> wrappedFrames = new ArrayList<>();
        for (int index = 0; index < frames.frames(); index++) {
            wrappedFrames.add(frames.frame(index)::color);
        }

        return wrappedFrames;
    }

}
