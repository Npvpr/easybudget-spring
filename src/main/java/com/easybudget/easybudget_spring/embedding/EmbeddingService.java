package com.easybudget.easybudget_spring.embedding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.easybudget.easybudget_spring.entry.Entry;
import com.easybudget.easybudget_spring.entry.EntryCheckService;
import com.easybudget.easybudget_spring.exception.NotFoundException;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

@Service
public class EmbeddingService {

    @Autowired
    private EntryEmbeddingRepository entryEmbeddingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EntryCheckService entryCheckService;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    public EmbeddingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public float[] getEmbedding(String text) {
        EmbeddingRequestDto.Part part = new EmbeddingRequestDto.Part();
        part.setText(text);

        EmbeddingRequestDto.Content content = new EmbeddingRequestDto.Content();
        content.setParts(List.of(part));

        EmbeddingRequestDto requestBody = new EmbeddingRequestDto();
        requestBody.setModel("models/gemini-embedding-001");
        requestBody.setContent(content);
        requestBody.setOutputDimensionality(1538); // Set the output dimensionality as required

        EmbeddingResponseDto response = this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-embedding-001:embedContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(EmbeddingResponseDto.class)
                .block(); // Use block() for a simple synchronous call. For a reactive app, use
                          // mono.subscribe()

        List<Double> embeddingList;
        if (response == null || response.getEmbedding() == null || response.getEmbedding().getValues() == null) {
            throw new RuntimeException("Failed to retrieve embedding from Gemini API");
        } else {
            embeddingList = response.getEmbedding().getValues();
        }

        float[] embeddingArray = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            embeddingArray[i] = embeddingList.get(i).floatValue();
        }
        System.out.println("Gemini Embedding Response: " + embeddingArray);

        return embeddingArray;
    }

    public EntryEmbedding createEntryEmbedding(Entry newEntry) {

        EntryEmbedding entryEmbedding = EntryEmbedding.builder()
                .entry(newEntry)
                .embedding(getEmbedding(newEntry.toString()))
                .build();

        return entryEmbeddingRepository.save(entryEmbedding);
    }

    public EntryEmbedding updateEntryEmbedding(Entry oldEntry) {
        Long entryId = oldEntry.getId();
        EntryEmbedding existingEmbedding = entryEmbeddingRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Embedding not found with Entry ID: " + entryId));

        existingEmbedding.setEmbedding(getEmbedding(oldEntry.toString()));

        return entryEmbeddingRepository.save(existingEmbedding);
    }

    public void deleteEntryEmbedding(Long entryId) {

        entryEmbeddingRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Embedding not found with Entry ID: " + entryId));

        entryEmbeddingRepository.deleteById(entryId);
    }

    public List<String> getTopKRelevantEntries(String prompt, int topK) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Long userId = currentUser.getId();

        float[] promptEmbedding = getEmbedding(prompt);
        List<Long> entryIds = entryEmbeddingRepository.findTopKRelevantEntries(promptEmbedding, userId, topK);

        return entryIds.stream()
                .map(id -> entryCheckService.findEntryById(id).toString())
                .toList();
    }

    // just for testing
    // org.postgresql.util.PSQLException: No results were returned by the query.
    // always this error, for some reason JPA can't handle pgvector directly (I
    // might need manually fix something)
    public List<EntryEmbedding> getAllEmbeddings() {
        return entryEmbeddingRepository.findAll();
    }
}