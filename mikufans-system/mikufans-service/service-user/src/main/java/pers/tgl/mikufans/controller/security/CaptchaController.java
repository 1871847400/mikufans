package pers.tgl.mikufans.controller.security;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.dto.security.CaptchaDto;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.model.VerificationCode;
import pers.tgl.mikufans.service.SysParamService;
import pers.tgl.mikufans.util.PuzzleCaptcha;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.vo.PuzzleVo;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

@RestController
@Slf4j
@RequestMapping("/auth/captcha")
@RequiredArgsConstructor
public class CaptchaController extends BaseController {
    private final RedisUtils redisUtils;
    private final AppConfig appConfig;
    private final SysParamService sysParamService;

    /**
     * 生成拼图验证码 3分钟有效期
     */
    @GetMapping("/generate")
    @AppLog("生成拼图验证码")
    public PuzzleVo generate() {
        String captchaPath = appConfig.getResource().getCaptchaPath();
        File puzzleFolder = new File(captchaPath, "puzzles");
        File imageFolder = new File(captchaPath, "images");
        if (FileUtil.isEmpty(puzzleFolder) || FileUtil.isEmpty(imageFolder)) {
            log.error("拼图文件夹不能为空！！");
            throw new CustomException(Code.SERVER_ERROR);
        }
        File puzzle = RandomUtil.randomEle(FileUtil.loopFiles(puzzleFolder));
        File image = RandomUtil.randomEle(FileUtil.loopFiles(imageFolder));
        PuzzleCaptcha.Result result;
        try {
            result = PuzzleCaptcha.builder()
                    .setPuzzleFile(puzzle)
                    .setRawImageFile(image)
                    .build();
            log.info("已生成拼图验证 {}", result.value);
        } catch (IOException e) {
            throw new CustomException(Code.SERVER_ERROR);
        }
        //随机拼图id
        String puzzleId = IdUtil.fastSimpleUUID();

        VerificationCode vCode = new VerificationCode();
        vCode.setId(puzzleId);
        vCode.setValue(result.value);
        vCode.setTimestamp(System.currentTimeMillis());
        vCode.setPass(false);
        String redisKey = "puzzle:" + puzzleId;
        Duration duration = Duration.ofMinutes(3);
        redisUtils.set(redisKey, vCode, duration);

        PuzzleVo puzzleVO = new PuzzleVo();
        puzzleVO.setPuzzle(result.puzzle);
        puzzleVO.setImage(result.image);
        puzzleVO.setOffset(result.offsetY);
        puzzleVO.setPuzzleId(puzzleId);
        puzzleVO.setExpireAt(System.currentTimeMillis() + duration.toMillis());
        return puzzleVO;
    }

    /**
     * 校验验证码
     */
    @PostMapping("/validate")
    @AppLog("校验拼图验证码")
    public void validate(@RequestBody @Validated CaptchaDto dto) {
        String redisKey = "puzzle:" + dto.getPuzzleId();
        VerificationCode vCode = redisUtils.getObject(redisKey, VerificationCode.class);
        //验证码已经失效 或 验证码已经使用过了
        if (vCode == null || Boolean.TRUE.equals(vCode.getPass())) {
            throw new CustomException("验证码已失效");
        }
        //精确误差
        float accuracy = appConfig.getResource().getAccuracy();
        if (sysParamService.isTrue("puzzle_captcha_enable")
                && Math.abs(vCode.getValue() - dto.getValue()) > accuracy) {
            throw new CustomException("拼图失败");
        }
        //设置验证码已通过
        vCode.setPass(true);
        redisUtils.set(redisKey, vCode, Duration.ofSeconds(30));
    }

    /**
     * 判断指定验证码是否已经通过验证
     */
    public void validated(String puzzleId) {
        String redisKey = "puzzle:" + puzzleId;
        VerificationCode vCode = redisUtils.getObject(redisKey, VerificationCode.class);
        if (vCode == null || !Boolean.TRUE.equals(vCode.getPass())) {
            throw new CustomException("验证码已失效,请重试！");
        }
        redisUtils.del(redisKey);
    }
}