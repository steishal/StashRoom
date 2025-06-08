package org.example.stashroom.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TelegramRestClient {

    private final OkHttpClient client = new OkHttpClient();

    public void sendMessage(Long chatId, String text) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("chat_id", chatId);
        jsonMap.put("text", text);

        try {
            String json = objectMapper.writeValueAsString(jsonMap);
            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url("http://localhost:8000/send-message")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to send message to Telegram API. Code: {}", response.code());
                }
            }
        } catch (IOException e) {
            log.error("Error sending message to Telegram API", e);
        }
    }
}
