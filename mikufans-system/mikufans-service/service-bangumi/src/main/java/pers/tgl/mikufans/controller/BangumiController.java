package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.params.BangumiParams;
import pers.tgl.mikufans.service.BangumiService;
import pers.tgl.mikufans.vo.BangumiVo;

@RestController
@RequestMapping("/bangumi")
@RequiredArgsConstructor
public class BangumiController extends BaseController {
    private final BangumiService bangumiService;

    @GetMapping("/{id}")
    public BangumiVo getById(@PathVariable Long id) {
        return bangumiService.getVoById(id);
    }

    @AppLog("搜索节目")
    @GetMapping("/search")
    public IPage<BangumiVo> search(@Validated BangumiParams search) {
        return bangumiService.search(search);
    }
}