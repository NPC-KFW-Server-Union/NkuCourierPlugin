package top.eati.npc_kfw_union.plugin.courier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import top.eati.npc_kfw_union.plugin.courier.service.*;

@Component
@ComponentScan
@Configuration
public class TestSpringConf {

	@Bean
	public IConfServ confServ() {
		return new TestConfServ();
	}

	@Bean
	public ILogServ logServ() {
		return new TestLogServ();
	}

	@Bean
	public IMcMsgServ mcMsgServ(ILogServ logServ) {
		return new TestMcMsgServ(logServ);
	}

	@Bean
	public IMcAndMiraiMsgConvertServ mcAndMiraiMsgConvertServ() {
		return new TestMcAndMiraiMsgConvertServ();
	}
}
