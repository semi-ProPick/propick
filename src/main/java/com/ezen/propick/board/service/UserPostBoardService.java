package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.BoardCommentRepository;
import com.ezen.propick.board.repository.BoardImageRepository;
import com.ezen.propick.board.repository.UserPostBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserPostBoardService {

    @Autowired
    private final UserPostBoardRepository userPostBoardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardImageRepository boardImageRepository;

    /* 게시글 작성 */
    public void write(UserPostBoard userPostBoard){
        userPostBoardRepository.save(userPostBoard);
    }

    /* 게시글 리스트 */
    public List<UserPostBoard> userPostBoardList() {
        return userPostBoardRepository.findAll();
    }

    /* 특정 게시글 불러오기 */
    public UserPostBoard boardView(Integer id) {

        return userPostBoardRepository.findById(id).get();
    }

    /* 특정 게시글 삭제 */
    public void boardDelete(Integer id){
        userPostBoardRepository.deleteById(id);
    }




    /* 댓글 등록 */
    /* 댓글 삭제 */
    /* 댓글 수정 */

    /* 이미지 등록 */
    /* 이미지 삭제 */

}
