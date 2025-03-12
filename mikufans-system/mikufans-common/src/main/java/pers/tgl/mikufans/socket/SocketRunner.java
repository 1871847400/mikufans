package pers.tgl.mikufans.socket;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class SocketRunner implements ApplicationRunner, DisposableBean {
    @Resource
    private SocketIOServer socketIOServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        socketIOServer.getConfiguration().setJsonSupport(new CustomJsonSupport());
        socketIOServer.start();
        log.info("已启动 socketIOServer");
    }

    @Override
    public void destroy() throws Exception {
        socketIOServer.stop();
        log.info("关闭 socketIOServer");
    }
}