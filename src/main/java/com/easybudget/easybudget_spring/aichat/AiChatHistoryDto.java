package com.easybudget.easybudget_spring.aichat;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiChatHistoryDto {
    private List<AiChatDto> chatHistory;
    private Integer dailyRateLimit;
}
