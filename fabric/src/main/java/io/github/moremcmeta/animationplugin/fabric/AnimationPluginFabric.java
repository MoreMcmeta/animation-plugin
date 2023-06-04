package io.github.moremcmeta.animationplugin.fabric;

import io.github.moremcmeta.animationplugin.ModConstants;
import io.github.moremcmeta.moremcmeta.api.client.MoreMcmetaTexturePlugin;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.texture.ComponentProvider;

/**
 * Implementation of the animation plugin on Fabric.
 * @author soir20
 */
public class AnimationPluginFabric implements MoreMcmetaTexturePlugin {
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
