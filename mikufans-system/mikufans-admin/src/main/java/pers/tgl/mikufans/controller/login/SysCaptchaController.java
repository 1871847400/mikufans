package pers.tgl.mikufans.controller.login;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.util.MathUtils;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.vo.CaptureImageVo;

import java.time.Duration;

@RestController
@RequestMapping("/admin/login/captcha")
@RequiredArgsConstructor
public class SysCaptchaController {
    private final RedisUtils redisUtils;
    /**
     * 获取验证码图片
     */
    @GetMapping("/{size}")
    public CaptureImageVo getImage(@PathVariable String size) {
        LineCaptcha lineCaptcha = null;
        try {
            int[] sizes = StrUtil.splitToInt(size, '_');
            int width = MathUtils.clamp(sizes[0], 10, 999);
            int height = MathUtils.clamp(sizes[1], 10, 999);
            lineCaptcha = CaptchaUtil.createLineCaptcha(width, height, 4, 20);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new CustomException("参数错误");
        }
        String uuid = IdUtil.fastSimpleUUID();
        redisUtils.set("captcha:"+uuid, lineCaptcha.getCode(), Duration.ofMinutes(1));
        CaptureImageVo result = new CaptureImageVo();
        result.setUuid(uuid);
        result.setImage(lineCaptcha.getImageBase64Data());
        return result;
    }
    /**
     * 检测验证码
     */
    public void verify(String uuid, String code) {
        String key = "captcha:" + uuid;
        String realCode = redisUtils.getString(key);
        if (StrUtil.isBlank(realCode)) {
            throw new CustomException("验证码已到期");
        }
        redisUtils.del(key);
        if (!StrUtil.equalsIgnoreCase(code, realCode)) {
            throw new CustomException("验证码错误");
        }
    }
}