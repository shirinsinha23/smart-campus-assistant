package com.example.smartcampusassistant.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    // Use the latest stable model for best results
    private static final String GEMINI_MODEL = "gemini-2.5-flash"; // Fast, reliable, and free tier friendly

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Get response from Gemini API for the user's question
     */
    public String getGeminiResponse(String question, String context) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            System.out.println("⚠️ Gemini API key not configured. Using fallback responses.");
            return null;
        }

        try {
            // Use the model that showed up in your API test
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key=" + apiKey;

            String prompt = buildPrompt(question, context);

            Map<String, Object> requestBody = new HashMap<>();

            // Build the content
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", new Object[]{part});
            requestBody.put("contents", new Object[]{content});

            // Add generation config for better responses
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 1024);
            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("📤 Sending request to Gemini API (model: " + GEMINI_MODEL + ")");
            System.out.println("📝 Question: " + question);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getBody() != null && response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> result = response.getBody();

                List<Map<String, Object>> candidates = (List<Map<String, Object>>) result.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentResponse = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResponse.get("parts");

                    if (parts != null && !parts.isEmpty()) {
                        String text = (String) parts.get(0).get("text");
                        System.out.println("✅ Gemini response received successfully!");
                        return text;
                    }
                }
            }

            System.out.println("⚠️ Gemini API returned empty response");
            return null;

        } catch (Exception e) {
            System.err.println("❌ Gemini API error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String buildPrompt(String question, String context) {
        return """
You are a friendly and helpful Smart Campus Assistant for Sharda University.

Your name is "Smart Campus Assistant".

You help students, faculty, and staff with university-related information.

IMPORTANT RULES:
1. Keep responses brief (2-3 paragraphs max)
2. Use bullet points for lists
3. Be friendly and encouraging
4. Use appropriate emojis
5. If you don't know something, suggest asking the admin

Available Features:
- 📊 Attendance: Track and view attendance
- 📅 Timetable: View class schedules
- 📚 Library: Reserve and search books
- 📋 Complaints: Raise and track complaints
- 🍽️ Cafeteria: Browse food menus
- 🏠 Hostel: Room details and maintenance
- 📝 Feedback: Submit feedback
- 💼 Placement: Apply to job drives
- 🔍 Lost & Found: Report and claim items
- 🚌 Transport: Book bus seats
- 📅 Events: View and register

Previous conversation context:
""" + context + """

User Question: """ + question + """

Provide a helpful, warm, and practical response about this topic.

Response:""";
    }

    /**
     * Get all available Gemini models (for debugging)
     */
    public List<String> listAvailableModels() {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> models = (List<Map<String, Object>>) response.getBody().get("models");
                List<String> modelNames = new ArrayList<>();
                for (Map<String, Object> model : models) {
                    modelNames.add((String) model.get("name"));
                }
                return modelNames;
            }
        } catch (Exception e) {
            System.err.println("Error listing models: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}