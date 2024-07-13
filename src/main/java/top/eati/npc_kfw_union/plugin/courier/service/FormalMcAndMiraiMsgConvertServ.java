package top.eati.npc_kfw_union.plugin.courier.service;

import io.github.xiaoyi311.entity.message.MessageChain;
import io.github.xiaoyi311.entity.message.Plain;
import io.github.xiaoyi311.event.GroupMessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;

import java.util.Arrays;
import java.util.List;

public class FormalMcAndMiraiMsgConvertServ implements IMcAndMiraiMsgConvertServ {

    public List<MessageChain> mcChatEvent2MiraiMsgChain(McPlayerChatEvent event) {
        return Arrays.asList(
                new Plain(event.getPlayerName() + ":\n"),
                new Plain(event.getText().getChildren().get(1).toPlain())
        );
    }

    public Text miraiMsgEvent2McText(GroupMessageEvent event) {
        Text.Builder builder = Text.builder()
                .append(
                        Text
                                .builder(String.format("[%s] ", event.sender.memberName))
                                .color(TextColors.YELLOW).build());

		if(MessageChain.toMiraiString(event.messages).isEmpty()) {
            builder
                    .append(Text.builder(
                            "（非文字消息）"
                    ).color(TextColors.WHITE).build());
        }else {
            builder
                    .append(Text.builder(
                            MessageChain.toMiraiString(event.messages)
                    ).color(TextColors.WHITE).build());
        }

        return builder.build();
    }
}
