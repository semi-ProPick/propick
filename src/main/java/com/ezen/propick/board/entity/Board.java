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
public class Board {

    @Id
    //id값 자동 갱신
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  //자유게시판 번호PK
    @Column(nullable = false)
    private String title;  //제목
    @Column(nullable = false)
    private String contents;  //내용
    @Column(nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();  //생성일

    private LocalDateTime updated_at;  //수정일

    private boolean pin_status;
    //null값 가질 수 없음
    @Column(nullable = false)
    private String answer;

    private boolean look_status;

    private int private_key;

    //boardtype으로 board 종류 구분
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    //하나의 보드가 여러개의 댓글을 가지고 있다.
    // casecade : 저장,병합,삭세,분리 등의 작업을 하면 자동으로 전파됨,
    // orphanRemoval : 연관관계에서 제거하면 자식엔티티 자동 삭제
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> images = new ArrayList<>();

    @ManyToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();
}


