package com.cryptoChatBot.ChatBot.service;

import com.cryptoChatBot.ChatBot.dto.CoinDto;
import com.cryptoChatBot.ChatBot.response.ApiResponse;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Service
public class ChatBotServiceImpl implements ChatbotService {

    private double convertToDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
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
        String url = "https://api.coingecko.com/api/v3/coins/bitcoin";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseBody = restTemplate.getForEntity(url, Map.class);

        if (responseBody != null && responseBody.getBody() != null) {
            Map<String, Object> body = responseBody.getBody();
            Map<String, Object> image = (Map<String, Object>) body.get("image");
            Map<String, Object> marketData = (Map<String, Object>) body.get("market_data");

            if (marketData == null) {
                throw new Exception("Market data not found in API response");
            }

            CoinDto coinDto = new CoinDto();
            coinDto.setId((String) body.get("id"));
            coinDto.setName((String) body.get("name"));
            coinDto.setSymbol((String) body.get("symbol"));
            coinDto.setImage(image != null ? (String) image.get("large") : null);

            // Extracting numeric values safely
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

            return coinDto;
        }
        throw new Exception("Coin not found");
    }

    @Override
    public ApiResponse getCoinDetails(String prompt) throws Exception {
        CoinDto coinDto = makeApiRequest(prompt);
        System.out.println("Coin Details: " + coinDto);
        return null;
    }

    @Override
    public String simpleChat(String prompt) {
        String GEMINI_API_URL= "";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return "";
    }
}
