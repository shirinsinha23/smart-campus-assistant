package com.example.smartcampusassistant.lostandfound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private Long itemId;
    private String itemTitle;
    private List<MatchedItem> matches;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchedItem {
        private Long id;
        private String title;
        private String category;
        private String location;
        private String reportedByName;
        private String contactInfo;
        private int matchScore;
    }
}