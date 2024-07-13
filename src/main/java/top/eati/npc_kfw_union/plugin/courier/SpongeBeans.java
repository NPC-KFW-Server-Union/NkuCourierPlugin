package top.eati.npc_kfw_union.plugin.courier;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class SpongeBeans {
    public static class _Beans {
        public static PluginContainer pluginContainer;
        public static Logger spongeLogger;
        public static Path defaultConfFile;
        public static ConfigurationLoader<CommentedConfigurationNode> confMan;
    }

    public PluginContainer getPluginContainer() {
        return _Beans.pluginContainer;
    }

    public Logger getSpongeLogger() {
        return _Beans.spongeLogger;
    }


    public Path getDefaultConfFile() {
        return _Beans.defaultConfFile;
    }
    public ConfigurationLoader<CommentedConfigurationNode> getConfMan() {
        return _Beans.confMan;
    }
}
