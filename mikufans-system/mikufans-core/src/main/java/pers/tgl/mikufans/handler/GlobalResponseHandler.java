package pers.tgl.mikufans.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import pers.tgl.mikufans.util.R;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 统一封装controller返回的结果,包括GlobalExceptionHandler
 *  如果controller直接response.write()则不会执行到这里
 *
 *  controller返回byte[]时会出现cast异常
 *  如果方法的返回值是void则不会到这里
 */
@RestControllerAdvice
@Slf4j
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = returnType.getMethod();
        return method != null && !method.getReturnType().isInstance(R.class);
    }

    /**
     * @param body controller返回的内容 void=null
     * @param returnType
     * @param selectedContentType 根据controller返回值判断,返回对象则是application/json,返回字符串则是text/plain
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,  ServerHttpResponse response) {
        if (body instanceof R) {
            return body;
        }
        /**
         * 在controller中返回string时，这里也必须返回string
         * 否则会出现类型cast异常
         */
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(R.ok(body));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.error("json write failed: {}", body);
            }
        }
        return R.ok(body);
    }
}