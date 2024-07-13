package top.eati.npc_kfw_union.plugin.courier.worker;

import io.github.xiaoyi311.MiraiHttp;
import io.github.xiaoyi311.MiraiHttpConn;
import io.github.xiaoyi311.entity.message.MessageChain;
import io.github.xiaoyi311.entity.message.Plain;
import io.github.xiaoyi311.event.GroupMessageEvent;
import io.github.xiaoyi311.event.MiraiEventListener;
import top.eati.npc_kfw_union.plugin.courier.entity.Conf;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;
import top.eati.npc_kfw_union.plugin.courier.service.ILogServ;
import top.eati.npc_kfw_union.plugin.courier.service.IMcAndMiraiMsgConvertServ;
import top.eati.npc_kfw_union.plugin.courier.service.IMcMsgServ;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Managed by McAndMiraiCourierWorkerManServ.
 * <p>
 * Don't new an instance directly.
 */
public class McAndMiraiCourierWorker {
	private final MiraiHttpConn miraiHttpConn;

	private final Conf conf;

	private final IMcAndMiraiMsgConvertServ mcAndMiraiMsgConvertServ;


	private final ILogServ logServ;

	private final IMcMsgServ mcMsgServ;

	private boolean working = false;

	/**
	 * Listener to listen message in qq group.
	 * <p>
	 * This function runs on another thread.
	 */
	private class MiraiListener implements MiraiEventListener {
		@Override
		public void onGroupMessage(GroupMessageEvent groupMessageEvent) {
			if(!groupMessageEvent.sender.group.id.equals(conf.getQqGroupId()))
				return;

			// Send chat message asynchronously is allowed
			// See https://docs.spongepowered.org/7.4.0/nb/plugin/scheduler.html#asynchronous-tasks
			mcMsgServ.broadcastMsgInServer(
					mcAndMiraiMsgConvertServ.miraiMsgEvent2McText(groupMessageEvent)
			);

			// TODO: refactor: 將整段代碼放到一箇新類 QqBotCmdServ 中。
			if(MessageChain.toMiraiString(groupMessageEvent.messages).equals("\\nkucourier")) {
				groupMessageEvent.conn.getApi().sendGroupMessage(
						conf.getQqGroupId(),
						new MessageChain[]{new Plain(
								String.format(
										"NPC&KFW 信使 v0.1\n"
												+ "启用：%s\n"
												+ "工作状态：%s",
										conf.isDisabled() ? "OFF" : "ON",
										"工作中")
						)}
				);
			}
		}
	}

	/**
	 * Listener to 監聽我的世界玩家聊天的信息。
	 */
	private class McChatListener implements IMcMsgServ.McChatEventListener {
		@Override
		public void onChat(McPlayerChatEvent chat) {
			// 將玩家發送的消息餵給 Mirai 信息發送線程。
			miraiMsgPushingWorker.feedMiraiMsg(
					mcAndMiraiMsgConvertServ.mcChatEvent2MiraiMsgChain(chat)
			);
		}

	}

	/**
	 * Mirai 信息發送線程。
	 * <p>
	 * 從 BlockingQueue 中逐一取出獲取待發送的信息，調用 Mirai 發送到 qq 羣。
	 */
	private class MiraiMsgPushingWorker extends Thread {

		private final BlockingQueue<List<MessageChain>> msgsToSend = new ArrayBlockingQueue<>(13);


		public void feedMiraiMsg(List<MessageChain> msg) {
			msgsToSend.add(msg);
		}

		public void shutdown() {
			miraiMsgPushingWorker.interrupt();
		}

		@Override
		public void run() {
			logServ.info("MiraiMsgPushingWorker::run: 线程已启动！");
			while (!miraiMsgPushingWorker.isInterrupted()) {
				try {
					MessageChain[] msgChain = msgsToSend.take().toArray(new MessageChain[0]);
					String msgId = miraiHttpConn.getApi().sendGroupMessage(
							conf.getQqGroupId(), msgChain);
					if(msgId == null)
						logServ.err(
							"Mirai 消息 '{}' 发送失败！",
							MessageChain.toMiraiString(msgChain));
				} catch (InterruptedException e) {
					logServ.info("MiraiMsgPushingWorker::run: 线程已退出！");
				}
			}
			logServ.info("MiraiMsgPushingWorker::run: 线程已退出！");
		}
	}


	private final MiraiListener miraiListener = new MiraiListener();
	private final McChatListener mcChatListener = new McChatListener();
	private final MiraiMsgPushingWorker miraiMsgPushingWorker = new MiraiMsgPushingWorker();

	public McAndMiraiCourierWorker(MiraiHttpConn miraiHttpConn, Conf conf, IMcAndMiraiMsgConvertServ mcAndMiraiMsgConvertServ, ILogServ logServ, IMcMsgServ mcMsgServ) {
		this.miraiHttpConn = miraiHttpConn;
		this.conf = conf;
		this.mcAndMiraiMsgConvertServ = mcAndMiraiMsgConvertServ;
		this.logServ = logServ;
		this.mcMsgServ = mcMsgServ;
	}
	public void start() {
		MiraiHttp.registerListener(miraiListener, miraiHttpConn);
		mcMsgServ.regChatEventListener(mcChatListener);
		miraiMsgPushingWorker.start();
		working = true;
	}
	public void stop() {
		miraiMsgPushingWorker.shutdown();
		mcMsgServ.deRegChatEventListener(mcChatListener);
		MiraiHttp.removeListener(miraiListener, miraiHttpConn);
		working = false;
	}

	public boolean getIsWorking() {
		return working;
	}
}
