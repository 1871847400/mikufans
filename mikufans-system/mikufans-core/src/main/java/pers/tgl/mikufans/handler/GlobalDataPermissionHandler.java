package pers.tgl.mikufans.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.util.SecurityUtils;

/**
 * 用户执行update和delete的SQL时拼接 where {创建数据的用户id} = {请求的用户id}
 */
@Deprecated
public class GlobalDataPermissionHandler implements MultiDataPermissionHandler {
    /**
     * 这里返回NULL就代表不拼接where
     * where为null一般是整表操作,所以不处理这种情况
     * 这里只对删除和更新作权限判断
     *
     * 注意：需要为user_id的mapper设置 @InterceptorIgnore(dataPermission = "true") true代表每个方法都不做检查
     * @param mappedStatementId 示例: pers.tgl.mikufans.mapper.VideoWatchHistoryMapper.delete
     */
    @Override
    public Expression getSqlSegment(Table table, @Nullable Expression where, String mappedStatementId) {
        Expression expression = null;
        //一般where为null都是查找全部
        //这里只对更新和删除
        if (where != null && mappedStatementId != null) {
            if (mappedStatementId.contains("update") || mappedStatementId.contains("delete")) {
                Long contextUserId = SecurityUtils.getContextUserId(false);
                if (contextUserId == null) {
                    contextUserId = -1L;
                }
                expression = new EqualsTo(new Column("user_id"), new LongValue(contextUserId));
            }
        }
        return expression;
    }
}