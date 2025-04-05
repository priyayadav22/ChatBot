package com.cryptoChatBot.ChatBot.service;

import com.cryptoChatBot.ChatBot.dto.CoinDto;
import com.cryptoChatBot.ChatBot.response.ApiResponse;
import com.cryptoChatBot.ChatBot.response.FunctionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatBotServiceImpl implements ChatbotService {

    private final String GEMINI_API_KEY = "AIzaSyAuBOWRi_hsbK9nmjVK32-Hk9iJIB8hBV8"; // Ideally from env

    private double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value: " + value);
            }
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
    }

    public CoinDto makeApiRequest(String currencyName) throws Exception {
        System.out.println("[DEBUG] Entered makeApiRequest() with currencyName: " + currencyName);

        String url = "https://api.coingecko.com/api/v3/coins/" + currencyName.toLowerCase();
        System.out.println("[DEBUG] Fetching data from CoinGecko: " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseBody = restTemplate.getForEntity(url, Map.class);

        if (responseBody != null && responseBody.getBody() != null) {
            Map<String, Object> body = responseBody.getBody();
            System.out.println("[DEBUG] CoinGecko response: " + body);

            Map<String, Object> image = (Map<String, Object>) body.get("image");
            Map<String, Object> marketData = (Map<String, Object>) body.get("market_data");

            if (marketData == null) {
                System.out.println("[ERROR] Market data not found in response");
                throw new Exception("Market data not found in API response");
            }

            CoinDto coinDto = new CoinDto();
            coinDto.setId((String) body.get("id"));
            coinDto.setName((String) body.get("name"));
            coinDto.setSymbol((String) body.get("symbol"));
            coinDto.setImage(image != null ? (String) image.get("large") : null);

            Map<String, Object> currentPriceMap = (Map<String, Object>) marketData.get("current_price");
            Map<String, Object> marketCapMap = (Map<String, Object>) marketData.get("market_cap");
            Map<String, Object> totalVolumeMap = (Map<String, Object>) marketData.get("total_volume");
            Map<String, Object> high24hMap = (Map<String, Object>) marketData.get("high_24h");
            Map<String, Object> low24hMap = (Map<String, Object>) marketData.get("low_24h");

            coinDto.setCurrentPrice(convertToDouble(currentPriceMap.get("usd")));
            coinDto.setMarketCap(convertToDouble(marketCapMap.get("usd")));
            coinDto.setMarketCapRank(convertToDouble(marketData.get("market_cap_rank")));
            coinDto.setTotalVolume(convertToDouble(totalVolumeMap.get("usd")));
            coinDto.setHigh24h(convertToDouble(high24hMap.get("usd")));
            coinDto.setLow24h(convertToDouble(low24hMap.get("usd")));
            coinDto.setPriceChange24h(convertToDouble(marketData.get("price_change_24h")));
            coinDto.setPriceChangePercentage24h(convertToDouble(marketData.get("price_change_percentage_24h")));
            coinDto.setMarketCapChangePercentage24h(convertToDouble(marketData.get("market_cap_change_percentage_24h")));
            coinDto.setCirculatingSupply(convertToDouble(marketData.get("circulating_supply")));
            coinDto.setTotalSupply(convertToDouble(marketData.get("total_supply")));

            System.out.println("[DEBUG] Parsed CoinDto: " + coinDto);
            return coinDto;
        }

        System.out.println("[ERROR] Coin not found or response body is null");
        throw new Exception("Coin not found");
    }

    public FunctionResponse getFunctionRespone(String promptBody) {
        System.out.println("[DEBUG] Entered getFunctionRespone() with prompt: " + promptBody);

        String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;
        System.out.println("[DEBUG] Gemini API URL: " + GEMINI_API_URL);

        JSONObject requestBodyJson = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("text", promptBody)
                                        )
                                )
                        )
                )
                .put("tools", new JSONArray()
                        .put(new JSONObject()
                                .put("functionDeclarations", new JSONArray()
                                        .put(new JSONObject()
                                                .put("name", "getCoinDetails")
                                                .put("description", "Get coin Details from given currency object")
                                                .put("parameters", new JSONObject()
                                                        .put("type", "object")
                                                        .put("properties", new JSONObject()
                                                                .put("currencyName", new JSONObject()
                                                                        .put("type", "string")
                                                                        .put("description", "The currency name, id, symbol."))
                                                                .put("currencyDate", new JSONObject()
                                                                        .put("type", "string")
                                                                        .put("description", "Currency date id, symbol. "))
                                                                .put("currencyData", new JSONObject()
                                                                        .put("type", "string")
                                                                        .put("description",  "Currency Data details..."))
                                                        )
                                                        .put("required", new JSONArray()
                                                                .put("currencyName")
                                                                .put("currencyData")
                                                        )
                                                )
                                        )
                                )
                        )
                );

        System.out.println("[DEBUG] Gemini Request Body: " + requestBodyJson.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson.toString(), headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
            String responseBody = response.getBody();

            System.out.println("[DEBUG] Gemini Response Status: " + response.getStatusCode());
            System.out.println("[DEBUG] Gemini Response Body: " + responseBody);
        } catch (Exception e) {
            System.out.println("[ERROR] Error while calling Gemini API: " + e.getMessage());
        }

        System.out.println("[DEBUG] Exiting getFunctionRespone()");
        return null;
    }

    @Override
    public ApiResponse getCoinDetails(String promptBody) throws Exception {
        System.out.println("[DEBUG] Entered getCoinDetails() with promptBody: " + promptBody);

        CoinDto coinDto = makeApiRequest(promptBody);
        System.out.println("[DEBUG] CoinDto returned from makeApiRequest(): " + coinDto);

        getFunctionRespone(promptBody);
        System.out.println("[DEBUG] Exiting getCoinDetails()");
        return null;
    }

    @Override
    public String simpleChat(String promptBody) {
        System.out.println("[DEBUG] Entered simpleChat() with promptBody: " + promptBody);

        String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBodyJson = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject().put("text", promptBody)))));

        System.out.println("[DEBUG] simpleChat Gemini Request Body: " + requestBodyJson.toString());

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson.toString(), httpHeaders);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
            System.out.println("[DEBUG] simpleChat Gemini Response Status: " + response.getStatusCode());
            System.out.println("[DEBUG] simpleChat Gemini Response Body: " + response.getBody());
            System.out.println("[DEBUG] Exiting simpleChat()");
            return response.getBody();
        } catch (Exception e) {
            System.out.println("[ERROR] Error in simpleChat(): " + e.getMessage());
            return null;
        }
    }
}
