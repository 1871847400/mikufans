package pers.tgl.mikufans.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 去除小数部分
 */
public class IntegerSerializer extends JsonSerializer<Number> {
    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.intValue());
    }
}