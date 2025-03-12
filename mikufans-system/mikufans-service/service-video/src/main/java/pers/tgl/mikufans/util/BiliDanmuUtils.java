package pers.tgl.mikufans.util;

import pers.tgl.mikufans.model.BiliDanmu;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BiliDanmuUtils {
    private static final Pattern pattern = Pattern.compile("<d p=\"([^\"]+)\">(.*?)</d>");

    public static List<BiliDanmu> parse(String xmlBody) {
        Matcher matcher = pattern.matcher(xmlBody);
        List<BiliDanmu> biliDanmus = new LinkedList<>();
        while (matcher.find()) {
            BiliDanmu biliDanmu = new BiliDanmu();
            String[] params = matcher.group(1).split(",");
            if (params.length != 9 || matcher.groupCount() != 2) {
                continue;
            }
            biliDanmu.setStime(Float.parseFloat(params[0]));
            biliDanmu.setMode(Integer.parseInt(params[1]));
            biliDanmu.setSize(Integer.parseInt(params[2]));
            biliDanmu.setColor(Integer.parseInt(params[3]));
            biliDanmu.setDate(Integer.parseInt(params[4]));
            biliDanmu.setPool(Integer.parseInt(params[5]));
            biliDanmu.setUhash(params[6]);
            biliDanmu.setDmid(Long.parseLong(params[7]));
            biliDanmu.setWeight(Integer.parseInt(params[8]));
            biliDanmu.setText(matcher.group(2));
            biliDanmus.add(biliDanmu);
        }
        return biliDanmus;
    }
}