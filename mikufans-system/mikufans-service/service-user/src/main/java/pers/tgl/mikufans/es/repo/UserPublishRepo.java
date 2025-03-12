package pers.tgl.mikufans.es.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import pers.tgl.mikufans.es.UserPublishDoc;

@Repository
public interface UserPublishRepo extends ElasticsearchRepository<UserPublishDoc, Long> {
}
