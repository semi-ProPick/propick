package com.ezen.propick.board.service;

import com.ezen.propick.board.dto.AddCommentRequestDTO;
import com.ezen.propick.board.entity.Comment;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.CommentRepository;
import com.ezen.propick.board.repository.UserPostBoardRepository;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {


    private final CommentRepository commentRepository;
    private final UserPostBoardRepository userPostBoardRepository;
    private final UserRepository userRepository;

    //댓글 추가
    public Comment save(Integer id, AddCommentRequestDTO requestDTO, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        UserPostBoard userPostBoard = userPostBoardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 쓰기 실패 : 해당 게시글이 존재하지 않습니다. " + id));

//        if (userPostBoard.getUser() == null) {
//            throw new IllegalArgumentException("게시글 작성자의 정보가 없습니다. user_no를 확인하세요.");
//        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setUserPostBoard(userPostBoard);
        comment.setContents(requestDTO.getContents());

        return commentRepository.save(comment);
    }


    //댓글 조회
    @Transactional
    public List<Comment> findAll(Integer id) {
        UserPostBoard userPostBoard = userPostBoardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        List<Comment> comments = userPostBoard.getComments();
        return comments;
    }

    //댓글 삭제
    @Transactional
    public void delete(Integer id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id=" + id));

        commentRepository.delete(comment);
    }




}
