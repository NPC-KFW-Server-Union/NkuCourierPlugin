package top.eati.npc_kfw_union.plugin.courier.service;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 用於游戲運行時的我的世界消息服務。
 */
public class FormalMcMsgServ implements IMcMsgServ {
	private final List<McChatEventListener> mcChatEventListeners = new ArrayList<>();

	@Override
	public void broadcastMsgInServer(Text text) {
		Sponge.getServer().getBroadcastChannel().send(text);
	}

	/**
	 * 玩家消息響應函數。
	 * <p>
	 * 該函數應由插件主類的 MessageChannelEvent.Chat 偵聽器調用。
	 */
	public void onMcChatEvent(McPlayerChatEvent event) {
		for(McChatEventListener listener : mcChatEventListeners) {
			listener.onChat(event);
		}
	}

	@Override
	public void regChatEventListener(McChatEventListener listener) {
		mcChatEventListeners.add(listener);
	}

	@Override
	public void deRegChatEventListener(McChatEventListener listener) {
		mcChatEventListeners.remove(listener);
	}
}
