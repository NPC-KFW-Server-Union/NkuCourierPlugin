package top.eati.npc_kfw_union.plugin.courier.service;

import io.github.xiaoyi311.entity.message.MessageChain;
import io.github.xiaoyi311.entity.message.Plain;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;

import java.util.Arrays;
import java.util.List;

public class TestMcAndMiraiMsgConvertServ extends FormalMcAndMiraiMsgConvertServ {
	@Override
	public List<MessageChain> mcChatEvent2MiraiMsgChain(McPlayerChatEvent event) {
		return Arrays.asList(
				new Plain(event.getPlayerName() + ":\n"),
				new Plain(event.getText().getChildren().get(1).toString())
		);
	}
}
