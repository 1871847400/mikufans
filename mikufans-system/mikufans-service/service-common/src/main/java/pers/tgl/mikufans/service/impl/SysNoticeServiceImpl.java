package pers.tgl.mikufans.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.system.SysNotice;
import pers.tgl.mikufans.mapper.SysNoticeMapper;
import pers.tgl.mikufans.service.SysNoticeService;

@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends BaseServiceImpl<SysNotice, SysNoticeMapper> implements SysNoticeService {

}