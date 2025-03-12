package pers.tgl.mikufans.socket;

import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.Module;

/**
 * 自定义socket序列化
 */
//@Component
public class CustomJsonSupport extends JacksonJsonSupport {
    public CustomJsonSupport(Module... modules) {
        super(modules);
//        objectMapper.registerModule(new SensitiveFieldProcessModule());
//        objectMapper.registerModule(new SimpleModule().addSerializer(Long.class, new LongSerializer(false)));
    }
}