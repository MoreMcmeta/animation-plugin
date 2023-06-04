package io.github.moremcmeta.animationplugin.forge;

import io.github.moremcmeta.animationplugin.ModConstants;
import io.github.moremcmeta.moremcmeta.api.client.MoreMcmetaTexturePlugin;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.texture.ComponentProvider;
import io.github.moremcmeta.moremcmeta.forge.api.client.MoreMcmetaClientPlugin;

/**
 * Implementation of the animation plugin on Forge.
 * @author soir20
 */
@SuppressWarnings("unused")
@MoreMcmetaClientPlugin
public class AnimationPluginForge implements MoreMcmetaTexturePlugin {
    @Override
    public String sectionName() {
        return ModConstants.SECTION_NAME;
    }

    @Override
    public MetadataAnalyzer analyzer() {
        return ModConstants.ANALYZER;
    }

    @Override
    public ComponentProvider componentProvider() {
        return ModConstants.COMPONENT_PROVIDER;
    }

    @Override
    public String id() {
        return ModConstants.MOD_ID;
    }
}
