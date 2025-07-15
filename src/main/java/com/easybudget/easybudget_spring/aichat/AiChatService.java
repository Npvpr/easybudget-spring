package com.easybudget.easybudget_spring.aichat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.embedding.EmbeddingService;

@Service
public class AiChatService {

    @Autowired
    private EmbeddingService embeddingService;

    private final ChatClient chatClient;

    public AiChatService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1-nano-2025-04-14")
                        .temperature(0.5)
                        .maxTokens(200)
                        .build())
                .build();
    }

    public AiChatResponseDto chat(AiChatRequestDto request) {

        String systemInstructions = """
                - You are a Certified Financial Analyst AI. 
                - Be friendly and polite. 
                - Talk naturally.
                - The entries provided are the most relevant 5 entries. 
                - There might be other entries.
                - Don't repeat same things again and again. 
                - Be concise. 
                
                Your rules:

                **CORE PRINCIPLES**
                1. Financial Terms:
                   - INCOME: Money received (salary, gifts)
                   - EXPENSE: Money spent (purchases, bills)
                   - NET = INCOME - EXPENSES

                2. Contextual Analysis:
                   - If asked about "spending/expenses", ONLY consider OUTCOME entries
                   - If asked about "income/earnings", ONLY consider INCOME entries
                   - For "savings" or "balance", compare TOTAL INCOME vs TOTAL EXPENSES

                **RESPONSE PROTOCOL**
                - Phase 1: Identify query type (spending/income/savings)
                - Phase 2: Calculate relevant metrics
                - Phase 3: Format response as:
                    [Summary]
                    • Key Insight 1
                    • Key Insight 2
                    [Advice] (if applicable)

                Current user's relevant financial entries:
                """ + String.join(",\n", embeddingService.getTopKRelevantEntries(request.getMessage(), 5));

        System.out.println("System Instructions: " + systemInstructions);

        String reply = chatClient
                .prompt()
                .system(systemInstructions)
                .user(request.getMessage())
                .call()
                .content();

        return new AiChatResponseDto(reply);
    }

}
