package pers.tgl.mikufans.socket;

import org.springframework.stereotype.Component;

@Component
public class MsgSocketListener implements SocketListener {
    @Override
    public String getNamespace() {
        return "/msg";
    }

    @Override
    public boolean forceLogin() {
        return true;
    }
}