package com.easybudget.easybudget_spring.embedding;

import com.easybudget.easybudget_spring.entry.Entry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entry_embeddings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntryEmbedding {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private Entry entry;

    @Column(columnDefinition = "vector(1538)", name = "embedding", nullable = false)
    private float[] embedding;
}
