package com.example.smartcampusassistant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String loginId, String otp, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🔐 Smart Campus - Your OTP for Login");
        message.setText(String.format(
                "Dear %s,\n\n" +
                        "Your One-Time Password (OTP) for login is:\n\n" +
                        "🔑 Login ID: %s\n" +
                        "🔐 OTP: %s\n\n" +
                        "This OTP is valid for 24 hours.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Smart Campus Assistant Team",
                name, loginId, otp
        ));
        mailSender.send(message);
        System.out.println("✅ OTP email sent to: " + to + " for Login ID: " + loginId);
    }

    public void sendWelcomeEmail(String to, String name, String loginId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🎓 Welcome to Smart Campus Assistant!");
        message.setText(String.format(
                "Dear %s,\n\n" +
                        "Welcome to Smart Campus Assistant!\n\n" +
                        "Your account has been successfully created.\n\n" +
                        "📋 Login ID: %s\n" +
                        "📧 Email: %s\n\n" +
                        "To login:\n" +
                        "1. Enter your Login ID\n" +
                        "2. Request OTP\n" +
                        "3. Enter OTP to login\n\n" +
                        "You can now access all campus features including:\n" +
                        "• View Timetable\n" +
                        "• Mark Attendance\n" +
                        "• Check Reports\n\n" +
                        "Best regards,\n" +
                        "Smart Campus Assistant Team",
                name, loginId, to
        ));
        mailSender.send(message);
        System.out.println("✅ Welcome email sent to: " + to);
    }
}