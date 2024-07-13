package top.eati.npc_kfw_union.plugin.courier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import top.eati.npc_kfw_union.plugin.courier.service.*;

@Component
@ComponentScan
@Configuration
public class FormalSpringConf {
    @Autowired
    private SpongeBeans spongeBeans;

    @Bean
    public IConfServ confServ(ILogServ logServ) {
        return new FormalConfServ(
                spongeBeans,
                logServ
        );
    }

    @Bean
    public ILogServ logServ() {
        return new FormalLogServ(spongeBeans.getSpongeLogger());
    }

    @Bean
    public IMcMsgServ mcMsgServ() {
        return new FormalMcMsgServ();
    }

    @Bean
    public IMcAndMiraiMsgConvertServ mcAndMiraiMsgConvertServ() {
        return new FormalMcAndMiraiMsgConvertServ();
    }

}
