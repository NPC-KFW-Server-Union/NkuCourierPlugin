package top.eati.npc_kfw_union.plugin.courier.service;

import org.spongepowered.api.text.Text;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 用於測試的我的世界消息服務。
 */
public class TestMcMsgServ implements IMcMsgServ{
	private final ILogServ logServ;

	private final List<McChatEventListener> mcChatEventListeners = new ArrayList<>();

	public TestMcMsgServ(ILogServ logServ) {
		this.logServ = logServ;
	}

	@Override
	public void broadcastMsgInServer(Text text) {
		logServ.info("我的世界廣播：\n" + text.toString());
	}

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
