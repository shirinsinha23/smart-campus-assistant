package com.example.smartcampusassistant.chatbot.repository;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChatRepository {

    // In-memory storage for chat history
    private final Map<String, List<Map<String, Object>>> chatStorage = new ConcurrentHashMap<>();

    public void saveMessage(String userId, String question, String response) {
        chatStorage.computeIfAbsent(userId, k -> new ArrayList<>());

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("question", question);
        message.put("response", response);
        message.put("timestamp", new Date().toString());

        chatStorage.get(userId).add(message);
    }

    public List<Map<String, Object>> getHistory(String userId) {
        return chatStorage.getOrDefault(userId, new ArrayList<>());
    }

    public void clearHistory(String userId) {
        chatStorage.remove(userId);
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalUsers", chatStorage.size());
        stats.put("totalMessages", chatStorage.values().stream()
                .mapToInt(List::size)
                .sum());
        return stats;
    }
}