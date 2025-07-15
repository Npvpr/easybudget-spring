package com.easybudget.easybudget_spring.embedding;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryEmbeddingRepository extends JpaRepository<EntryEmbedding, Long> {
    @Query(value = """
        SELECT en.id
        FROM entry_embeddings ee
        JOIN entries en ON ee.entry_id = en.id
        WHERE en.user_id = :userId
        ORDER BY ee.embedding <-> CAST(:promptEmbedding AS vector)
        LIMIT :topK
        """, nativeQuery = true)
    List<Long> findTopKRelevantEntries(@Param("promptEmbedding") float[] promptEmbedding, 
                                    @Param("userId") Long userId,
                                    @Param("topK") int topK);
}
