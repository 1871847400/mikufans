package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.system.SysParam;

import java.util.Map;
import java.util.function.Consumer;

public interface SysParamService extends BaseService<SysParam> {
    @Nullable
    String getString(String key);

    @Nullable
    Boolean getBoolean(String key);

    boolean isTrue(String key);

    int getInt(String key, int defaultValue);

    @Nullable
    Long getLong(String key);

    @Nullable
    Double getDouble(String key);

    /**
     * 设置参数值,如果value=null代表删除
     */
    void setValue(String key, @Nullable Object value);
    /**
     * 使用like查询参数
     */
    Map<String, String> getMap(String likeStr, boolean asc);
    /**
     * 监听参数的值变化,会立即执行 （需要通过service修改才会触发）
     * 便于参数值改变后立即生效
     * @return 取消监听方法
     */
    Runnable watch(String key, Consumer<String> callback);
}