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

import io.github.moremcmeta.moremcmeta.api.client.metadata.AnalyzedMetadata;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Holds all {@link AnimationMetadata} for animations within the same texture.
 * @author soir20
 */
public final class AnimationGroupMetadata implements AnalyzedMetadata {
    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;
    private final List<AnimationMetadata> PARTS;

    /**
     * Creates a new group of animation metadata.
     * @param frameWidth        width of a frame in the base texture only
     * @param frameHeight       height of a frame in the base texture only
     * @param parts             all parts of the animation/members of the group
     */
    public AnimationGroupMetadata(int frameWidth, int frameHeight, List<AnimationMetadata> parts) {
        FRAME_WIDTH = frameWidth;
        FRAME_HEIGHT = frameHeight;
        PARTS = requireNonNull(parts, "Parts cannot be null");
    }

    @Override
    public Optional<Integer> frameWidth() {
        return Optional.of(FRAME_WIDTH);
    }

    @Override
    public Optional<Integer> frameHeight() {
        return Optional.of(FRAME_HEIGHT);
    }

    /**
     * Gets the list of all animations in this group/within the same texture.
     * @return all animations in this group
     */
    public List<AnimationMetadata> parts() {
        return PARTS;
    }

}
