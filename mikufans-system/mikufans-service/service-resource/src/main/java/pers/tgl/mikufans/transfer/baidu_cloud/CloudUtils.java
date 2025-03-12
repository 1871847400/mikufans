package pers.tgl.mikufans.transfer.baidu_cloud;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSize;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.service.SysParamService;
import pers.tgl.mikufans.transfer.ResourceTransfer;
import pers.tgl.mikufans.util.FileComparator;
import pers.tgl.mikufans.util.JsonUtils;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.util.RedisUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网盘工具类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CloudUtils implements ResourceTransfer {
    //网盘单文件最大大小
    private static final long PER_FILE_MAX_SIZE = DataSize.ofMegabytes(4).toBytes() - 1024;

    //记录token过期时间
    private static final String PARAM_ACCESS_TOKEN_EXPIRE_KEY = "baidu_openapi_access_token_expire";
    //刷新token(90天有效期)
    private static final String PARAM_REFRESH_TOKEN_KEY = "baidu_openapi_refresh_token";
    //访问token(30天有效期)
    private static final String PARAM_ACCESS_TOKEN_KEY = "baidu_openapi_access_token";
    //您应用的AppKey
    private static final String PARAM_APP_KEY = "baidu_openapi_appkey";
    //您应用的SecretKey
    private static final String PARAM_APP_SECRET = "baidu_openapi_secretkey";

    private final SysParamService sysParamService;
    private final RedisUtils redisUtils;

    /**
     * 网盘OpenAPI设置
     */
    //访问token(确认身份)
    private String accessToken;

    //用户id
    private String uk;
    //分享id(区分分享文件夹)
    private String shareid;
    private String cookie;

    @PostConstruct
    public void init() {
        sysParamService.watch(PARAM_ACCESS_TOKEN_KEY, value->accessToken=value);
        sysParamService.watch("baidu_session_uk", value->uk=value);
        sysParamService.watch("baidu_session_shareid", value->shareid=value);
        sysParamService.watch("baidu_session_cookie", value->cookie=value);
    }

    /**
     * 上传文件到网盘
     * @param file 要上传的文件
     * @param savePath 文件的存储位置，例如：/shares/files/readme.md
     */
    public void upload(File file, final String savePath) throws IOException {
        //将文件转为base64
        byte[] base64 = Base64.encode(file).getBytes(StandardCharsets.UTF_8);
        //如果文件转换base64后超过限制,则将文件分为多个数据块
        byte[][] chunks = ArrayUtil.split(base64, (int) PER_FILE_MAX_SIZE);
        //块的起始序号
        int index = 1;
        //遍历每个数据块
        for (byte[] bytes : chunks) {
            String filepath = savePath + ".txt";
            if (chunks.length > 1) {
                //将下标作为文件名的一部分,合并时需要按照顺序合并
                filepath = savePath + "." + (index++) + ".txt";
            }
            //上传数据的md5值
            String blockList = "[\"" + DigestUtils.md5DigestAsHex(bytes) + "\"]";
            //预创建文件,并获取上传任务的id
            String uploadId = precreate(accessToken, filepath, bytes.length, blockList);
            //上传分片
            uploadChunk(accessToken, filepath, bytes, uploadId);
            //最后合并文件
            mergeChunks(accessToken, filepath, bytes.length, blockList, uploadId);
        }
    }

    /**
     * 网盘预上传文件
     */
    private String precreate(String accessToken, String filepath, int fileSize, String blockList) throws IOException {
        String url = UrlBuilder.of("http://pan.baidu.com/rest/2.0/xpan/file")
                .addQuery("method", "precreate")
                .addQuery("access_token", accessToken)
                .build();
        HttpRequest request = HttpUtil.createPost(url)
                .form("path", filepath)
                .form("size", fileSize)
                .form("isdir", 0)
                .form("block_list", blockList)
                .form("autoinit", 1)
                .form("rtype", 1);
        HttpResponse response = request.execute();
        if (isOK(response)) {
            JSONObject body = new JSONObject(response.body());
            return body.optString("uploadid", null);
        } else {
            log.error("网盘预创建文件失败 response={}", response.body());
            throw new IOException("预创建文件失败");
        }
    }
    /**
     * 分片上传文件
     */
    private void uploadChunk(String accessToken, String filepath, byte[] chunk, String uploadId) throws IOException {
        String url = UrlBuilder.of("https://d.pcs.baidu.com/rest/2.0/pcs/superfile2")
                .addQuery("method", "upload")
                .addQuery("access_token", accessToken)
                .addQuery("type", "tmpfile")
                .addQuery("path", filepath)
                .addQuery("uploadid", uploadId)
                .addQuery("partseq", 0)
                .build();
        HttpResponse response = HttpUtil.createPost(url)
                .contentType(ContentType.MULTIPART.getValue())
                .form("file", chunk, "file.txt")
                .execute();
        if (!isOK(response)) {
            log.error("网盘上传分片失败 response={}", response.body());
            throw new IOException("上传分片失败");
        }
    }
    /**
     * 网盘合并文件
     */
    private void mergeChunks(String accessToken, String filepath, int fileSize, String blockList, String uploadId) throws IOException {
        String url = UrlBuilder.of("https://pan.baidu.com/rest/2.0/xpan/file")
                .addQuery("method", "create")
                .addQuery("access_token", accessToken)
                .build();
        HttpRequest request = HttpUtil.createPost(url)
                .form("path", filepath)
                .form("size", fileSize)
                .form("isdir", "0")
                .form("block_list", blockList)
                .form("uploadid", uploadId);
        HttpResponse response = request.execute();
        if (!isOK(response)) {
            log.error("网盘合并分片失败 response={}", response.body());
            throw new IOException("合并分片失败");
        }
    }

    private static final String REDIS_CACHE_LINK_KEY = "cloud-file-link:";
    //匹配  文件名.数字(可能不存在).txt => 文件名
    private static final Pattern pattern = Pattern.compile("^(.+?)(?:\\.\\d+)?\\.txt$");

    /**
     * 获取下载链接
     * @param filepath 完整的文件路径,例如：/a/b/c/readme.md
     */
    @Nullable
    @SuppressWarnings("all")
    public List<String> getDownloadLinks(String filepath) {
        String dir = MyUtils.getParentUri(filepath);
        if (!redisUtils.exists(REDIS_CACHE_LINK_KEY + dir)) {
            cacheDir(dir);
        }
        return redisUtils.getObject(REDIS_CACHE_LINK_KEY + filepath, List.class);
    }

    /**
     * 缓存指定文件夹下的所有txt文件的下载链接(注意链接有效期1天)
     * @param dir 必须是完整的文件夹路径 /xx1/xx2
     */
    private void cacheDir(final String dir) {
        //dir下的所有文件(非递归)
        List<CloudShareListFile> listFiles = new ArrayList<>(100);
        //遍历目标路径下所有文件,直到不存在的页码
        for (int page = 1; page <= 100; page++) {
            UrlBuilder urlBuilder = UrlBuilder
                    .of("https://pan.baidu.com/share/list")
                    .addQuery("uk", uk)
                    .addQuery("shareid", shareid)
                    .addQuery("order", "server_filename") //默认other
                    .addQuery("desc", 0) //0升序1降序 都是降序
                    .addQuery("page", page) //访问不存在的page不会报错只返回空集合
                    .addQuery("num", 100) //默认每页最多100条数据
                    .addQuery("dir", dir)
                    .addQuery("app_id", "250528");
            HttpResponse response = HttpRequest.of(urlBuilder)
                    .cookie(cookie)
                    .header(Header.HOST, "pan.baidu.com")
                    .execute();
            if (!isOK(response)) {
                log.error("访问网盘文件列表失败 {}", response.body());
                throw new CustomException(Code.SERVER_ERROR);
            }
            JSONObject result = new JSONObject(response.body());
            //分片文件列表
            JSONArray list = result.optJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                CloudShareListFile file = JsonUtils.read(list.getJSONObject(i).toString(), CloudShareListFile.class);
                if (file != null && file.getIsdir() == 0 && file.getServer_filename().endsWith(".txt")) {
                    listFiles.add(file);
                }
            }
            //数据量不足,说明没有下一页
            if (list.length() < 100) {
                break;
            }
        }
        //文件路径 : 下载url的链接列表(一个ts切片可能又由多个文件组成)
        Map<String, List<String>> links = new LinkedHashMap<>(listFiles.size());
        // a.jpg.txt  b.jpg.1.txt b.jpg.2.txt
        //由于服务器返回的list顺序是乱的,这里需要先根据文件名进行排序
        listFiles.sort(new FileComparator<>(CloudShareListFile::getServer_filename));
        //遍历排过序的文件列表
        for (CloudShareListFile file : listFiles) {
            Matcher matcher = pattern.matcher(file.getServer_filename());
            if (!matcher.find()) {
                log.error("无法匹配的文件名 path={}", file.getPath());
                continue;
            }
            //真实文件名
            String filename = matcher.group(1);
            String key = MyUtils.getParentUri(file.getPath()) + "/" + filename;
            if (!links.containsKey(key)) {
                links.put(key, new ArrayList<>(0));
            }
            links.get(key).add(file.getPicdocpreview());
        }
        for (Map.Entry<String, List<String>> e : links.entrySet()) {
            String redisKey = REDIS_CACHE_LINK_KEY + e.getKey();
            redisUtils.set(redisKey, e.getValue(), Duration.ofDays(1));
        }
        redisUtils.set(REDIS_CACHE_LINK_KEY + dir, links.size(), Duration.ofDays(1));
    }

    /**
     * 删除网盘中的资源，非空目录也会被删除
     * @param paths 完整路径
     */
    public void delete(String... paths) throws IOException {
        if (ArrayUtil.isEmpty(paths)) {
            return;
        }
        StringBuilder sb = new StringBuilder("[");
        for (String path : paths) {
            sb.append("\"").append(path).append("\"").append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        UrlBuilder urlBuilder = UrlBuilder
                .of("https://pan.baidu.com/rest/2.0/xpan/file?method=filemanager")
                .addQuery("access_token", accessToken)
                .addQuery("opera", "delete");
        HttpResponse response = HttpRequest.of(urlBuilder)
                .method(Method.POST)
                .header(Header.HOST, "pan.baidu.com")
                .form("async", 1)
                .form("filelist", sb.toString())
                .execute();
        if (!isOK(response)) {
            log.error("删除网盘文件失败 paths={} response={}", String.join(",", paths), response.body());
            throw new IOException("删除文件失败," + response.body());
        }
    }
    /**
     * 定期检查access_token是否到期，如果快要到期马上刷新
     */
    @Scheduled(initialDelay = 0, fixedDelay = 8, timeUnit = TimeUnit.HOURS)
    public void checkTokenExpire() {
        String expireStr = sysParamService.getString(PARAM_ACCESS_TOKEN_EXPIRE_KEY);
        if (StrUtil.isBlank(expireStr)) {
            if (StrUtil.isNotBlank(accessToken)) {
                refreshAccessToken();
            }
            return;
        }
        Date now = new Date();
        DateTime expire = DateUtil.parse(expireStr);
        if (expire.isAfter(now)) {
            //计算到期时间和今天相差多久,永远是正数
            long time = expire.between(now, DateUnit.HOUR);
            if (time < 12) {
                refreshAccessToken();
            }
        } else {
            //如果当前时间已经超过到期时间,立马刷新token
            refreshAccessToken();
        }
    }
    /**
     * 网盘响应示例
     * {
     *   expires_in: 2592000,           Access Token的有效期，单位为秒。
     *   refresh_token: "...",          用于刷新Access Token, 有效期为10年。
     *   access_token: "...",           获取到的Access Token，Access Token是调用网盘开放API访问用户授权资源的凭证。
     *   scope: "basic netdisk"         Access Token 最终的访问权限，即用户的实际授权列表。
     * }
     */
    private void refreshAccessToken() {
        String refreshToken = sysParamService.getString(PARAM_REFRESH_TOKEN_KEY);
        String appKey = sysParamService.getString(PARAM_APP_KEY);
        String appSecret = sysParamService.getString(PARAM_APP_SECRET);
        Assert.notBlank(refreshToken, "{} can not be blank", PARAM_REFRESH_TOKEN_KEY);
        Assert.notBlank(appKey, "{} can not be blank", PARAM_APP_KEY);
        Assert.notBlank(appSecret, "{} can not be blank", PARAM_APP_SECRET);
        log.info("正在刷新网盘access_token: " + refreshToken);
        HttpRequest request = HttpUtil.createGet("https://openapi.baidu.com/oauth/2.0/token")
                .form("grant_type", "refresh_token")
                .form("refresh_token", refreshToken)
                .form("client_id", appKey)
                .form("client_secret", appSecret);
        HttpResponse response = request.execute();
        if (isOK(response)) {
            JSONObject body = new JSONObject(response.body());
            int duration = body.getInt("expires_in");
            DateTime dateTime = new DateTime();
            dateTime.offset(DateField.SECOND, duration);
            sysParamService.setValue(PARAM_ACCESS_TOKEN_EXPIRE_KEY, dateTime.toString());
            String newAccessToken = body.getString("access_token");
            String newRefreshToken = body.getString("refresh_token");
            sysParamService.setValue(PARAM_ACCESS_TOKEN_KEY, newAccessToken);
            sysParamService.setValue(PARAM_REFRESH_TOKEN_KEY, newRefreshToken);
            this.accessToken = newAccessToken;
            log.info("更新网盘认证Token成功 {}", this.accessToken);
        } else {
            log.error("刷新网盘认证Token失败 {}", response.body());
        }
    }
    /**
     * 判断网盘的响应是否成功
     * {
     * 	"errno": -6,
     * 	"errmsg": "Invalid Bduss",
     * 	"request_id": "8889392513333350895"
     * }
     * 错误码:
     *  0: 成功
     *  -6: token有问题
     *  -3: 文件不存在
     */
    private boolean isOK(HttpResponse response) {
        if (response == null || !response.isOk()) {
            return false;
        }
        JSONObject body = new JSONObject(response.body());
        return body.optInt("errno", 0) == 0;
    }

    /**
     * 获取网盘容量信息
     */
    @Nullable
    public CloudStats getStats() {
        checkTokenExpire();
        UrlBuilder urlBuilder = UrlBuilder
                .of("https://pan.baidu.com/api/quota")
                .addQuery("access_token", accessToken)
                .addQuery("checkfree", 1)
                .addQuery("checkexpire", 1);
        HttpResponse response = HttpRequest.of(urlBuilder)
                .method(Method.GET)
                .header(Header.HOST, "pan.baidu.com")
                .execute();
        if (!isOK(response)) {
            return null;
        }
        return JsonUtils.read(response.body(), CloudStats.class);
    }

    /**
     * 获取指定目录下，包含指定关键字的文件列表。
     * @param dir 搜索目录，默认根目录
     * @param key 搜索关键字，最大30字符（UTF8格式）
     */
    @Nullable
    public CloudSearch search(String dir, String key) {
        UrlBuilder urlBuilder = UrlBuilder
                .of("https://pan.baidu.com/rest/2.0/xpan/file")
                .addQuery("access_token", accessToken)
                .addQuery("method", "search")
                .addQuery("key", key)
                .addQuery("dir", dir);
//                .addQuery("category", 0)
//                .addQuery("page", 1) //页数，从1开始，缺省则返回所有条目
//                .addQuery("num", 500) //默认为500，不能修改
//                .addQuery("recursion", 1); //是否递归，带这个参数就会递归，否则不递归
//                .addQuery("web", 0) //是否展示缩略图信息，带这个参数会返回缩略图信息，否则不展示缩略图信息
        HttpResponse response = HttpRequest.of(urlBuilder)
                .method(Method.GET)
                .header(Header.HOST, "pan.baidu.com")
                .execute();
        if (!isOK(response)) {
            return null;
        }
        return JsonUtils.read(response.body(), CloudSearch.class);
    }

    /**
     * 判断指定文件是否存在
     */
    @Nullable
    public Boolean exists(String dir, String key) {
        CloudSearch cloudSearch = search(dir, key);
        return cloudSearch == null ? null : CollUtil.isNotEmpty(cloudSearch.getList());
    }
}