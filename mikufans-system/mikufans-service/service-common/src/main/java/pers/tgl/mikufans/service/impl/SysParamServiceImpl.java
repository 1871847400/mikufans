package pers.tgl.mikufans.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.system.SysParam;
import pers.tgl.mikufans.mapper.SysParamMapper;
import pers.tgl.mikufans.service.SysParamService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class SysParamServiceImpl extends BaseServiceImpl<SysParam, SysParamMapper> implements SysParamService {
    private static final Map<String, List<Consumer<String>>> tracks = new ConcurrentHashMap<>();

    @Override
    public String getString(String key) {
        SysParam sysParam = getOneBy(SysParam::getParamKey, key);
        return sysParam != null ? sysParam.getParamValue() : null;
    }

    @Override
    public Boolean getBoolean(String key) {
        return BooleanUtil.toBooleanObject(getString(key));
    }

    @Override
    public boolean isTrue(String key) {
        return BooleanUtil.isTrue(getBoolean(key));
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return NumberUtil.parseInt(getString(key), defaultValue);
    }

    @Override
    public Long getLong(String key) {
        return NumberUtil.parseLong(getString(key), null);
    }

    @Override
    public Double getDouble(String key) {
        return NumberUtil.parseDouble(getString(key), null);
    }

    @Override
    public synchronized void setValue(String key, Object value) {
        SysParam sysParam = getOneBy(SysParam::getParamKey, key);
        if (sysParam == null) {
            sysParam = new SysParam();
            sysParam.setParamKey(key);
        }
        if (sysParam.getParamValue() == null || !Objects.equals(sysParam.getParamValue(), value)) {
            if (value == null) {
                removeById(sysParam);
            } else {
                sysParam.setParamValue(StrUtil.toString(value));
                saveOrUpdate(sysParam);
            }
            List<Consumer<String>> _tracks = tracks.get(key);
            if (_tracks != null) {
                _tracks.forEach(c->{
                    try {
                        c.accept(StrUtil.toString(value));
                    } catch (Exception ignored) {}
                });
            }
        }
    }

    @Override
    public Map<String, String> getMap(String likeStr, boolean asc) {
        Map<String, String> map = new LinkedHashMap<>();
        String col = CharSequenceUtil.toUnderlineCase(SysParam.Fields.paramKey);
        List<SysParam> list = lambdaQuery()
                .apply(col + " like {0}", likeStr) //如果用自带的like会强制加上%%
                .orderBy(true, asc, SysParam::getParamKey)
                .list();
        for (SysParam sysParam : list) {
            map.put(sysParam.getParamKey(), sysParam.getParamValue());
        }
        return map;
    }

    @Override
    public synchronized Runnable watch(String key, Consumer<String> callback) {
        callback.accept(getString(key));
        if (!tracks.containsKey(key)) {
            tracks.put(key, new ArrayList<>(1));
        }
        tracks.get(key).add(callback);
        return ()->{
          tracks.get(key).remove(callback);
          if (tracks.get(key).isEmpty()) {
              tracks.remove(key);
          }
        };
    }
}