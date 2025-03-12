package pers.tgl.mikufans.jackson;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import lombok.RequiredArgsConstructor;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.service.SysExtDictService;
import pers.tgl.mikufans.util.SecurityUtils;

import java.io.IOException;
import java.util.List;

public class SensitiveFieldProcessModule extends Module {
    @Override
    public String getModuleName() {
        return "sensitive-field-process-module";
    }

    @Override
    public Version version() {
        return VersionUtil.parseVersion("0.0.1", "pers.tgl", getModuleName());
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new SensitiveFieldModifier());
    }

    /**
     * 会在服务器启动时对每个字段进行分配序列化器,并非请求中,拿不到user数据
     */
    private static class SensitiveFieldModifier extends BeanSerializerModifier {
        /**
         * 从beanProperties删除对应字段的writer,则不会将字段序列化
         */
        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            for (BeanPropertyWriter writer : beanProperties) {
                Sensitive sensitive = writer.getAnnotation(Sensitive.class);
                if (sensitive != null) {
                    writer.assignSerializer(new SensitiveJsonSerializer(sensitive));
                }
            }
            return super.changeProperties(config, beanDesc, beanProperties);
        }
    }

    /**
     * 序列化器,必须调用写入方法,不能什么都不做,否则报错
     */
    @RequiredArgsConstructor
    private static class SensitiveJsonSerializer extends JsonSerializer<Object> {
        private final Sensitive sensitive;
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            UserToken userToken = SecurityUtils.getContextUserToken();
            if (sensitive.skipIfAdmin() && userToken != null && userToken.getUserType() == 1) {
                gen.writePOJO(value);
                return;
            }
            switch (sensitive.value()) {
                case TERM:
                    SysExtDictService extDictService = SpringUtil.getBean(SysExtDictService.class);
                    String filterContent = extDictService.filterSensitiveWord(value.toString(), '*');
                    gen.writeString(filterContent);
                    break;
                case CLEAR_TO_NULL:
                    gen.writeNull();
                    break;
                case PASSWORD:
                    gen.writeString("******");
                    break;
                default:
                    gen.writeString(DesensitizedUtil.desensitized(value.toString(),
                            DesensitizedUtil.DesensitizedType.valueOf(sensitive.value().name())));
                    break;
            }
        }
    }
}