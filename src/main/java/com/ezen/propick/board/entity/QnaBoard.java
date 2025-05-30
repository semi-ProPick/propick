package com.ezen.propick.board.entity;

import com.ezen.propick.user.entity.User;
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
@Table(name = "q_n_a_board")
public class QnaBoard {
    @Id
    //id값 자동 갱신
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("q_n_a_id"))
    private Integer id;  //번호PK

    @Column(name = ("q_n_a_title"), nullable = false)
    private String title;  //제목

    @Column(name = ("q_n_a_content"), nullable = false)
    private String contents;  //내용

    @Column(name = ("q_n_a_created_at"),nullable = false)
    @CreatedDate
    private LocalDateTime created_at = LocalDateTime.now();  //생성일


    @Column(name = ("q_n_a_lock_status"))
    private boolean lock_status;

    @Column(name = ("q_n_a_private_key"))
    private String private_key;

    @Column(name = ("post_filename"))
    private String filename;

    @Column(name = ("post_filepath"))
    private String filepath;

    @Column(name = "is_answered")
    private boolean isAnswered;

    //user랑 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)  // user_no 컬럼 매핑
    private User user;

    @OneToMany(mappedBy = "qnaBoard")
    private List<QnaAnswer> answers;

//    @OneToMany(mappedBy = "qnaBoard", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserPostBoardComment> comments = new ArrayList<>();

    @PrePersist
    public void setDefaultPostContents() {
        if (this.contents == null) {
            this.contents = "";  // 기본값 설정
        }
        if (this.title == null) {
            this.title = "";  // 기본값 설정
        }
    }

    public QnaBoard(String title, String contents, boolean lock_status, String

            private_key) {
        this.title=title;
        this.contents=contents;
        this.lock_status=lock_status;
        this.private_key=private_key;
    }
}
