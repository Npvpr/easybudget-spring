package com.easybudget.easybudget_spring.embedding;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {
    @Autowired
    private EmbeddingService embeddingService;

    // for testing only
    @GetMapping
    public ResponseEntity<List<EntryEmbedding>> getAllEmbeddings(){
        return new ResponseEntity<>(embeddingService.getAllEmbeddings(), HttpStatus.OK);
    }
}
