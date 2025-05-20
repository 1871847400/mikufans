package pers.tgl.mikufans.socket;

import org.springframework.stereotype.Component;

/**
 * websocket实现弹幕实时同步
 */
@Component
public class DanmuSocketListener implements SocketListener {
    @Override
    public String getNamespace() {
        return "/danmu";
    }

    @Override
    public boolean forceLogin() {
        return false;
    }
}
