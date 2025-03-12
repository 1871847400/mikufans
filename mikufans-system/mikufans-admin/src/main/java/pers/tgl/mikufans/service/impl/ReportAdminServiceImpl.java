package pers.tgl.mikufans.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.system.ReportBehavior;
import pers.tgl.mikufans.domain.user.UserReport;
import pers.tgl.mikufans.event.UserReportAuditEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserReportMapper;
import pers.tgl.mikufans.service.MessageService;
import pers.tgl.mikufans.service.ReportAdminService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.ReportAuditDto;
import pers.tgl.mikufans.vo.UserReportVo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportAdminServiceImpl extends BaseServiceImpl<UserReport, UserReportMapper> implements ReportAdminService {
    private final MessageService messageService;

    @Override
    public IPage<UserReportVo> search(UserReport params) {
        PageImpl<UserReportVo> page = wrapper()
                .select(UserReport::getReportType,
                        UserReport::getTargetId)
                //查询分组后的每一组内的数量
                .selectCount(UserReport::getId, UserReportVo::getReportCount)
                .eq(UserReport::getAudit, AuditStatus.UNKNOWN)
                .groupBy(UserReport::getReportType,
                        UserReport::getTargetId)
                .setEntity(params)
                .page(BaseController.createPage(), UserReportVo.class);
        return page.fill(vo -> {
            vo.setTarget(messageService.getModel(vo.getReportType(), vo.getTargetId()));
        });
    }

    @Override
    public IPage<UserReport> getReportList(Long targetId) {
        return wrapper()
                .leftJoin(ReportBehavior.class, ReportBehavior::getId, UserReport::getBehaviorId)
                .selectAssociation(ReportBehavior.class, UserReport::getBehaviorName, builder -> {
                    return builder.result(ReportBehavior::getBehavior);
                })
                .eq(UserReport::getTargetId, targetId)
                .page(BaseController.createPage());
    }


    @Override
    public void audit(ReportAuditDto dto) {
        List<UserReport> list = lambdaQuery()
                .eq(UserReport::getTargetId, dto.getTargetId())
                .eq(UserReport::getReportType, dto.getReportType())
                .eq(UserReport::getAudit, AuditStatus.UNKNOWN)
                .list();
        if (list.isEmpty()) {
            throw new CustomException("没有需要处理的举报信息");
        }
        boolean result = lambdaUpdate().set(UserReport::getAudit, dto.getAudit())
                .in(UserReport::getId, map(list, UserReport::getId))
                .update(new UserReport());
        if (result) {
            //通知相关的业务做出处理
            publishEvent(new UserReportAuditEvent(this,
                    dto.getTargetId(),
                    dto.getReportType(),
                    dto.getMessage(),
                    dto.getAudit(),
                    map(list, UserReport::getUserId)));
        }
    }
}