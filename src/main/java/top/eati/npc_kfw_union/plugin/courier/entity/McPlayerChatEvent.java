package top.eati.npc_kfw_union.plugin.courier.entity;

import org.spongepowered.api.text.Text;

public class McPlayerChatEvent {
	private String playerName;
	private Text text;

	public McPlayerChatEvent(String playerName, Text text) {
		this.playerName = playerName;
		this.text = text;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "McPlayerChatEvent{" +
				"playerName='" + playerName + '\'' +
				", text=" + text +
				'}';
	}
}
