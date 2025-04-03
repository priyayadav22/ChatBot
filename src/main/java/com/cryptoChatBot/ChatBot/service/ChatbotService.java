package com.cryptoChatBot.ChatBot.service;

import com.cryptoChatBot.ChatBot.response.ApiResponse;

public interface ChatbotService {
    ApiResponse getCoinDetails(String prompt) throws Exception;
    String simpleChat(String prompt);
}
