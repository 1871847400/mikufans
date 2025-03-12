package pers.tgl.mikufans.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.system.SysDictType;
import pers.tgl.mikufans.mapper.SysDictTypeMapper;
import pers.tgl.mikufans.service.SysDictDataService;
import pers.tgl.mikufans.service.SysDictTypeService;

@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends BaseServiceImpl<SysDictType, SysDictTypeMapper> implements SysDictTypeService {
    private final SysDictDataService sysDictDataService;
}