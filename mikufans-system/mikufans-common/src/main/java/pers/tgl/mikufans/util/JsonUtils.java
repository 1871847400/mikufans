package pers.tgl.mikufans.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

@Slf4j
public class JsonUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 忽略反序列化时不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);//值为null的字段不参与序列化
    }

    @Nullable
    public static <T> T readPath(String json, TypeReference<T> type, String ...paths) {
        try {
            JsonNode node = objectMapper.readTree(json);
            for (String path : paths) {
                if (node.hasNonNull(path)) {
                    node = node.get(path);
                } else {
                    return null;
                }
            }
            return objectMapper.readValue(node.toString(), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Nullable
    public static LinkedHashMap<String, Object> readMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> List<T> readList(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static JsonNode readTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            log.error("err: {} json: {}", e.getMessage(), json);
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T read(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T read(URL url, Class<T> type) {
        try {
            return objectMapper.readValue(url, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T read(InputStream is, Class<T> type) {
        try {
            return objectMapper.readValue(is, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T read(byte[] data, Class<T> type) {
        try {
            return objectMapper.readValue(data, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 注意只能序列化基本类
     * 序列化例如含有InputStream接口的会异常
     * @param obj
     * @return
     */
    public static String writeString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("err: {} obj: {}", e.getMessage(), obj);
            e.printStackTrace();
            return null;
        }
    }
}