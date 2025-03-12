package pers.tgl.mikufans.controller.business;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.dto.DanmuImportDto;
import pers.tgl.mikufans.service.VideoDanmuService;

@RestController
@RequestMapping("/admin/video/danmu")
@RequiredArgsConstructor
public class DanmuAdminController extends BaseController {
    private final VideoDanmuService videoDanmuService;

    @PostMapping("/import")
    public String importDanmus(@RequestBody @Validated DanmuImportDto dto) {
        return videoDanmuService.importBatch(dto);
    }
}