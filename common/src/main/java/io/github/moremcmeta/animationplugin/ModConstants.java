package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.animationplugin.parse.AnimationComponentProvider;
import io.github.moremcmeta.animationplugin.parse.AnimationMetadataParser;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataParser;
import io.github.moremcmeta.moremcmeta.api.client.texture.ComponentProvider;
import io.github.moremcmeta.moremcmeta.api.client.texture.TextureHandle;
import net.minecraft.client.Minecraft;

import java.util.Optional;

/**
 * Constants for both Fabric and Forge implementations of the plugin.
 * @author soir20
 */
public class ModConstants {
    public static final String MOD_ID = "moremcmeta_animation_plugin";
    public static final String SECTION_NAME = "animation";
    public static final String DISPLAY_NAME = "MoreMcmeta Animations";
    public static final MetadataParser PARSER = new AnimationMetadataParser(TextureHandle::find);
    public static final ComponentProvider COMPONENT_PROVIDER = new AnimationComponentProvider(
            () -> Optional.ofNullable(Minecraft.getInstance().level)
    );
}
