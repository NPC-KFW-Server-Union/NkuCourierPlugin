package top.eati.npc_kfw_union.plugin.courier;

import com.google.inject.Inject;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;
import top.eati.npc_kfw_union.plugin.courier.excption.LoadConfFormFileFailed;
import top.eati.npc_kfw_union.plugin.courier.service.*;

import java.io.IOException;
import java.nio.file.Path;


/**
 * The main class of your Sponge plugin.
 *
 * <p>All methods are optional -- some common event registrations are included as a jumping-off point.</p>
 */
@Plugin(
        id = "nku-courier",
        name = "NPC&KFW Courier Plugin",
        description = "Bridge minecraft player's chat with Mirai",
        version = NkuCourierPlugin.VERSION
)
public class NkuCourierPlugin {
    public static final String VERSION = "1.0-SNAPSHOT";

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfFile;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> confMan;

    ApplicationContext appContext;

    public NkuCourierPlugin() {

    }


    @Component
    public static class MyService {
        public void test() {
            System.out.println("HI!");
        }
    }


    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        logger.info("NKU Minecraft Player Chat 2 Mirai Plugin, " + VERSION);
        SpongeBeans._Beans.pluginContainer = this.pluginContainer;
        SpongeBeans._Beans.spongeLogger = this.logger;
        SpongeBeans._Beans.confMan = this.confMan;
        SpongeBeans._Beans.defaultConfFile = this.defaultConfFile;

        this.appContext = new AnnotationConfigApplicationContext(FormalSpringConf.class);

        //appContext.getBean(McAndMiraiMsgServ.class).test();
        logger.info("Init complete.");

//        appContext.getBean(FormalMcMsgServ.class).onMcChatEvent(
//                new McPlayerChatEvent("test", Text.builder("hehe").build())
//        );


    }

    @Listener
    public void onPostInit(GamePostInitializationEvent e) {
        appContext.getBean(McAndMiraiCourierServ.class).start();
        appContext.getBean(McCommandServ.class).regCmds();
    }



    @Listener
    public void onMessageChannelChat(MessageChannelEvent.Chat event) {

        if(!(event.getSource() instanceof Player))
            return;

        Player player = (Player) event.getSource();

        appContext.getBean(FormalMcMsgServ.class).onMcChatEvent(
                new McPlayerChatEvent(player.getName(), event.getMessage())
        );
    }

    @Listener
    public void onGameReload(GameReloadEvent event) throws LoadConfFormFileFailed, IOException {
        if(!(event.getSource() instanceof CommandSource)) {
            logger.error("McMsg2MiraiPlugin::onGameReload: event.getSource() 不是 CommandSource。");
            return;
        }

        CommandSource cmder = (CommandSource) event.getSource();
        cmder.sendMessage(Text.of("重新从文件加载配置……"));
        try {
            ((FormalConfServ)appContext.getBean(IConfServ.class)).
                    reloadConfFromConfFile();
        } catch (Exception e) {
            cmder.sendMessage(Text.builder("加载配置失败： " + e)
                    .color(TextColors.RED)
                    .build()
            );
            throw e;
        }
        cmder.sendMessage(Text.of("重新从文件加载配置毕！"));
        cmder.sendMessage(Text.of(
                "注意：要让新的配置生效，需重启 McAndMiraiCourierServ： \n" +
                        "/nkucourier restart"));
    }
}
