package com.example.ddmdemo.indexrepository;

import com.example.ddmdemo.indexmodel.DummyIndex;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyIndexRepository
    extends ElasticsearchRepository<DummyIndex, String> {

    @Query("{\"knn\": {\"field\": \"vectorizedContent\", \"query_vector\": ?0, \"k\": 10, \"num_candidates\": 100}}")
    DummyIndex searchByVector(float[] queryVector);
}
