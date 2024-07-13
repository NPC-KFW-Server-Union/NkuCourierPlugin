package top.eati.npc_kfw_union.plugin.courier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用於 JUnit 測試的日志服務。
 */
public class TestLogServ implements ILogServ {
	private final Logger logger = LoggerFactory.getLogger("NKU-MC2Mirai");
	@Override
	public void info(String info) {
		logger.info(info);
	}

	@Override
	public void err(String info) {
		logger.error(info);
	}

	@Override
	public void err(String info, Throwable obj) {
		logger.error(info, obj);
	}

	@Override
	public void err(Throwable obj) {
		logger.error("", obj);
	}

	@Override
	public void err(String format, Object... args) {
		logger.error(format, args);
	}

	@Override
	public void warn(String info) {
		logger.warn(info);
	}
}
