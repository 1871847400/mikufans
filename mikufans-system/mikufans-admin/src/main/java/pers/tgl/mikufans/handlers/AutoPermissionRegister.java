package pers.tgl.mikufans.handlers;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.anno.PermType;
import pers.tgl.mikufans.domain.system.SysPerm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 自动为每个标记了注解的实体类创建权限标识,不存在的权限标识会自动创建
 * 必须使用注解标识名称和分组
 * 权限标识会自动使用类名作为其中的一部分，例如：business.video_part.select
 */
@Component
public class AutoPermissionRegister implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //创建一级权限分组
        Map<PermGroup, Long> groupIds = new LinkedHashMap<>();
        for (PermGroup permFlag : PermGroup.values()) {
            String permKey = permFlag.getPermPrefix();
            SysPerm sysPerm = getByKey(permKey);
            if (sysPerm == null) {
                sysPerm = new SysPerm();
                sysPerm.setPermKey(permKey);
                sysPerm.setPermName(permFlag.getPermName());
                sysPerm.setPid(0L);
                sysPerm.setCreateBy(0L);
                Db.save(sysPerm);
            }
            groupIds.put(permFlag, sysPerm.getId());
        }
        //遍历所有实体类
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("pers.tgl.mikufans", PermFlag.class);
        for (Class<?> cls : classes) {
            //需要有这个注解才自动添加权限记录
            PermFlag permFlag = cls.getAnnotation(PermFlag.class);
            // system:user_stats
            String prefix = permFlag.group().getPermPrefix() + ":" + StrUtil.toUnderlineCase(cls.getSimpleName());
            SysPerm sysPerm = getByKey(prefix);
            if (sysPerm == null) {
                sysPerm = new SysPerm();
                sysPerm.setPermKey(prefix);
                sysPerm.setPid(groupIds.get(permFlag.group()));
                sysPerm.setPermName(permFlag.name());
                sysPerm.setCreateBy(0L);
                //增加权限种类
                Db.save(sysPerm);
            }
            //为各种操作创建子权限
            for (PermType permType : PermType.values()) {
                String permKey = prefix + ":" + permType.getValue();
                SysPerm childPerm = getByKey(permKey);
                if (childPerm == null) {
                    childPerm = new SysPerm();
                    childPerm.setPermKey(permKey);
                    childPerm.setPid(sysPerm.getId());
                    childPerm.setPermName(permFlag.name() + "-" + permType.getLabel());
                    childPerm.setCreateBy(0L);
                    Db.save(childPerm);
                }
            }
        }
    }

    private SysPerm getByKey(String permKey) {
        return Db.lambdaQuery(SysPerm.class)
                .eq(SysPerm::getPermKey, permKey)
                .last("limit 1")
                .one();
    }
}