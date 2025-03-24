package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.repository.FnaBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FnaBoardService {

    private final FnaBoardRepository fnaBoardRepository;

    /* 게시글 작성 */
    public void write(FnaBoard fnaBoard) {
        fnaBoardRepository.save(fnaBoard);
    }

    /* 게시글 리스트 */
    public List<FnaBoard> fnaBoardList() {

        return fnaBoardRepository.findAll();
    }

    /* 특정 게시글 불러오기 */
    public FnaBoard boardView(Integer id) {

        return fnaBoardRepository.findById(id).get();
    }

}
