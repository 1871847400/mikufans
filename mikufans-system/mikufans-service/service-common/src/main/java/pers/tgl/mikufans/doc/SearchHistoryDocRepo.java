package pers.tgl.mikufans.doc;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryDocRepo extends ElasticsearchRepository<SearchHistoryDoc, Long> {
}
