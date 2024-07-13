package top.eati.npc_kfw_union.plugin.courier.service;

import top.eati.npc_kfw_union.plugin.courier.entity.Conf;

import static top.eati.npc_kfw_union.plugin.courier.entity.Conf.Builder.aConf;

/**
 * 用於 JUnit 測試的配置服務。
 */
public class TestConfServ implements IConfServ {
	@Override
	public Conf getConf() {
		return aConf()
				.withMiraiHttpServerUrl("http://example.com")
				.withMiraiHttpServerToken("asdf")
				.withQqBotId(123L)
				.withQqGroupId(321L)
				.build();
	}

}
