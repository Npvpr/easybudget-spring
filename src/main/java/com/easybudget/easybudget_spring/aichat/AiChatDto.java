package com.easybudget.easybudget_spring.aichat;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiChatDto {
    private String prompt;
    private String response;
    private LocalDateTime createdAt;
}
