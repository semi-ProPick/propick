package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.entity.Notice;
import com.ezen.propick.board.repository.FnaBoardRepository;
import com.ezen.propick.board.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /* 게시글 작성 */
    public void write(Notice notice) {
        noticeRepository.save(notice);
    }

    /* 게시글 리스트 */
    public List<Notice> noticeList() {

        return noticeRepository.findAll();
    }

    /* 특정 게시글 불러오기 */
    public Notice boardView(Integer id) {

        return noticeRepository.findById(id).get();
    }
}
