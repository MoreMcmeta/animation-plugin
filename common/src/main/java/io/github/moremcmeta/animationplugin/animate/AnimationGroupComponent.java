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
public final class AnimationGroupComponent implements TextureComponent<CurrentFrameView> {
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
    public void onTick(CurrentFrameView currentFrame, FrameGroup<? extends PersistentFrameView> predefinedFrames) {
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
    public void onClose(CurrentFrameView currentFrame, FrameGroup<? extends PersistentFrameView> predefinedFrames) {
        RESOURCE_CLOSERS.forEach(Runnable::run);
    }

    /**
     * Wraps predefined farms so that they conform to the {@link Frame} interface.
     * @param frames    frames to wrap
     * @return wrapped frames
     */
    private static List<Frame> wrapFrames(FrameGroup<? extends PersistentFrameView> frames) {
        List<Frame> wrappedFrames = new ArrayList<>();
        for (int index = 0; index < frames.frames(); index++) {
            wrappedFrames.add(frames.frame(index)::color);
        }

        return wrappedFrames;
    }

}
