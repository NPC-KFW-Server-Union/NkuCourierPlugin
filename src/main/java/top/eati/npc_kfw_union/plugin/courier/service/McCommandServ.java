package top.eati.npc_kfw_union.plugin.courier.service;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.eati.npc_kfw_union.plugin.courier.SpongeBeans;

import java.util.UUID;
import java.util.function.Consumer;

@Service
public class McCommandServ {

    private final SpongeBeans spongeBeans;
    private final McAndMiraiCourierServ mcAndMiraiCourierServ;

    private final ILogServ logServ;

    @Autowired
    public McCommandServ(SpongeBeans spongeBeans, McAndMiraiCourierServ mcAndMiraiCourierServ, ILogServ logServ) {
        this.spongeBeans = spongeBeans;
        this.mcAndMiraiCourierServ = mcAndMiraiCourierServ;
        this.logServ = logServ;
    }

    public void regCmds() {
        CommandSpec statusCmd = CommandSpec.builder()
                .executor(new StatusCmd())
                .build();

        CommandSpec restartCmd = CommandSpec.builder()
                .executor(new RestartCmd())
                .build();

        CommandSpec mainCmd = CommandSpec.builder()
                .executor(new StatusCmd())
                .child(statusCmd, "status")
                .child(restartCmd, "restart")
                .build();

        Sponge.getCommandManager().register(
                spongeBeans.getPluginContainer(), mainCmd, "nkucourier");
    }

    private class StatusCmd implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            src.sendMessage(
                    Text.of("NPC&KFW 信使 v0.1\n"
                            + String.format(
                                    "启用： %s\n"
                                            + "工作状态： %s",
                                mcAndMiraiCourierServ.getConf().isDisabled() ? "OFF" : "ON",
                                mcAndMiraiCourierServ.getWorkerStatus() ? "工作中" : "已停止"))
            );
            return CommandResult.success();
        }
    }

    private class RestartCmd implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            src.sendMessage(
                    Text.of("正在重启 McAndMiraiCourierServ...")
            );

            // TODO: 优化异步命令的回报结果的逻辑
            // 避免保持 CommandSource
            // See https://docs.spongepowered.org/7.4.0/en/plugin/practices/bad.html
            UUID playerUUID;
            boolean srcIsPlayer;
            if(src instanceof Player) {
                playerUUID = ((Player) src).getUniqueId();
                srcIsPlayer = true;
            } else {
                playerUUID = null;
                srcIsPlayer = false;
            }
            Consumer<Text> respToCmder = (text) -> {
                if(srcIsPlayer) {
                    Sponge.getServer().getPlayer(playerUUID).ifPresent(cmder -> {
                        cmder.sendMessage(text);
                    });
                // 如果命令源不是玩家，则要么是服务器后台，要么是命令方块，这种情况视作
                // 不会非法。
                } else {
                    src.sendMessage(text);
                }
            };



            Task.builder().execute(() -> {
                try {
                    mcAndMiraiCourierServ.stop();
                    mcAndMiraiCourierServ.start();
                } catch (RuntimeException e) {
                    logServ.err(e);
                    respToCmder.accept(
                            Text.builder("重启失败！： " + e)
                                .color(TextColors.RED)
                                .build());
                }

                respToCmder.accept(
                        Text.of("重启完毕！")
                );

            }).submit(spongeBeans.getPluginContainer());

            return CommandResult.success();
        }
    }

}
