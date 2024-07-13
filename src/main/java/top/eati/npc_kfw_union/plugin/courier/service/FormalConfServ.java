package top.eati.npc_kfw_union.plugin.courier.service;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.apache.commons.validator.routines.UrlValidator;
import org.spongepowered.api.Sponge;
import org.springframework.beans.factory.annotation.Autowired;
import top.eati.npc_kfw_union.plugin.courier.SpongeBeans;
import top.eati.npc_kfw_union.plugin.courier.entity.Conf;
import top.eati.npc_kfw_union.plugin.courier.excption.LoadConfFormFileFailed;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 用於游戲運行時的配置服務。
 */
public class FormalConfServ implements IConfServ {
    private final SpongeBeans spongeBeans;
    private final ILogServ logServ;
    private Conf conf;

    private void loadConfFromNode(ConfigurationNode node) throws LoadConfFormFileFailed {
        StringBuilder errMsgSb = new StringBuilder();
        boolean failed = false;

        String miraiHttpServerUrl = node.getNode("miraiHttpServerUrl").getString();
        String[] urlSchemes = { "http", "https" };
        if(!new UrlValidator(urlSchemes).isValid(miraiHttpServerUrl)) {
            errMsgSb.append(String.format(
                    "MiraiHttpServerUrl 不是合法的 http URL。目前值爲 '%s'。",
                    miraiHttpServerUrl)
            );
            failed = true;
        }

        String miraiHttpServerToken = node.getNode("miraiHttpServerToken").getString();

        long qqBotId = node.getNode("qqBotId").getLong();
        if(qqBotId == 0L) {
            errMsgSb.append(String.format(
                    "qqBotId 爲空或不是數字。目前值爲 '%s'。\n",
                    qqBotId)
            );
            failed = true;
        }

        long qqGroupId = node.getNode("qqGroupId").getLong();
        if(qqGroupId == 0L) {
            errMsgSb.append(String.format(
                    "qqGroupId 爲空或不是數字。目前值爲 '%s'。\n",
                    qqGroupId)
            );
            failed = true;
        }

        if(failed) {
            this.conf = new Conf(false);
            throw new LoadConfFormFileFailed("驗證配置失敗：\n" + errMsgSb.toString());
        }

        this.conf = new Conf(
                miraiHttpServerUrl,
                miraiHttpServerToken,
                qqBotId,
                qqGroupId,
                false
        );
    }

    @Autowired
    public FormalConfServ(SpongeBeans spongeBeans, ILogServ logServ) {
        this.spongeBeans = spongeBeans;
        this.logServ = logServ;

        createInitConfFileIfTheresNone();

        try {
            reloadConfFromConfFile();
        } catch (LoadConfFormFileFailed | IOException e) {
            logServ.err("从文件加载配置失败：", e);
            logServ.err("请修正配置文件后 reload。");
        }
    }

    public void createInitConfFileIfTheresNone() {
        Path confFile = spongeBeans.getDefaultConfFile();

        try {
            Sponge.getAssetManager()
                    .getAsset(spongeBeans.getPluginContainer(), "default.conf")
                    .orElseThrow(
                            () -> new IOException("找不到缺省配置文件！？WTF？")
                    )
                    .copyToFile(confFile, false, true);
        } catch (IOException e) {
            throw new RuntimeException("创建初始配置文件失败", e);
        }
    }


    public void reloadConfFromConfFile() throws LoadConfFormFileFailed, IOException {
        logServ.info("正在重新载入配置文件……");
        Path confFile = spongeBeans.getDefaultConfFile();
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(confFile).build();

        ConfigurationNode rootNode;

        rootNode = loader.load();


        loadConfFromNode(rootNode);

        logServ.info("重新载入配置文件毕。");
    }

    @Override
    public Conf getConf() {
        return conf;
    }

}
