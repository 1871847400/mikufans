package pers.tgl.mikufans.validator.db;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import pers.tgl.mikufans.util.SecurityUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class DBExistsValidator implements ConstraintValidator<DBExists, Object> {
    private DBExists instance;
    private String keyColumn;
    private String userIdColumn;
    private List<String> ignores;

    @Override
    public void initialize(DBExists constraintAnnotation) {
        instance = constraintAnnotation;
        this.keyColumn = instance.keyColumn();
        if (StrUtil.isBlank(keyColumn)) {
            this.keyColumn = TableInfoHelper.getTableInfo(instance.value()).getKeyColumn();
            if (keyColumn == null) {
                throw new RuntimeException("无法找到 " + instance.value() + " 的主键");
            }
        }
        this.userIdColumn = instance.userIdColumn();
        this.ignores = Arrays.asList(constraintAnnotation.ignores());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value != null && !ignores.contains(value.toString())) {
            Long contextUserId = SecurityUtils.getContextUserId(false);
            boolean result = Db.query(instance.value())
                    .eq(keyColumn, value)
                    .eq(instance.checkUserId(), userIdColumn, contextUserId)
                    .exists();
            return instance.errorInExist() != result;
        }
        return true;
    }
}