package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.system.SysDictData;

import java.util.List;

public interface SysDictDataService extends BaseService<SysDictData> {
    /**
     * 通过字典类型的名称获取所有字典数据
     */
    List<SysDictData> getDictData(String dictType);
}