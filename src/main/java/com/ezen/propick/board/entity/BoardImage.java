package com.ezen.propick.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer img_id;

    private String img_url;

    private String img_name;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)  // FK 설정
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
//    @JoinColumn(name = "board_id", nullable = false)
//    private UserPostBoard userPostBoard;

}
