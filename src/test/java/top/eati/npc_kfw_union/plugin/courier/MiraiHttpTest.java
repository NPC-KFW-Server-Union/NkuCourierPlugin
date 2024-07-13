package top.eati.npc_kfw_union.plugin.courier;

import io.github.xiaoyi311.MiraiHttp;
import io.github.xiaoyi311.MiraiHttpConn;
import io.github.xiaoyi311.MiraiHttpMsgFetchingThread;
import io.github.xiaoyi311.entity.message.MessageChain;
import io.github.xiaoyi311.entity.message.Plain;
import io.github.xiaoyi311.err.NetworkIOError;
import io.github.xiaoyi311.err.RobotNotFound;
import io.github.xiaoyi311.err.VerifyKeyError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import top.eati.npc_kfw_union.plugin.courier.entity.Conf;
import top.eati.npc_kfw_union.plugin.courier.service.IConfServ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig(TestSpringConf.class)
public class MiraiHttpTest {
    @Autowired
    private IConfServ confServ;

    private Conf conf;

    @BeforeAll
    public void init() throws VerifyKeyError, RobotNotFound {
        conf = confServ.getConf();
    }

//    public static class MyListener implements MiraiEventListener {
//
//        @Override
//        public void onGroupMessage(GroupMessageEvent event) {
//            System.out.println(MessageChain.toMiraiString(event.messages));
//        }
//    }

    private MiraiHttpConn createMiraiHttpConn() throws VerifyKeyError, RobotNotFound {
        MiraiHttpConn session = null;
        try {
            session = MiraiHttp.createConn(
                    conf.getMiraiHttpServerToken(),
                    conf.getMiraiHttpServerUrl(),
                    conf.getQqBotId(),
                    MiraiHttpMsgFetchingThread.NetworkErrorStrategy.CONTINUE,
                    MiraiHttpMsgFetchingThread.SessionOutDateErrorStrategy.REFRESH
            );
        } catch (NetworkIOError e) {
            throw new RuntimeException(e);
        }
        return session;
    }

    @Test
    public void sendMsgToGroup() throws VerifyKeyError, RobotNotFound {
        createMiraiHttpConn().getApi().sendGroupMessage(
                conf.getQqGroupId(),
                new MessageChain[] {
                        new Plain("你好！")
                }
        );
    }

    @Test
    public void recvMsgInGroup() throws VerifyKeyError, RobotNotFound, InterruptedException {
		Semaphore sem = new Semaphore(0);

		MiraiHttpConn conn = createMiraiHttpConn();
        MiraiHttp.registerListener(event -> {
			String miraiStr = MessageChain.toMiraiString(event.messages);

			List<MessageChain> msgChain = new ArrayList<>();
			msgChain.add(new Plain("你發送了："));
			msgChain.addAll(Arrays.asList(event.messages));
			msgChain.add(new Plain("\nMirai 字符串：" + miraiStr));
			msgChain.add(new Plain("\nMirai JSON：" + MessageChain.toJSONObject(event.messages)));

			event.conn.getApi().sendGroupMessage(
					event.sender.group.id,
					msgChain.toArray(new MessageChain[0])
			);

			if(miraiStr.equals("\\bye"))
				sem.release();


		}, conn);
        conn.getApi().sendGroupMessage(
                conf.getQqGroupId(),
                new MessageChain[] {
                        new Plain("MiraiHttpTest\n請發送消息，输入 \\bye 以结束。")
                }
        );

		sem.acquire();

		conn.getApi().sendGroupMessage(
				conf.getQqGroupId(),
				new MessageChain[] {
						new Plain("測試結束。")
				}
		);
    }

//    @Test
//    public void test() throws VerifyKeyError, RobotNotFound {
//        MiraiHttpSession session = MiraiHttp.createSession(
//                "INITKEYlSqMMWHU",
//                "http://36.235.137.142:30776",
//                3951829853L
//        );
//        MiraiHttp.registerListener(new MyListener(), session);
//        session.getApi().sendGroupMessage(
//                881525601L,
//                new MessageChain[] {
//                        new Plain("你好！")
//                }
//        );
//        Thread.currentThread().checkAccess();
//    }
}
