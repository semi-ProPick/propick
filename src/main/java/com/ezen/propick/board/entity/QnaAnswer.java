package com.ezen.propick.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Data
@Table(name = "q_n_a_answer")
public class QnaAnswer {

    @Id
    @Column(name = ("q_n_a_comment_id"))
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = ("q_n_a_created_at"),nullable = false)
    @CreatedDate
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "q_n_a_comments_contents", nullable = false)
    private String answer;

    @ManyToOne
    @JoinColumn(name = "q_n_a_id")
    private QnaBoard qnaBoard;
}
