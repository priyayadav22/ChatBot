package com.cryptoChatBot.ChatBot.service;

import com.cryptoChatBot.ChatBot.dto.CoinDto;
import com.cryptoChatBot.ChatBot.response.ApiResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Service
public class ChatBotServiceImpl implements ChatbotService{

    private double convertToDouble(Object value){
        if(value instanceof Integer){
            return ((Integer)value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long)value).doubleValue();
        } else if (value instanceof Double) {
            return (Double)value;
        }
        else throw new IllegalArgumentException("unsupported type" + value.getClass().getName());
    }

    public CoinDto makeApiRequest(String currencyName) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/bitcoin";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseBody = restTemplate.getForEntity(url, Map.class);
        if(responseBody!=null){
            Map<String, Object> image = (Map<String, Object>)responseBody.getBody().get("image");
            Map<String, Object> marketData = (Map<String, Object>)responseBody.getBody().get("market_data");
            CoinDto coinDto = new CoinDto();
            coinDto.setId((String) responseBody.getBody().get("id"));
            coinDto.setName((String) responseBody.getBody().get("name"));
            coinDto.setSymbol((String) responseBody.getBody().get("symbol"));
            coinDto.setImage((String) responseBody.getBody().get("large"));

            coinDto.setCurrentPrice(convertToDouble(((Map<String,Object>) marketData.get("current_price")).get("usd")));
            coinDto.setMarketCap(convertToDouble(((Map<String,Object>) marketData.get("market_cap")).get("usd")));
            coinDto.setMarketCapRank(convertToDouble(((Map<String,Object>) marketData.get("market_cap_rank")).get("usd")));
            coinDto.setTotalVolume(convertToDouble(((Map<String,Object>) marketData.get("total_volume")).get("usd")));
            coinDto.setHigh24h(convertToDouble(((Map<String,Object>) marketData.get("high_24")).get("usd")));
            coinDto.setLow24h(convertToDouble(((Map<String,Object>) marketData.get("low_24")).get("usd")));
            coinDto.setPriceChange24h(convertToDouble(((Map<String,Object>) marketData.get("price_change_24"))));
            coinDto.setPriceChangePercentage24h(convertToDouble(((Map<String,Object>) marketData.get("current_price"))));
            coinDto.setMarketCapChangePercentage24h(convertToDouble(((Map<String,Object>) marketData.get("market_cap_change_percentage_24"))));
            coinDto.setCirculatingSupply(convertToDouble(((Map<String,Object>) marketData.get("circulating_supply"))));
            coinDto.setTotalSupply(convertToDouble(((Map<String,Object>) marketData.get("total_supply"))));

        return coinDto;
        }
        throw new Exception("coin not found");
    }

    @Override
    public String simpleChat(String prompt) {
        return "";
    }

    @Override
    public ApiResponse getCoinDetails(String prompt){
        return null;
    }
}
