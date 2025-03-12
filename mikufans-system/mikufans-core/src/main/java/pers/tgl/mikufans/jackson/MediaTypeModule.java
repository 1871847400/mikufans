package pers.tgl.mikufans.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.MediaType;

import java.io.IOException;

public class MediaTypeModule extends SimpleModule {
    public MediaTypeModule() {
        super();
        addSerializer(MediaType.class, new JsonSerializer<MediaType>() {
            @Override
            public void serialize(MediaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.getType() + "/" + value.getSubtype());
            }
        });
        addDeserializer(MediaType.class, new JsonDeserializer<MediaType>() {
            @Override
            public MediaType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                return MediaType.parseMediaType(p.getText());
            }
        });
    }
}