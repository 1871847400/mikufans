package pers.tgl.mikufans.config;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

//@Configuration
@RequiredArgsConstructor
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
    @Override
    public RestHighLevelClient elasticsearchClient() {
        return null;
    }
}
