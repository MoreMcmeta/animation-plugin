package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.animationplugin.metadata.AnimationComponentProvider;
import io.github.moremcmeta.animationplugin.metadata.AnimationMetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.texture.ComponentProvider;
import net.minecraft.client.Minecraft;

import java.util.Optional;

/**
 * Constants for both Fabric and Forge implementations of the plugin.
 * @author soir20
 */
public class ModConstants {
    public static final String MOD_ID = "moremcmeta_animation_plugin";
    public static final String SECTION_NAME = "animation";
    public static final MetadataAnalyzer ANALYZER = new AnimationMetadataAnalyzer();
    public static final ComponentProvider COMPONENT_PROVIDER = new AnimationComponentProvider(
            () -> Optional.ofNullable(Minecraft.getInstance().level)
    );
}
