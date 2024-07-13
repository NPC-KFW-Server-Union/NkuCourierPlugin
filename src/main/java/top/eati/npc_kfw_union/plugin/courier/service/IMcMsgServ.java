package top.eati.npc_kfw_union.plugin.courier.service;

import org.spongepowered.api.text.Text;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;

/**
 * 我的世界消息服務。
 * <p>
 * 負責發送廣播消息，和監聽我的世界中玩家實體發出的聊天。
 */
public interface IMcMsgServ {
	interface McChatEventListener {
		void onChat(McPlayerChatEvent chat);
	}
	void broadcastMsgInServer(Text text);
	void regChatEventListener(McChatEventListener listener);
	void deRegChatEventListener(McChatEventListener listener);
}
