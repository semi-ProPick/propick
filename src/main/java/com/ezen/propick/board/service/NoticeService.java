package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.entity.Notice;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.FnaBoardRepository;
import com.ezen.propick.board.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    @Autowired
    private final NoticeRepository noticeRepository;

    /* 게시글 작성 */
    public void write(Notice notice) {
        noticeRepository.save(notice);
    }

    /* 게시글 리스트 */
    public Page<Notice> noticeList(Pageable pageable) {

        return noticeRepository.findAll(pageable);
    }

    public Page<Notice> boardSearchList(String searchKeyword, Pageable pageable) {

        return noticeRepository.findByTitleContaining(searchKeyword, pageable);
    }

    /* 특정 게시글 불러오기 */
    public Notice boardView(Integer id) {

        return noticeRepository.findById(id).get();
    }
}
