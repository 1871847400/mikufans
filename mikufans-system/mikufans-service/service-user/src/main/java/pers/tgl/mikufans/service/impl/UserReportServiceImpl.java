package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.domain.user.UserReport;
import pers.tgl.mikufans.dto.UserReportDto;
import pers.tgl.mikufans.mapper.UserReportMapper;
import pers.tgl.mikufans.service.UserReportService;
import pers.tgl.mikufans.util.SecurityUtils;

@Service
@RequiredArgsConstructor
public class UserReportServiceImpl extends BaseServiceImpl<UserReport, UserReportMapper> implements UserReportService {
    @Override
    public UserReport getByTargetId(Long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (!isValidId(contextUserId) || !isValidId(targetId)) {
            return null;
        }
        return wrapper().eq(UserReport::getTargetId, targetId)
                .eq(UserReport::getUserId, contextUserId)
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOne(UserReportDto dto) {
        UserReport oldReport = getByTargetId(dto.getTargetId());
        if (oldReport != null) {
            removeById(oldReport);
        }
        UserReport report = BeanUtil.toBean(dto, UserReport.class);
        save(report);
    }
}