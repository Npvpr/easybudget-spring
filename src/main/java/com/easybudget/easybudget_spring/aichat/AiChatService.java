package com.easybudget.easybudget_spring.aichat;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easybudget.easybudget_spring.embedding.EmbeddingService;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;

@Service
public class AiChatService {

        @Autowired
        private EmbeddingService embeddingService;

        @Autowired
        private UserService userService;

        @Autowired
        private AiChatRepository aiChatRepository;

        private final ChatClient chatClient;

        private final String cachedSystemMessage;

        public AiChatService(ChatClient.Builder builder) {

                this.cachedSystemMessage = """
                                Your rules:
                                **General Guidelines**
                                - You are a Certified Financial Analyst AI
                                - Be friendly and polite
                                - Talk naturally
                                - The entries provided are the most relevant 5 entries
                                - There might be other entries
                                - Don't repeat same things again and again
                                - Be concise.
                                - Very politely decline if questions are about irrelevant topics.
                                - User's currency is %s
                                - Provide currency symbols correctly
                                - Please respond in plain text format
                                **Finance Principals**
                                - Identify query type (spending/income/savings)
                                - Calculate relevant metrics
                                1. Financial Terms:
                                   - INCOME: Money received (salary, gifts)
                                   - EXPENSE: Money spent (purchases, bills)
                                   - NET = INCOME - EXPENSES
                                2. Contextual Analysis:
                                   - If asked about "spending/expenses", ONLY consider OUTCOME entries
                                   - If asked about "income/earnings", ONLY consider INCOME entries
                                   - For "savings" or "balance", compare TOTAL INCOME vs TOTAL EXPENSES
                                """;

                this.chatClient = builder
                                .defaultSystem(cachedSystemMessage)
                                .defaultOptions(OpenAiChatOptions.builder()
                                                .model("gpt-4.1-nano-2025-04-14")
                                                .temperature(1.0)
                                                .maxTokens(150)
                                                .build())
                                .build();
        }

        public AiChatResponseDto chat(AiChatRequestDto request) {

                User currentUser = userService.getCurrentAuthenticatedUser();

                String dynamicContext = """
                                Current user's relevant financial entries:
                                """
                                + String.join(",\n", embeddingService.getTopKRelevantEntries(request.getMessage(), 5));

                System.out.println("System Instructions: " + dynamicContext);

                String reply = chatClient
                                .prompt()
                                .system(dynamicContext)
                                .user(request.getMessage())
                                .call()
                                .content();

                createChatHistory(request.getMessage(), reply);
                userService.increaseDailyRateLimit();

                return AiChatResponseDto.builder()
                                .reply(reply)
                                .build();
        }

        public AiChatDto createChatHistory(String prompt, String response) {
                User currentUser = userService.getCurrentAuthenticatedUser();

                AiChat newAiChat = AiChat.builder()
                                .prompt(prompt)
                                .response(response)
                                .createdAt(java.time.LocalDateTime.now())
                                .user(currentUser)
                                .build();

                aiChatRepository.save(newAiChat);

                return AiChatDto.builder()
                                .prompt(newAiChat.getPrompt())
                                .response(newAiChat.getResponse())
                                .createdAt(newAiChat.getCreatedAt())
                                .build();
        }

        public AiChatHistoryDto getChatHistory() {

                User currentUser = userService.getCurrentAuthenticatedUser();

                return AiChatHistoryDto.builder()
                                .chatHistory(aiChatRepository.findAllByUser(currentUser).stream()
                                                .map(chat -> AiChatDto.builder()
                                                                .prompt(chat.getPrompt())
                                                                .response(chat.getResponse())
                                                                .createdAt(chat.getCreatedAt())
                                                                .build())
                                                .toList())
                                .dailyRateLimit(currentUser.getDailyRateLimit())
                                .build();
        }

        public void deleteChatHistory() {
                User currentUser = userService.getCurrentAuthenticatedUser();
                List<AiChat> chatHistory = aiChatRepository.findAllByUser(currentUser);
                aiChatRepository.deleteAll(chatHistory);
        }

}
