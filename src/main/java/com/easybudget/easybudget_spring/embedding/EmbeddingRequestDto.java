package com.easybudget.easybudget_spring.embedding;

import java.util.List;

import lombok.Data;

@Data
public class EmbeddingRequestDto {
    private String model;
    private Content content;
    private Integer outputDimensionality;

    @Data
    public static class Content {
        private List<Part> parts;
        // Getters and Setters
    }

    @Data
    public static class Part {
        private String text;
        // Getters and Setters
    }

    // Getters and Setters
}