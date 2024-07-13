package top.eati.npc_kfw_union.plugin.courier.excption;

public class LoadConfFormFileFailed extends Exception {

	public LoadConfFormFileFailed() {
	}

	public LoadConfFormFileFailed(String message) {
		super(message);
	}

	public LoadConfFormFileFailed(String message, Throwable cause) {
		super(message, cause);
	}

	public LoadConfFormFileFailed(Throwable cause) {
		super(cause);
	}

	public LoadConfFormFileFailed(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
