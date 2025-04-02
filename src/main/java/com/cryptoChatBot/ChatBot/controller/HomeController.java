package com.cryptoChatBot.ChatBot.controller;

import com.cryptoChatBot.ChatBot.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public ResponseEntity<ApiResponse>Home(){
        ApiResponse response= new ApiResponse();
        response.setMessage("Welcome Guyz");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
