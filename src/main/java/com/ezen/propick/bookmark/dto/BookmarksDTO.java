package com.ezen.propick.bookmark.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarksDTO {

    private Integer id;
    private String title;
    private String url;
    private String category;
    private String createdBy;
    private String createdAt; // LocalDateTime을 String으로 변환

    @Override
    public String toString() {
        return "BookmarksDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", category='" + category + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}