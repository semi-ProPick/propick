package com.ezen.propick.board.entity;

import com.ezen.propick.user.entity.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@NoArgsConstructor
@Entity
@Data
@Table(name = "user_post_board")
public class UserPostBoard {

    @Id
    //id값 자동 갱신
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer id;  //자유게시판 번호PK

    @Column(name = ("post_title"), nullable = false)
    private String title;  //제목

    @Column(name = ("post_contents"), nullable = false)
    private String contents;  //내용

    @Column(name = ("post_created_at"), nullable = false)
    @CreatedDate
    private LocalDateTime created_at = LocalDateTime.now();  //생성일

    @Column(name = ("post_updated_at"))
    private LocalDateTime updated_at;  //수정일

    @Column(name = ("post_countview"), columnDefinition = "int default 0", nullable = false)
    private int countview = 0;  //조회수

    @Column(name = ("post_filename"))
    private String filename;

    @Column(name = ("post_filepath"))
    private String filepath;

    @PreUpdate
    public void onPreUpdate() {
        this.updated_at = LocalDateTime.now();  // updated_at 필드를 현재 시간으로 설정
    }

    @PrePersist
    public void setDefaultPostContents() {
        if (this.contents == null) {
            this.contents = "";  // 기본값 설정
        }
        if (this.title == null) {
            this.title = "";  // 기본값 설정
        }
    }

    //하나의 보드가 여러개의 댓글을 가지고 있다.
    // casecade : 저장,병합,삭세,분리 등의 작업을 하면 자동으로 전파됨,
    // orphanRemoval : 연관관계에서 제거하면 자식엔티티 자동 삭제
    @OneToMany(mappedBy = "userPostBoard", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @OrderBy("id asc")
    @JsonManagedReference
    private List<Comment> comments;


    //user랑 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)  // user_no 컬럼 매핑
    private User user;


    @Builder
    public UserPostBoard(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}




