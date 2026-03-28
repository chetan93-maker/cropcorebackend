package com.crop.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
public class ChatHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerEntity farmer;

    @Column(nullable = false, length = 500)
    private String userMessage;

    @Column(nullable = false, length = 2000)
    private String botResponse;

    private String intent; // What type of question (crop, fertilizer, weather, etc.)
    
    private String context; // Additional context (weather, market prices, etc.)

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private Integer rating; // User feedback (1-5)
    private String feedback;

    // Constructors
    public ChatHistoryEntity() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatHistoryEntity(FarmerEntity farmer, String userMessage, String botResponse) {
        this.farmer = farmer;
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FarmerEntity getFarmer() {
        return farmer;
    }

    public void setFarmer(FarmerEntity farmer) {
        this.farmer = farmer;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getBotResponse() {
        return botResponse;
    }

    public void setBotResponse(String botResponse) {
        this.botResponse = botResponse;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}