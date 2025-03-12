package pers.tgl.mikufans.controller.system;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.system.SysDictData;
import pers.tgl.mikufans.service.SysDictDataService;
import pers.tgl.mikufans.service.SysDictTypeService;

import java.util.List;

@RestController
@RequestMapping("/system/dict")
@RequiredArgsConstructor
public class SysDictController extends BaseController {
    private final SysDictTypeService sysDictTypeService;
    private final SysDictDataService sysDictDataService;

    @GetMapping("/data/{type}")
    public List<SysDictData> getDictData(@PathVariable String type) {
        return sysDictDataService.getDictData(type);
    }
}