package com.cryptoChatBot.ChatBot.controller;

import com.cryptoChatBot.ChatBot.dto.prompt;
import com.cryptoChatBot.ChatBot.response.ApiResponse;
import com.cryptoChatBot.ChatBot.service.ChatbotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/chat")
public class ChatBotController {

    private final ChatbotService chatbotService;

    public ChatBotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> getCoinDetails(@RequestBody prompt promptBody) throws Exception {

        chatbotService.getCoinDetails(promptBody.getPromptBody());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(promptBody.getPromptBody());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
