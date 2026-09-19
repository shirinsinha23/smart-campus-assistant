package com.example.smartcampusassistant.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    @Autowired(required = false)
    private GeminiService geminiService;

    private final Map<String, List<Map<String, String>>> conversationHistory = new ConcurrentHashMap<>();
    private final Map<String, String> keywordResponses = new LinkedHashMap<>();

    public ChatService() {
        initializeResponses();
    }

    private void initializeResponses() {
        // Attendance
        keywordResponses.put("attendance",
                "📊 **Attendance Tracking**\n\n" +
                        "• You can check your attendance in the **Attendance** tab.\n" +
                        "• View subject-wise attendance percentage.\n" +
                        "• Track your overall attendance record.\n" +
                        "• Faculty can mark attendance for their classes.\n" +
                        "• Admin can view all attendance reports.\n\n" +
                        "💡 **Tip:** Maintain at least 75% attendance for good standing.\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Attendance in the sidebar\n" +
                        "• View your attendance summary\n" +
                        "• Check subject-wise records");

        // Timetable
        keywordResponses.put("timetable",
                "📅 **Timetable & Schedule**\n\n" +
                        "• View your class schedule in the **Timetable** tab.\n" +
                        "• See subject, faculty, room number, and time.\n" +
                        "• Check today's classes at a glance.\n" +
                        "• Faculty and Admin can manage timetable slots.\n\n" +
                        "💡 **Tip:** Timetable updates are reflected in real-time.\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Timetable in the sidebar\n" +
                        "• View your weekly schedule\n" +
                        "• Check today's classes");

        // Library
        keywordResponses.put("library",
                "📚 **Library Services**\n\n" +
                        "• Search for books by title, author, or ISBN.\n" +
                        "• Check book availability and location.\n" +
                        "• Reserve books online.\n" +
                        "• View your borrowing history.\n" +
                        "• Track due dates and returns.\n\n" +
                        "💡 **Tip:** Books can be reserved for up to 7 days.\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Library in the sidebar\n" +
                        "• Search for books\n" +
                        "• Reserve available books");

        // Complaints
        keywordResponses.put("complaint",
                "📋 **Complaint Management**\n\n" +
                        "• Raise complaints in the **My Complaints** tab.\n" +
                        "• Categories: Hostel, Library, Classroom, Internet, Transport, Cafeteria.\n" +
                        "• Track complaint status (Pending, In Progress, Resolved).\n" +
                        "• Add comments to your complaints.\n" +
                        "• Admin/Faculty can update complaint status.\n\n" +
                        "💡 **Tip:** High priority complaints are addressed first.\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Complaints in the sidebar\n" +
                        "• Raise a new complaint\n" +
                        "• Track your existing complaints");

        // Cafeteria
        keywordResponses.put("cafeteria",
                "🍽️ **Cafeteria & Dining**\n\n" +
                        "• Browse menus from 8 restaurants.\n" +
                        "• Cuisines: South Indian, North Indian, Chinese, Street Food, Continental, Bakery, Beverages.\n" +
                        "• Search for specific menu items.\n" +
                        "• Filter by cuisine type.\n" +
                        "• View item details: price, description, prep time.\n\n" +
                        "💡 **Tip:** Try the popular items like Masala Dosa, Butter Chicken, and Mango Lassi!\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Cafeteria in the sidebar\n" +
                        "• Browse restaurant menus\n" +
                        "• Search for your favorite dishes");

        // Hostel
        keywordResponses.put("hostel",
                "🏠 **Hostel Management**\n\n" +
                        "• View your room and hostel details.\n" +
                        "• Check mess menu for the week.\n" +
                        "• Raise maintenance requests.\n" +
                        "• View hostel rules and regulations.\n" +
                        "• Contact warden information.\n\n" +
                        "💡 **Tip:** Maintenance requests are typically resolved within 24-48 hours.\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Hostel in the sidebar\n" +
                        "• View your room details\n" +
                        "• Raise a maintenance request");

        // Feedback
        keywordResponses.put("feedback",
                "📝 **Feedback System**\n\n" +
                        "• Submit feedback about faculty, courses, infrastructure, cafeteria, and hostel.\n" +
                        "• Rate on a scale of 1-5.\n" +
                        "• Submit anonymously if preferred.\n" +
                        "• View feedback analytics and stats.\n\n" +
                        "💡 **Tip:** Your feedback helps improve campus services!\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Feedback in the sidebar\n" +
                        "• Submit your feedback\n" +
                        "• View feedback statistics");

        // Placement
        keywordResponses.put("placement",
                "💼 **Placement Cell**\n\n" +
                        "• View upcoming placement drives.\n" +
                        "• Apply to companies and roles.\n" +
                        "• Track application status.\n" +
                        "• View interview schedules.\n" +
                        "• Check placement statistics.\n\n" +
                        "💡 **Tip:** Apply early to maximize your chances!\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Placement in the sidebar\n" +
                        "• View active drives\n" +
                        "• Apply to your dream companies");

        // Lost & Found
        keywordResponses.put("lost",
                "🔍 **Lost & Found**\n\n" +
                        "• Report lost items with description and location.\n" +
                        "• Claim found items.\n" +
                        "• Upload images of lost/found items.\n" +
                        "• View all reported items.\n" +
                        "• Find matches for your lost items.\n\n" +
                        "💡 **Tip:** Add clear images and detailed descriptions for better matches.\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Lost & Found in the sidebar\n" +
                        "• Report a lost item\n" +
                        "• Claim a found item");

        // Transport
        keywordResponses.put("transport",
                "🚌 **Transport & Bus Booking**\n\n" +
                        "• View bus routes and schedules.\n" +
                        "• Book bus seats online.\n" +
                        "• View your bookings.\n" +
                        "• Cancel bookings if needed.\n" +
                        "• Track bus locations (coming soon).\n\n" +
                        "💡 **Tip:** Book your seat at least 1 day in advance.\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Transport in the sidebar\n" +
                        "• View available buses\n" +
                        "• Book your seat");

        // Events
        keywordResponses.put("events",
                "📅 **Events & Activities**\n\n" +
                        "• View upcoming campus events.\n" +
                        "• Register for events.\n" +
                        "• Check event details: date, time, venue, capacity.\n" +
                        "• View club events and activities.\n\n" +
                        "💡 **Tip:** Some events have limited capacity, register early!\n\n" +
                        "📌 **Quick Actions:**\n" +
                        "• Click on Events in the sidebar\n" +
                        "• Browse upcoming events\n" +
                        "• Register for events");

        // Dashboard
        keywordResponses.put("dashboard",
                "📊 **Dashboard Overview**\n\n" +
                        "• Your personalized dashboard shows:\n" +
                        "  - Attendance percentage\n" +
                        "  - Recent activity\n" +
                        "  - Upcoming classes\n" +
                        "  - Notifications\n" +
                        "  - Quick access to all features\n\n" +
                        "💡 **Tip:** Use the sidebar to navigate to any section.");

        // Help
        keywordResponses.put("help", getHelpResponse());
    }

    private String getHelpResponse() {
        return "🤖 **Smart Campus Assistant Features**\n\n" +
                "I can help you with:\n\n" +
                "1️⃣ **📊 Attendance**\n" +
                "   • Check your attendance\n" +
                "   • View subject-wise records\n" +
                "   • Track overall percentage\n\n" +
                "2️⃣ **📅 Timetable**\n" +
                "   • View class schedule\n" +
                "   • Check faculty details\n" +
                "   • See room and block info\n\n" +
                "3️⃣ **📚 Library**\n" +
                "   • Search for books\n" +
                "   • Reserve books\n" +
                "   • Check availability\n\n" +
                "4️⃣ **📋 Complaints**\n" +
                "   • Raise complaints\n" +
                "   • Track status\n" +
                "   • Add comments\n\n" +
                "5️⃣ **🍽️ Cafeteria**\n" +
                "   • Browse menus\n" +
                "   • Search for items\n" +
                "   • Filter by cuisine\n\n" +
                "6️⃣ **🏠 Hostel**\n" +
                "   • View room details\n" +
                "   • Check mess menu\n" +
                "   • Raise maintenance\n\n" +
                "7️⃣ **📝 Feedback**\n" +
                "   • Submit feedback\n" +
                "   • Rate services\n" +
                "   • Anonymous option\n\n" +
                "8️⃣ **💼 Placement**\n" +
                "   • View drives\n" +
                "   • Apply to jobs\n" +
                "   • Check status\n\n" +
                "9️⃣ **🔍 Lost & Found**\n" +
                "   • Report items\n" +
                "   • Claim found items\n" +
                "   • Find matches\n\n" +
                "🔟 **🚌 Transport**\n" +
                "   • View routes\n" +
                "   • Book seats\n" +
                "   • Track bookings\n\n" +
                "💡 **Just ask me about any of these topics!**";
    }

    public String getResponse(String question, String userId) {
        if (question == null || question.trim().isEmpty()) {
            return "🤔 Please ask a question so I can help you!";
        }

        // Try Gemini first if available
        if (geminiService != null) {
            try {
                String context = getConversationContext(userId);
                String geminiResponse = geminiService.getGeminiResponse(question, context);
                if (geminiResponse != null && !geminiResponse.isEmpty()) {
                    saveConversation(userId, question, geminiResponse);
                    return geminiResponse;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Gemini API failed, falling back to local responses: " + e.getMessage());
            }
        }

        // Fallback to local responses
        return getLocalResponse(question, userId);
    }

    private String getLocalResponse(String question, String userId) {
        String normalizedQuestion = question.toLowerCase().trim();

        // Check for greetings
        if (normalizedQuestion.matches("^(hi|hello|hey|good morning|good afternoon|good evening|namaste|hola|yo|sup|howdy).*")) {
            String response = "👋 Hello! I'm your Smart Campus Assistant. How can I help you today?\n\n" +
                    "💡 **Try asking about:**\n" +
                    "• 📊 Attendance\n" +
                    "• 📅 Timetable\n" +
                    "• 📚 Library\n" +
                    "• 📋 Complaints\n" +
                    "• 🍽️ Cafeteria\n" +
                    "• 🏠 Hostel\n" +
                    "• 📝 Feedback\n" +
                    "• 💼 Placement\n" +
                    "• 🔍 Lost & Found\n" +
                    "• 🚌 Transport";
            saveConversation(userId, question, response);
            return response;
        }

        // Check for thank you
        if (normalizedQuestion.matches(".*(thank|thanks|thx|ty|tysm|thank you|appreciate).*")) {
            String response = "😊 You're welcome! Is there anything else I can help you with?\n\n" +
                    "💡 **Feel free to ask about any Smart Campus feature!**";
            saveConversation(userId, question, response);
            return response;
        }

        // Check for help
        if (normalizedQuestion.matches(".*(help|what can you do|features|capabilities|list).*")) {
            String response = getHelpResponse();
            saveConversation(userId, question, response);
            return response;
        }

        // Check for specific keywords
        for (Map.Entry<String, String> entry : keywordResponses.entrySet()) {
            if (normalizedQuestion.contains(entry.getKey())) {
                String response = entry.getValue();
                saveConversation(userId, question, response);
                return response;
            }
        }

        // If no specific keyword matches, return a helpful response
        String fallbackResponse = "🤖 I'm here to help you with Smart Campus!\n\n" +
                "📚 **Topics I can assist with:**\n\n" +
                "• 📊 **Attendance** - Check your attendance\n" +
                "• 📅 **Timetable** - View your class schedule\n" +
                "• 📚 **Library** - Reserve books and check availability\n" +
                "• 📋 **Complaints** - Raise and track complaints\n" +
                "• 🍽️ **Cafeteria** - Browse restaurant menus\n" +
                "• 🏠 **Hostel** - Manage room and maintenance\n" +
                "• 📝 **Feedback** - Submit feedback\n" +
                "• 💼 **Placement** - Apply to job drives\n" +
                "• 🔍 **Lost & Found** - Report and claim items\n" +
                "• 🚌 **Transport** - Book bus seats\n\n" +
                "💡 **Try asking about any of these topics!**";

        saveConversation(userId, question, fallbackResponse);
        return fallbackResponse;
    }

    private String getConversationContext(String userId) {
        List<Map<String, String>> history = getConversationHistory(userId);
        if (history.isEmpty()) {
            return "No previous conversation.";
        }
        StringBuilder context = new StringBuilder("Previous conversation (last 3 exchanges):\n");
        int recentCount = Math.min(3, history.size());
        for (int i = history.size() - recentCount; i < history.size(); i++) {
            Map<String, String> entry = history.get(i);
            context.append("User: ").append(entry.get("question")).append("\n");
            context.append("Assistant: ").append(entry.get("response")).append("\n");
        }
        return context.toString();
    }

    private void saveConversation(String userId, String question, String response) {
        if (userId == null || userId.isEmpty()) {
            userId = "anonymous";
        }

        conversationHistory.computeIfAbsent(userId, k -> new ArrayList<>());
        List<Map<String, String>> history = conversationHistory.get(userId);

        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("question", question);
        entry.put("response", response);
        entry.put("timestamp", LocalDateTime.now().toString());

        history.add(entry);

        // Keep only last 50 conversations per user
        if (history.size() > 50) {
            history.remove(0);
        }
    }

    public List<Map<String, String>> getConversationHistory(String userId) {
        if (userId == null || userId.isEmpty()) {
            userId = "anonymous";
        }
        return conversationHistory.getOrDefault(userId, new ArrayList<>());
    }

    public void clearConversationHistory(String userId) {
        if (userId == null || userId.isEmpty()) {
            userId = "anonymous";
        }
        conversationHistory.remove(userId);
    }
}