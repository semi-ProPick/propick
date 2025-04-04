package com.ezen.propick.board.service;

import com.ezen.propick.board.dto.CommentRequestDTO;
import com.ezen.propick.board.entity.Comment;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.CommentRepository;
import com.ezen.propick.board.repository.UserPostBoardRepository;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserPostBoardRepository userPostBoardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment commentSave(Integer id, CommentRequestDTO dto, String userId) {
        UserPostBoard userPostBoard = userPostBoardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + id));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. Username: " + userId));

        Comment comment = Comment.builder()
                .contents(dto.getContents())  // 댓글 내용 설정
                .userPostBoard(userPostBoard) // 게시글 설정
                .user(user)                   // 사용자 설정 (필수!)
                .build();

        return commentRepository.save(comment);  // 저장 후 반환
    }


}
