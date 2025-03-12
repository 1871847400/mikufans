package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.system.SysExtDict;
import pers.tgl.mikufans.mapper.SysExtDictMapper;
import pers.tgl.mikufans.service.SysExtDictService;
import pers.tgl.mikufans.util.RedisUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SysExtDictServiceImpl extends BaseServiceImpl<SysExtDict, SysExtDictMapper> implements SysExtDictService {
    private final RedisUtils redisUtils;
    private static final String REDIS_KEY = "es-ext-word-update-time";
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void saveDto(SysExtDict sysExtDict) {
        if (super.save(sysExtDict)) {
            redisUtils.set(REDIS_KEY, DateUtil.formatHttpDate(new Date()), null);
        }
    }

    @Override
    public String filterSensitiveWord(String content, char replaceChar) {
        AnalyzeRequest ar = AnalyzeRequest
                .buildCustomAnalyzer("ik_smart")
                .build(content);
        AnalyzeResponse response = elasticsearchRestTemplate.execute(client -> {
            return client.indices().analyze(ar, RequestOptions.DEFAULT);
        });
        List<String> tokens = response.getTokens()
                .stream()
                .map(AnalyzeResponse.AnalyzeToken::getTerm)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(tokens)) {
            return content;
        }
        List<SysExtDict> list = wrapper()
                .select(SysExtDict::getTerm)
                .in(SysExtDict::getTerm, tokens)
                .eq(SysExtDict::getIllegal, 1)
                .list();
        for (SysExtDict sysExtDict : list) {
            String term = sysExtDict.getTerm();
            content = StrUtil.replace(content, term, StrUtil.repeat(replaceChar, term.length()), true);
        }
        return content;
    }

    @Override
    public boolean removeById(SysExtDict entity) {
        if (super.removeById(entity)) {
            redisUtils.set(REDIS_KEY, DateUtil.formatHttpDate(new Date()), null);
        }
        return true;
    }

    @Override
    public List<String> getWordList() {
        return Db.lambdaQuery(SysExtDict.class)
                .select(SysExtDict::getTerm)
                .list()
                .stream()
                .map(SysExtDict::getTerm)
                .collect(Collectors.toList());
    }

    @Override
    public String getLastUpdateDate() {
        return redisUtils.getString(REDIS_KEY, "");
    }
}