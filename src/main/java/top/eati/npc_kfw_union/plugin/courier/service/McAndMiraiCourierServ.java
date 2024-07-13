package top.eati.npc_kfw_union.plugin.courier.service;

import io.github.xiaoyi311.MiraiHttp;
import io.github.xiaoyi311.MiraiHttpConn;
import io.github.xiaoyi311.MiraiHttpMsgFetchingThread;
import io.github.xiaoyi311.err.NetworkIOError;
import io.github.xiaoyi311.err.RobotNotFound;
import io.github.xiaoyi311.err.VerifyKeyError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.eati.npc_kfw_union.plugin.courier.entity.Conf;
import top.eati.npc_kfw_union.plugin.courier.worker.McAndMiraiCourierWorker;

/**
 * 創建與管理 McAndMiraiCourierWorker，並使用之來進行
 * 我的世界 -> Mirai 的聊天的雙向通訊。
 */
@Service
public class McAndMiraiCourierServ {

	private final IConfServ confServ;
	private final IMcAndMiraiMsgConvertServ mcAndMiraiMsgConvertServ;
	private final IMcMsgServ mcMsgServ;
	private final ILogServ logServ;

	private McAndMiraiCourierWorker mcAndMiraiCourierWorker;
	private MiraiHttpConn miraiHttpConn;

	private Conf conf;

	public Conf getConf() {
		return conf;
	}

	@Autowired
	public McAndMiraiCourierServ(IConfServ confServ, IMcAndMiraiMsgConvertServ mcAndMiraiMsgConvertServ, IMcMsgServ mcMsgServ, ILogServ logServ) {
		this.confServ = confServ;
		this.mcAndMiraiMsgConvertServ = mcAndMiraiMsgConvertServ;
		this.mcMsgServ = mcMsgServ;
		this.logServ = logServ;
	}

	/**
	 * 開始消息的同步
	 */
	public void start() {
		conf = confServ.getConf().clone();
		if(conf.isDisabled()) {
			logServ.info("McAndMiraiCourierServ::start: 什么也不做。因为插件已被禁用。");
			return;
		}

		logServ.info("McAndMiraiCourierServ::start: 信息同步启动中……。");

		try {
			miraiHttpConn = MiraiHttp.createConn(
					conf.getMiraiHttpServerToken(),
					conf.getMiraiHttpServerUrl(),
					conf.getQqBotId(),
					MiraiHttpMsgFetchingThread.NetworkErrorStrategy.CONTINUE,
					MiraiHttpMsgFetchingThread.SessionOutDateErrorStrategy.REFRESH
			);
		} catch (VerifyKeyError | RobotNotFound | NetworkIOError e) {
			throw new RuntimeException(e);
		}

		mcAndMiraiCourierWorker = new McAndMiraiCourierWorker(
				miraiHttpConn,
				conf,
				mcAndMiraiMsgConvertServ,
				logServ, mcMsgServ
		);

		mcAndMiraiCourierWorker.start();

		logServ.info("McAndMiraiCourierServ::start: 信息同步已启动！。");
	}

	/**
	 * 停止消息的同步
	 */
	public void stop() {
		if(conf.isDisabled()) {
			logServ.info("McAndMiraiCourierServ::stop: 什么也不做。因为插件已被禁用。");
			return;
		}

		if (mcAndMiraiCourierWorker == null) {
			logServ.info("McAndMiraiCourierServ::stop: 什么也不做。因为信息同步已停止。");
			return;
		}


		logServ.info("McAndMiraiCourierServ::start: 信息同步停止中……。");
		mcAndMiraiCourierWorker.stop();
		mcAndMiraiCourierWorker = null;
		try {
			miraiHttpConn.unbind();
		} catch (NetworkIOError e) {
			throw new RuntimeException(e);
		}
		logServ.info("McAndMiraiCourierServ::start: 信息同步已停止！。");
	}

	public boolean getWorkerStatus() {
		return mcAndMiraiCourierWorker.getIsWorking();
	}
}
