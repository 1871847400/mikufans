package pers.tgl.mikufans.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class ServletUtils {
    /**
     * 获取当前线程绑定的servlet请求
     */
    public static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    public static String getParameter(String parameterName) {
        return getRequest().getParameter(parameterName);
    }

    public static String getParameter(String parameterName, String defaultValue) {
        String value = getRequest().getParameter(parameterName);
        return value == null ? defaultValue : value;
    }

    public static boolean getBoolParameter(String parameterName, boolean defaultValue) {
        String value = getParameter(parameterName, defaultValue + "");
        return Boolean.parseBoolean(value);
    }

    public static void setParameter(String parameterName, String[] value) {
        getRequest().getParameterMap().put(parameterName, value);
    }

    public static Object getAttribute(String attributeName, Object defaultValue) {
        return Optional.ofNullable(getRequest().getAttribute(attributeName)).orElse(defaultValue);
    }

    public static void setAttribute(String attributeName, Object value) {
        getRequest().setAttribute(attributeName, value);
    }
}