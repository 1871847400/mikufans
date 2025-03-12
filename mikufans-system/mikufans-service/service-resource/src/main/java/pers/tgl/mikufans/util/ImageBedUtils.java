package pers.tgl.mikufans.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 图床工具
 */
@Deprecated
public class ImageBedUtils {
    private static JsonNode uploadImageBed(File file) throws IOException {
        UrlBuilder urlBuilder = UrlBuilder.ofHttp("https://imgloc.com/api/1/upload");
        HttpRequest request = new HttpRequest(urlBuilder);
        request.method(Method.POST);
        request.header("X-API-Key", "chv_nVzy_7bf1209c125b52672e423c72xxxxxxxxxxxxxx");
        request.form("source", Base64Utils.encodeToString(FileUtil.readBytes(file)));
        request.form("title", UUID.fastUUID());
        HttpResponse response = request.execute();
        if (response.isOk()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response.bodyStream()).get("image");
        }
        throw new FileNotFoundException("上传图床失败了");
    }

    private static String uploadImageBed2(File file, String md5) throws Exception {
        String filename = md5 + "." + FileUtil.getSuffix(file);
        UrlBuilder urlBuilder = UrlBuilder.of("https://upload.hd-r.cn/token")
                .addQuery("name", filename);
        HttpRequest request = new HttpRequest(urlBuilder);
        request.method(Method.GET);
        HttpResponse response = request.execute();
        if (!response.isOk()) {
            throw new Exception("");
        }
        JSONObject result = new JSONObject(response.body());
        if (result.optInt("Code") != 404) {
            throw new Exception("");
        }
        String token = result.optString("Token");
        String time = result.optString("Time");
        urlBuilder = UrlBuilder.of("https://u.hd-r.cn")
                .addQuery("name", filename)
                .addQuery("time", time)
                .addQuery("token", token);
        request = new HttpRequest(urlBuilder);
        response = request
                .method(Method.PUT)
                .body(FileUtil.readBytes(file))
                .execute();

        if (!response.isOk()) {
            throw new Exception("");
        }
        result = new JSONObject(response.body());
        if (result.optInt("code") != 200) {
            throw new Exception(result.toString());
        }
        return "https://i.hd-r.cn/" + result.optString("name");
    }
}
