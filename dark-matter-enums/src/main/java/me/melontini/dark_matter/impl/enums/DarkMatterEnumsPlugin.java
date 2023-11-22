package me.melontini.dark_matter.impl.enums;

import me.melontini.dark_matter.api.base.util.mixin.ExtendablePlugin;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

public class DarkMatterEnumsPlugin extends ExtendablePlugin {

    @Override
    protected void collectPlugins(Set<IPluginPlugin> plugins) {
        plugins.add(DefaultPlugins.asmTransformerPlugin());
        plugins.add(DefaultPlugins.publicizePlugin());
    }
}
