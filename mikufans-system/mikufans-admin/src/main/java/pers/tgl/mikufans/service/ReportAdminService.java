package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.tgl.mikufans.domain.user.UserReport;
import pers.tgl.mikufans.vo.ReportAuditDto;
import pers.tgl.mikufans.vo.UserReportVo;

public interface ReportAdminService extends BaseService<UserReport> {
    /**
     * 查询举报列表
     * 可能同一个业务对象被多次举报,结果会合并为一条
     */
    IPage<UserReportVo> search(UserReport params);
    /**
     * 查询具体业务的举报列表,来自不同用户的举报
     */
    IPage<UserReport> getReportList(Long targetId);
    /**
     * 审核举报
     */
    void audit(ReportAuditDto auditDto);
}