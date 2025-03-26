package com.ezen.propick.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bookmarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // bookmarkId → id

    @Column(nullable = false, length = 255)
    private String title; // bookmarkTitle → title

    @Column(nullable = false, length = 255)
    private String url; // bookmarkUrl → url

    @Column(nullable = false)
    private String category; // bookmarkCategory → category

    @Column(nullable = false)
    private String createdBy; // bookmarkCreatedBy → createdBy

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // bookmarkCreatedAt → createdAt

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}