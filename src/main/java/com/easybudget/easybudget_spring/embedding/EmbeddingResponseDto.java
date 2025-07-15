package com.easybudget.easybudget_spring.embedding;

import java.util.List;

import lombok.Data;

@Data
public class EmbeddingResponseDto {
    private Embedding embedding;

    @Data
    public static class Embedding {
        private List<Double> values;
        // Getters and Setters
    }

    // Getters and Setters
}
