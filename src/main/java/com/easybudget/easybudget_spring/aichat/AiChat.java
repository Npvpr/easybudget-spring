package com.easybudget.easybudget_spring.aichat;

import java.time.LocalDateTime;

import com.easybudget.easybudget_spring.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Prompt cannot be null.")
    private String prompt;

    @NotNull(message = "Response cannot be null.")
    private String response;

    @NotNull(message = "CreatedAt cannot be null.")
    private LocalDateTime createdAt;

}
