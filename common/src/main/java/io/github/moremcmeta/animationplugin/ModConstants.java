package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataParser;
import io.github.moremcmeta.moremcmeta.api.client.texture.ComponentProvider;

/**
 * Constants for both Fabric and Forge implementations of the plugin.
 * @author soir20
 */
public class ModConstants {
    public static final String MOD_ID = "moremcmeta_animation_plugin";
    public static final String SECTION_NAME = "animation";
    public static final String DISPLAY_NAME = "MoreMcmeta Animations";
    public static final MetadataParser PARSER = new AnimationMetadataParser();
    public static final ComponentProvider COMPONENT_PROVIDER = new AnimationComponentProvider();
}
