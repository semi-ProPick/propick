package com.ezen.propick.board.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
public class BoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer comment_id;

    private String comment_contents;

    private LocalDateTime comment_created_at = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private UserPostBoard board;

    @ManyToOne
    @JoinColumn(name = "f_n_a_id", nullable = false)
    private FnaBoard fnaBoard;

    @ManyToOne
    @JoinColumn(name = "q_n_a_id", nullable = false)
    private QnaBoard qnaBoard;

    @ManyToOne
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

//    @ManyToOne
//    private List<User> users = new ArrayList<>();

}
