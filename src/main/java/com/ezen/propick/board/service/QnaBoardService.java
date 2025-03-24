package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.repository.BoardCommentRepository;
import com.ezen.propick.board.repository.BoardImageRepository;
import com.ezen.propick.board.repository.QnaBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QnaBoardService {

    @Autowired
    private final QnaBoardRepository qnaBoardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardImageRepository boardImageRepository;


    /* 게시글 작성 */
    public void write(QnaBoard qnaBoard) {
        qnaBoardRepository.save(qnaBoard);
    }

    /* 게시글 리스트 */
    public List<QnaBoard> qnaBoardList() {

        return qnaBoardRepository.findAll();
    }

    /* 특정 게시글 불러오기 */
    public QnaBoard boardView(Integer id) {

        return qnaBoardRepository.findById(id).get();
    }
}
