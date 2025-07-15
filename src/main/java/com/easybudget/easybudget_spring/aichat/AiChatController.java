package com.easybudget.easybudget_spring.aichat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponseDto> chat(@Valid @RequestBody AiChatRequestDto request) {
        return new ResponseEntity<>(aiChatService.chat(request), HttpStatus.OK);
    }
}
