package top.eati.npc_kfw_union.plugin.courier.service;

/**
 * 日志服務。
 * <p>
 * 用於輸出日志。
 */
public interface ILogServ {
    void info(String info);
    void err(String info);
    void err(String info, Throwable excp);
    void err(Throwable excp);
    void err(String format, Object... args);
    void warn(String info);
}
