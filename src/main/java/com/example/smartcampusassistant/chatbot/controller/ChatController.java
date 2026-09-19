package com.example.smartcampusassistant.chatbot.controller;

import com.example.smartcampusassistant.chatbot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Send a message to the chatbot and get a response
     */
    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String userId = request.get("userId");

        // Validate input
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Question cannot be empty"
            ));
        }

        try {
            String answer = chatService.getResponse(question, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("answer", answer);
            response.put("question", question);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to get response: " + e.getMessage()
            ));
        }
    }

    /**
     * Get conversation history for a user
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getConversationHistory(@PathVariable String userId) {
        try {
            List<Map<String, String>> history = chatService.getConversationHistory(userId);
            return ResponseEntity.ok(Map.of(
                    "history", history,
                    "count", history.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to get history: " + e.getMessage()
            ));
        }
    }

    /**
     * Clear conversation history for a user
     */
    @DeleteMapping("/history/{userId}")
    public ResponseEntity<?> clearConversationHistory(@PathVariable String userId) {
        try {
            chatService.clearConversationHistory(userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Conversation history cleared successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to clear history: " + e.getMessage()
            ));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "Chat service is running",
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Get all available topics the chatbot can help with
     */
    @GetMapping("/topics")
    public ResponseEntity<?> getTopics() {
        Map<String, String> topics = new HashMap<>();
        topics.put("attendance", "📊 Check and track attendance");
        topics.put("timetable", "📅 View class schedules");
        topics.put("library", "📚 Reserve books and check availability");
        topics.put("complaint", "📋 Raise and track complaints");
        topics.put("cafeteria", "🍽️ Browse restaurant menus");
        topics.put("hostel", "🏠 Manage room and maintenance");
        topics.put("feedback", "📝 Submit feedback");
        topics.put("placement", "💼 Apply to job drives");
        topics.put("lost", "🔍 Report and claim lost items");
        topics.put("transport", "🚌 Book bus seats");
        topics.put("events", "📅 View and register for events");
        topics.put("dashboard", "📊 Overview of all features");

        return ResponseEntity.ok(topics);
    }
}