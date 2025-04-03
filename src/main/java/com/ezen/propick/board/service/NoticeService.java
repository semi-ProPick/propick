package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.Notice;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    //작성
    public void noticewrite(Notice notice){

        noticeRepository.save(notice);
    }

    //리스트 처리
    public Page<Notice> noticeList(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    public Page<Notice> noticeSearchList(String searchKeyword, Pageable pageable) {

        return noticeRepository.findByTitleContaining(searchKeyword, pageable);
    }

    //특정 게시글 불러오기
    public Notice noticeView(Integer id) {
        return noticeRepository.findById(id).get();
    }

    /* 특정 게시글 삭제 */
    public void noticeDelete(Integer id) {
        noticeRepository.deleteById(id);
    }

    /* 게시글 수정 */
    public void modify(Notice notice) {
        noticeRepository.save(notice);
    }

}
