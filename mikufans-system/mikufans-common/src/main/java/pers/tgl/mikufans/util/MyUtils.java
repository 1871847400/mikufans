package pers.tgl.mikufans.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import org.apache.tika.Tika;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.exception.CustomException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyUtils {
    private static final Random random = new Random();
    private static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int charsLength = chars.length();

    public static String generateString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String generateNumbers(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 从字符串中提取数字
     */
    public static Integer extractInteger(String str, Integer def) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = Pattern.compile("\\d+").matcher(str);
        while (matcher.find()) {
            sb.append(matcher.group(0));
        }
        try {
            return Integer.parseInt(sb.toString());
        } catch (Exception e) {
            return def;
        }
    }

    public static String join(Collection<?> value, CharSequence delimiter) {
        return value.stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    public static String createSerialNumber(String prefix, int totalLength) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = prefix.length(); i < totalLength; i++) {
            sb.append(MathUtils.randomInt(0, 9));
        }
        return sb.toString();
    }

    public static void writeJSONString(ServletResponse response, String jsonContent) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setContentType(ContentType.JSON.getValue());
        PrintWriter writer = response.getWriter();
        writer.print(jsonContent);
        writer.flush();
    }

    public static void writePlainText(ServletResponse response, String content) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setContentType(ContentType.TEXT_PLAIN.getValue());
        PrintWriter writer = response.getWriter();
        writer.print(content);
        writer.flush();
    }

    public static void writeFile(HttpServletResponse response, File file, @Nullable Duration cache) throws IOException {
        response.setContentType(getMimeType(file));
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        if (cache != null) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=" + cache.getSeconds());
        }
        try (ServletOutputStream os = response.getOutputStream()) {
            os.write(FileUtil.readBytes(file));
            os.flush();
        }
        response.flushBuffer();
    }

    /**
     * 根据文件头获取文件类型
     * application/json
     * video/mp4
     * ...
     */
    @Nullable
    public static String getMimeType(File file) {
        Tika tika = new Tika();
        try {
            return tika.detect(file);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将字符串按指定每段长度进行分割
     */
    public static List<String> splitString(String str, int partLen) {
        List<String> list = new ArrayList<>();
        int count = str.length() / partLen + (str.length() % partLen == 0 ? 0 : 1);
        for (int i = 0; i < count; i++) {
            int end = Math.min(i * partLen + partLen, str.length());
            list.add(str.substring(i * partLen, end));
        }
        return list;
    }

    public static void mergeChunks(List<File> chunks, File mergeFile) throws IOException {
        if (mergeFile.exists()) {
            FileUtil.del(mergeFile);
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(mergeFile, "rw");
        int offset = 0;
        for (File chunk : chunks) {
            byte[] bytes = FileUtil.readBytes(chunk);
            randomAccessFile.seek(offset);
            randomAccessFile.write(bytes, 0, bytes.length);
            offset += bytes.length;
        }
        randomAccessFile.close();
    }

    /**
     * 获得文件base64编码，并且根据文件后缀名添加前缀，如果是用于<img>标签则必须要加前缀才能正确显示
     */
    public static String base64FileWithPrefix(File file) {
        String result = Base64.getEncoder().encodeToString(FileUtil.readBytes(file));
        MediaType mediaType = getMediaType(file.getPath());
        if (mediaType != null) {
            result = "data:" + mediaType + ";base64," + result;
        }
        return result;
    }


    private static final String emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
    private static final Pattern emailPattern = Pattern.compile(emailRegex);
    public static boolean isValidEmail(String str) {
        if (StrUtil.isBlank(str) || !str.contains("@")) {
            return false;
        }
        Matcher matcher = emailPattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 打乱list里的元素顺序后返回新的list
     */
    public static <T> List<T> shuffleList(List<T> list) {
        List<T> result = new ArrayList<>(list.size());
        List<Integer> record = new ArrayList<>(list.size());
        int index;
        for (int i = 0; i < list.size(); i++) {
            do {
                index = random.nextInt(list.size());
            } while (record.contains(index));
            result.add(list.get(index));
            record.add(index);
        }
        return result;
    }

    /**
     * 根据文件后缀名获取媒体类型
     * 可以直接用后缀(前面要有小数点): .jpg .mp4
     */
    public static MediaType getMediaType(String filepath) {
        return MediaTypeFactory.getMediaType(filepath)
                .orElse(null);
    }

    public static <T extends Enum<T>> T parseEnumName(Class<T> enumClass, String enumName) {
        T result = null;
        try {
            result = Enum.valueOf(enumClass, enumName.toUpperCase());
        } catch (Exception e) {
            try {
                result = Enum.valueOf(enumClass, enumName);
            } catch (Exception ignored) {
                throw new CustomException("参数错误");
            }
        }
        return result;
    }

    /**
     * 对集合元素进行排序,数字按大小,字符按编码
     * [3, 18, 5, b, a, x, 1, 1000, 啊]
     * 排序后
     * [1, 3, 5, 18, 1000, a, b, x, 啊]
     */
    public static <T> List<T> sortList(List<T> list) {
        return list.stream()
                .sorted((a, b) -> {
                    if (NumberUtil.isInteger(a.toString()) && NumberUtil.isInteger(b.toString())) {
                        return NumberUtil.compare(NumberUtil.parseInt(a.toString()), NumberUtil.parseInt(b.toString()));
                    } else {
                        return StrUtil.compare(a.toString(), b.toString(), true);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 3s = Duration.ofSeconds(3)
     * 5d = Duration.ofDays(5)
     */
    public static Duration parseDuration(String str) {
        if (StrUtil.isBlank(str) || str.length() == 1) {
            throw new RuntimeException("时间格式有误");
        }
        String numStr = str.substring(0, str.length() - 1);
        if (!NumberUtil.isInteger(numStr)) {
            throw new RuntimeException("时间格式有误");
        }
        int num = NumberUtil.parseInt(numStr);
        if (str.endsWith("s")) {
            return Duration.ofSeconds(num);
        }
        if (str.endsWith("m")) {
            return Duration.ofMinutes(num);
        }
        if (str.endsWith("h")) {
            return Duration.ofHours(num);
        }
        if (str.endsWith("d")) {
            return Duration.ofDays(num);
        }
        throw new RuntimeException("时间格式有误");
    }

    /**
     * seconds => 00:11:10.000
     */
    public static String formatSeconds(float seconds) {
        return LocalTime.ofSecondOfDay((long) seconds)
                .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    /**
     * 根据请求头的UserAgent获取使用的设备类型
     * @return 0未知 1电脑 2手机
     */
    public static int getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (StrUtil.isNotBlank(userAgent)) {
            userAgent = userAgent.toLowerCase();
            boolean isApp = userAgent.contains("iphone") ||
                    userAgent.contains("ipad") ||
                    userAgent.contains("android");
            return isApp ? 2 : 1;
        }
        return 0;
    }

    public static boolean isValidImage(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            BufferedImage bufferedImage = ImageIO.read(bais);
            if (bufferedImage == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            IoUtil.close(bais);
        }
        return true;
    }

    /**
     * 今天 => xx小时前 xx分钟前 xx秒前 刚刚
     * 昨天 => 昨天 08:23
     * 今年内 => 11-15
     * 不是今年的内容 => 2022-11-15
     */
    public static String formatHumanTime(Date date) {
        Date now = new Date();
        if (DateUtil.isSameDay(DateUtil.yesterday(), date)) {
            return "昨天 " + DateUtil.format(date, "HH:mm");
        }
        if (DateUtil.isSameDay(date, now)) {
            long hour = DateUtil.between(date, now, DateUnit.HOUR);
            if (hour > 0) {
                return hour + "小时前";
            }
            long minute = DateUtil.between(date, now, DateUnit.MINUTE);
            if (minute > 0) {
                return minute + "分钟前";
            }
            long second = DateUtil.between(date, now, DateUnit.SECOND);
            if (second > 10) {
                return second + "秒前";
            }
            return "刚刚";
        }
        if (now.getYear() == date.getYear()) {
            return DateUtil.format(date, "MM-dd");
        }
        return DateUtil.formatDate(date);
    }

    /**
     * 格式化持续时间  3:33:22  23:40 小时为0不会显示
     */
    public static String formatDuration(Duration duration) {
        return formatDuration(duration, "%d:%02d:%02d");
    }

    public static String formatChineseDuration(Duration duration) {
        return formatDuration(duration, "%d天%02d分%02d秒");
    }

    private static String formatDuration(Duration duration, String format) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                format,
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        if (absSeconds / 3600 == 0) {
            positive = positive.substring(2);
        }
        return seconds < 0 ? "-" + positive : positive;
    }

    /**
     * 拼接文件路径
     * /data /user  => /data/user
     * /data/ /user/ => /data/user/
     */
    public static String joinFilepath(String ...paths) {
        if (ArrayUtil.isEmpty(paths)) {
            return "";
        }
        return StrUtil.join("/", paths)
                .replaceAll("///", "/")
                .replaceAll("//", "/");
    }

    /**
     * /a/b/c.txt => /a/b
     * /a => /
     * a => /
     * '' => /
     */
    public static String getParentUri(String uri) {
        int lastIndex = uri.lastIndexOf("/");
        if (lastIndex == -1) {
            return "/";
        }
        return uri.substring(0, Math.max(lastIndex, 1));
    }

    /**
     * 从所有公共无参方法中找到第一个返回指定类型的方法，并获取
     */
    @SuppressWarnings("all")
    public static <R> R getReturnValue(Object obj, Class<R> returnType) {
        if (obj == null) {
            return null;
        }
        for (Method publicMethod : ReflectUtil.getPublicMethods(obj.getClass())) {
            if (publicMethod.getReturnType().equals(returnType) && publicMethod.getParameterTypes().length == 0) {
                try {
                    return (R) publicMethod.invoke(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        return null;
    }
}