package com.ezen.propick.board.entity;


import com.ezen.propick.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "user_post_comments")
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_id")
    private Integer id;

    @Column(name = "post_comment_contents", columnDefinition = "Text", nullable = false)
    private String contents;

    @Column(name = "post_comment_created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private UserPostBoard userPostBoard;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private User user;


}

