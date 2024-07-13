package top.eati.npc_kfw_union.plugin.courier.service;

import org.slf4j.Logger;

/**
 * 用於游戲運行時的日志服務。
 */
public class FormalLogServ implements ILogServ {
    private final Logger spongeLogger;

    public FormalLogServ(Logger spongeLogger) {
        this.spongeLogger = spongeLogger;
    }

    public void info(String info) {
        spongeLogger.info(info);
    }

    public void err(String info) {
        spongeLogger.error(info);
    }

    @Override
    public void err(String info, Throwable obj) {
        spongeLogger.error(info, obj);
    }

    @Override
    public void err(Throwable obj) {
        spongeLogger.error("", obj);
    }

    @Override
    public void err(String format, Object... args) {
        spongeLogger.error(format, args);
    }

    public void warn(String info) {
        spongeLogger.warn(info);
    }
}
