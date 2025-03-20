package com.ezen.propick.board.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Board board;

//    @ManyToOne
//    private List<User> users = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BoardType boardType;
}
