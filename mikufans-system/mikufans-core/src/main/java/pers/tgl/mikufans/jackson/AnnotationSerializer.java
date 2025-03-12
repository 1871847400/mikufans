package pers.tgl.mikufans.jackson;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class AnnotationSerializer extends JsonSerializer<Annotation> {
    private final ObjectMapper objectMapper;

    @Override
    public void serialize(Annotation value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writePOJO(getAnnotationAttributes(value));
    }
    /**
     * 获取注解所有参数
     * 参考：hutool AnnotationUtil.getAnnotationValueMap()
     */
    private Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        final Method[] methods = ReflectUtil.getMethods(annotation.annotationType(), t -> {
            if (ArrayUtil.isEmpty(t.getParameterTypes())) {
                // 只读取无参方法
                final String name = t.getName();
                // 跳过自有的几个方法
                return (false == "hashCode".equals(name)) //
                        && (false == "toString".equals(name)) //
                        && (false == "annotationType".equals(name));
            }
            return false;
        });
        final HashMap<String, Object> result = new HashMap<>(methods.length, 1);
        for (Method method : methods) {
            boolean ignore = false;
            Annotation[] annotations = method.getAnnotations();
            for (Annotation anno : annotations) {
                if (anno instanceof JsonIgnore) {
                    if (((JsonIgnore) anno).value()) {
                        ignore = true;
                    }
                }
            }
            if (!ignore) {
                result.put(method.getName(), ReflectUtil.invoke(annotation, method));
            }
        }
        return result;
    }
}
