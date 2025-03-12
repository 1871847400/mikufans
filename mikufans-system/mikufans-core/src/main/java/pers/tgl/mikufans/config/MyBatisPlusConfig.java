package pers.tgl.mikufans.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("pers.tgl.mikufans.mapper")
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        // 如果设置overflow属性会在pageNum超出限制后自动改pageNum=1
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        /**
         * 乐观锁插件,需要在属性上加@Version
         * 仅支持 updateById(id) 与 update(entity, wrapper) 方法
         * 在 update(entity, wrapper) 方法下, wrapper 不能复用
         */
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 数据权限插件
//        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
//        dataPermissionInterceptor.setDataPermissionHandler(new GlobalDataPermissionHandler());
//        interceptor.addInnerInterceptor(dataPermissionInterceptor);
        //记录SQL的变动,会打印日志
//        interceptor.addInnerInterceptor(new DataChangeRecorderInnerInterceptor());
        return interceptor;
    }
}