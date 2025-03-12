package pers.tgl.mikufans.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 自定义Long的序列化方式,使用@JsonSerialize先于全局配置
 */
public class LongSerializer extends JsonSerializer<Long> {
    private final boolean writeNumber;

    public LongSerializer() {
        this(true);
    }

    public LongSerializer(boolean writeNumber) {
        this.writeNumber = writeNumber;
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            if (writeNumber) {
                gen.writeNumber(value);
            } else {
                gen.writeString(value.toString());
            }
        }
    }
}