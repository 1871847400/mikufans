package pers.tgl.mikufans.util;

import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtils {
    /**
     * @return xx省xx市
     */
    public static String getRealLocation(String ip) {
        if (StrUtil.isBlank(ip)) {
            return "";
        }
        if (Ipv4Util.isInnerIP(ip)) {
            return "本地";
        }
        String url = String.format("https://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true", ip);
        try (HttpResponse response = HttpUtil.createGet(url)
                .execute()) {
            if (response.isOk()) {
                JSONObject result = new JSONObject(response.body());
                //省
                String pro = result.optString("pro", "");
                //市
                String city = result.optString("city", "");
                return pro + city;
            }
        }
        return "";
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "未知";
        }
    }

    public static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "未知";
        }
    }

    /**
     * 获取客户端真实IP地址
     * 代理模式下,需要配置Nginx写入请求头，否则 request.getRemoteAddr() == 127.0.0.1
     * 如果是在localhost的时候 request.getRemoteAddr() == 0:0:0:0:0:0:0:1
     */
    public static String getIpaddr(HttpServletRequest request) {
        String result = request.getHeader("x-real-ip");
        if (result == null) {
            result = request.getHeader("x-forwarded-for");
            if (result == null) {
                result = request.getRemoteAddr();
            }
        }
        return "0:0:0:0:0:0:0:1".equals(result) ? "127.0.0.1" : result;
    }
}