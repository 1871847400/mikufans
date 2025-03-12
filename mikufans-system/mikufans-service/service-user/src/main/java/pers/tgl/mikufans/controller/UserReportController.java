package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.domain.system.ReportBehavior;
import pers.tgl.mikufans.domain.user.UserReport;
import pers.tgl.mikufans.dto.UserReportDto;
import pers.tgl.mikufans.service.UserReportService;

import java.util.List;

@RestController
@RequestMapping("/user/report")
@RequiredArgsConstructor
public class UserReportController extends BaseController {
    private final UserReportService userReportService;

    @AppLog("查询举报")
    @GetMapping("/{targetId}")
    public UserReport find(@PathVariable Long targetId) {
        return userReportService.getByTargetId(targetId);
    }

    @AppLog("提交举报")
    @PostMapping
    public void create(@RequestBody @Validated UserReportDto dto) {
        userReportService.createOne(dto);
    }

    @GetMapping("/behaviors")
    public List<ReportBehavior> getBehaviors() {
        return Db.list(ReportBehavior.class);
    }
}