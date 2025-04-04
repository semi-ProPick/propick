package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.entity.Notice;
import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.repository.FnaBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class FnaBoardService {

    @Autowired
    private FnaBoardRepository fnaBoardRepository;

    /* 게시글 작성 */
    public void fnawrite(FnaBoard fnaBoard) {
        fnaBoardRepository.save(fnaBoard);
    }

    //리스트 처리
    public Page<FnaBoard> fnaBoardList(Pageable pageable) {

        return fnaBoardRepository.findAll(pageable);
    }

    public Page<FnaBoard> fnaSearchList(String searchKeyword, Pageable pageable) {

        return fnaBoardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    /* 특정 게시글 불러오기 */
    public FnaBoard fnaView(Integer id) {

        return fnaBoardRepository.findById(id).get();
    }

    /* 특정 게시글 삭제 */
    public void fnaDelete(Integer id) {
        fnaBoardRepository.deleteById(id);
    }

    /* 게시글 수정 */
    public void modify(FnaBoard fnaBoard) {
        fnaBoardRepository.save(fnaBoard);
    }


}
