package pers.tgl.mikufans.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.system.SysDictData;
import pers.tgl.mikufans.mapper.SysDictDataMapper;
import pers.tgl.mikufans.service.SysDictDataService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends BaseServiceImpl<SysDictData, SysDictDataMapper> implements SysDictDataService {
    @Override
    public List<SysDictData> getDictData(String dictType) {
        return wrapper()
                .eq(SysDictData::getDictType, dictType)
                .orderByAsc(SysDictData::getDictSort)
                .list(SysDictData.class);
    }
}