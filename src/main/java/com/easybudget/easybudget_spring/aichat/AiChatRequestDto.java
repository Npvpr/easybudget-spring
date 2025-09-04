package com.easybudget.easybudget_spring.aichat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequestDto {
    @NotBlank(message = "Message cannot be blank.")
    @Size(min = 1, max = 250, message = "Message must be between 1 and 500 characters.")
    private String message;
}
