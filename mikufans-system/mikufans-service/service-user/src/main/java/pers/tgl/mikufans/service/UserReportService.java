package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.user.UserReport;
import pers.tgl.mikufans.dto.UserReportDto;

public interface UserReportService extends BaseService<UserReport> {
    /**
     * 当前用户对目标的举报
     */
    @Nullable
    UserReport getByTargetId(Long targetId);
    /**
     * 创建举报,会先删除旧的
     */
    void createOne(UserReportDto dto);
}