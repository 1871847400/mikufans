package pers.tgl.mikufans.controller;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.service.SysExtDictService;
import pers.tgl.mikufans.util.MyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/es")
@RequiredArgsConstructor
public class SysExtDictController extends BaseController {
    private final SysExtDictService sysExtDictService;

    /**
     * HTTP请求需要返回两个标头，一个是Last Modified，另一个是ETag。
     * 这两个都是字符串类型，如果其中任何一个发生变化，插件将获取新的分词来更新单词库。
     * HTTP请求返回的内容格式是每行一个单词，换行符由\n表示。
     * 满足上述两个要求可以实现热字更新，而无需重新启动ES实例。
     * 您可以将需要自动更新的热门词放在UTF-8编码的.txt文件中。
     * 将其放置在nginx或其他简单的HTTP服务器下。当.txt文件被修改时，
     * HTTP服务器将在客户端请求该文件时自动返回相应的Last modified和ETag。
     * 您还可以创建一个单独的工具，从业务系统中提取相关词汇并更新此.txt文件。
     */
    @GetMapping("/ext_dict")
    public void getTermList(HttpServletResponse response) throws IOException {
        String lastUpdateDate = sysExtDictService.getLastUpdateDate();
        response.setHeader("Last-Modified", lastUpdateDate);
        response.setHeader("ETag", lastUpdateDate);
        MyUtils.writePlainText(response, StrUtil.join("\n", sysExtDictService.getWordList()));
    }
}