package top.eati.npc_kfw_union.plugin.courier.service;

import io.github.xiaoyi311.entity.message.MessageChain;
import io.github.xiaoyi311.event.GroupMessageEvent;
import org.spongepowered.api.text.Text;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;

import java.util.List;


public interface IMcAndMiraiMsgConvertServ {
	List<MessageChain> mcChatEvent2MiraiMsgChain(McPlayerChatEvent event);
	Text miraiMsgEvent2McText(GroupMessageEvent event);
}
