package pers.tgl.mikufans.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 对所有注册的组件进行处理
 */
@Component
public class SocketListenerRegister implements BeanPostProcessor {
    @Resource
    private SocketIOServer socketIOServer;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SocketListener) {
            SocketListener socketListener = (SocketListener) bean;
            SocketIONamespace socketIONamespace = socketIOServer.addNamespace(socketListener.getNamespace());
            socketIONamespace.addConnectListener(socketListener);
            socketIONamespace.addDisconnectListener(socketListener);
            socketIONamespace.addAuthTokenListener(socketListener);
        }
        return bean;
    }
}