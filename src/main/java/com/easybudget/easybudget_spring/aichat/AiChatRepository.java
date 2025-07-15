package com.easybudget.easybudget_spring.aichat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easybudget.easybudget_spring.user.User;

public interface AiChatRepository extends JpaRepository<AiChat, Long> {
    List<AiChat> findAllByUser(User user);
}
