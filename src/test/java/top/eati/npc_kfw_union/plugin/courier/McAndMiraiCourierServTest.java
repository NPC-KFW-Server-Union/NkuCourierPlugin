package top.eati.npc_kfw_union.plugin.courier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.spongepowered.api.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import top.eati.npc_kfw_union.plugin.courier.entity.McPlayerChatEvent;
import top.eati.npc_kfw_union.plugin.courier.service.McAndMiraiCourierServ;
import top.eati.npc_kfw_union.plugin.courier.service.TestMcMsgServ;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig(TestSpringConf.class)
public class McAndMiraiCourierServTest {

	@Autowired
	McAndMiraiCourierServ mcAndMiraiCourierServ;

	@Autowired
	TestMcMsgServ mcMsgServ;

	@Test
	public void test() throws InterruptedException {
		mcAndMiraiCourierServ.start();

		Thread.sleep(1000);

		// 模擬我的世界玩家消息
		mcMsgServ.onMcChatEvent(new McPlayerChatEvent("fakeplayer",
				Text.builder("<fakeplayer>")
						.append(Text.of(" "), Text.of("你好"))
						.build()));

		Thread.currentThread().suspend();
	}
}
