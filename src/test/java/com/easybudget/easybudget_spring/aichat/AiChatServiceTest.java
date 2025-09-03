package com.easybudget.easybudget_spring.aichat;

import com.easybudget.easybudget_spring.embedding.EmbeddingService;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AiChatServiceTest {

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private UserService userService;

    @Mock
    private AiChatRepository aiChatRepository;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient.ChatClientRequestSpec chatClientRequestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private AiChatService aiChatService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .currency("USD")
                .dailyRateLimit(0)
                .build();

        // Mock the builder chain
        when(chatClientBuilder.defaultOptions(any(OpenAiChatOptions.class)))
                .thenReturn(chatClientBuilder);
        when(chatClientBuilder.build())
                .thenReturn(chatClient);

        // Create the service instance
        aiChatService = new AiChatService(chatClientBuilder);
        
        // Inject dependencies
        ReflectionTestUtils.setField(aiChatService, "embeddingService", embeddingService);
        ReflectionTestUtils.setField(aiChatService, "userService", userService);
        ReflectionTestUtils.setField(aiChatService, "aiChatRepository", aiChatRepository);

        // Mock the ChatClient fluent API chain
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
    }

    @Test
    void testChat_ShouldReturnAiResponseAndSaveHistory() {
        AiChatRequestDto request = AiChatRequestDto.builder()
                .message("How should I save money?")
                .build();

        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);
        when(embeddingService.getTopKRelevantEntries(anyString(), eq(5)))
                .thenReturn(List.of("Entry1", "Entry2"));

        // Mock ChatResponse
        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage("You should budget 20% of income.");
        
        when(mockGeneration.getOutput()).thenReturn(assistantMessage);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(callResponseSpec.chatResponse()).thenReturn(mockResponse);

        when(aiChatRepository.save(any(AiChat.class))).thenAnswer(inv -> inv.getArgument(0));

        AiChatResponseDto result = aiChatService.chat(request);

        assertEquals("You should budget 20% of income.", result.getReply());
        verify(userService, times(1)).increaseDailyRateLimit();
        verify(aiChatRepository, times(1)).save(any(AiChat.class));
    }

    @Test
    void testGetChatHistory_ShouldReturnUserHistory() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        AiChat chat = AiChat.builder()
                .prompt("Test prompt")
                .response("Test response")
                .createdAt(java.time.LocalDateTime.now())
                .user(mockUser)
                .build();

        when(aiChatRepository.findAllByUser(mockUser)).thenReturn(List.of(chat));

        AiChatHistoryDto historyDto = aiChatService.getChatHistory();

        assertEquals(1, historyDto.getChatHistory().size());
        assertEquals("Test prompt", historyDto.getChatHistory().get(0).getPrompt());
        assertEquals("Test response", historyDto.getChatHistory().get(0).getResponse());
        assertEquals(0, historyDto.getDailyRateLimit());
    }

    @Test
    void testDeleteChatHistory_ShouldRemoveChats() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        AiChat chat = AiChat.builder()
                .prompt("Test prompt")
                .response("Test response")
                .createdAt(java.time.LocalDateTime.now())
                .user(mockUser)
                .build();

        when(aiChatRepository.findAllByUser(mockUser)).thenReturn(List.of(chat));

        aiChatService.deleteChatHistory();

        verify(aiChatRepository, times(1)).deleteAll(List.of(chat));
    }
}