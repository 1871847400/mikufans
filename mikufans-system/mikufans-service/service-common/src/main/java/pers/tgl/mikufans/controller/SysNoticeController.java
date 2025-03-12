package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.service.SysNoticeService;

@RestController
@RequestMapping("/sys/notice")
@Validated
@RequiredArgsConstructor
public class SysNoticeController extends BaseController {
    private final SysNoticeService sysNoticeService;
}