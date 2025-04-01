package com.ezen.propick.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Data
@Table(name = "notice")
public class Notice {

    @Id
    //id값 자동 갱신
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("notice_id"))
    private Integer id;  //자유게시판 번호PK

    @Column(name = ("notice_title"), nullable = false)
    private String title;  //제목

    @Column(name = ("notice_contents"), nullable = false)
    private String contents;  //내용

    @Column(name = ("notice_create_at"), nullable = false)
    @CreatedDate
    private LocalDateTime created_at = LocalDateTime.now();  //생성일

    @Column(name = ("notice_updated_at"), nullable = false)
    private LocalDateTime updated_at;  //수정일

    @Column(name = ("notice_pin_status"))
    private boolean pin_status;

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
//    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserPostBoardComment> comments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<BoardImage> images = new ArrayList<>();
//

    public Notice(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}