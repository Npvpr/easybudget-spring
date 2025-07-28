package com.easybudget.easybudget_spring.aichat;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
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

        public AiChatService(ChatClient.Builder builder) {

                this.chatClient = builder
                                .defaultOptions(OpenAiChatOptions.builder()
                                                .model("gpt-4.1-nano-2025-04-14")
                                                .temperature(1.0)
                                                .maxTokens(150)
                                                .build())
                                .build();
        }

        public AiChatResponseDto chat(AiChatRequestDto request) {

                User currentUser = userService.getCurrentAuthenticatedUser();

                String instructions = """
                                - Act as Financial Advisor AI, reject non-finance questions politely
                                - Be friendly polite concise natural
                                - User's currency: %s
                                - No suggestion prompt/ folow-up questions
                                - User's 5 most relevant financial entries(There might be other entries):
                                """.formatted(currentUser.getCurrency())
                                + String.join(",\n", embeddingService.getTopKRelevantEntries(request.getMessage(), 5));

                System.out.println("System Instructions: " + instructions);

                ChatResponse response = chatClient
                                .prompt()
                                .system(instructions)
                                .user(request.getMessage())
                                .call()
                                .chatResponse();

                System.out.println("Chat Response Metadata: " + response.getMetadata());

                createChatHistory(request.getMessage(), response.getResult().getOutput().getText());
                userService.increaseDailyRateLimit();

                return AiChatResponseDto.builder()
                                .reply(response.getResult().getOutput().getText())
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
