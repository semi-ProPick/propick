package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.repository.BoardImageRepository;
import com.ezen.propick.board.repository.QnaBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QnaBoardService {

    @Autowired
    private final QnaBoardRepository qnaBoardRepository;
    private final BoardImageRepository boardImageRepository;


    /* 게시글 작성 */
    public void write(QnaBoard qnaBoard, MultipartFile file) throws Exception {
//        // 파일 저장 경로 지정
//        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\q&a_post_files";
//
//        // 저장 폴더가 없으면 생성
//        File dir = new File(projectPath);
//        if (!dir.exists()) {
//            dir.mkdirs(); // 폴더 생성
//        }
//
//        // 랜덤 파일 이름 생성 (한글, 공백 문제 해결)
//        UUID uuid = UUID.randomUUID();
//        String originalFilename = file.getOriginalFilename();
//        String cleanedFilename = uuid + "_" + originalFilename.replaceAll("[^a-zA-Z0-9.]", "_");
//
//        //실제 파일 저장
//        File saveFile = new File(projectPath, cleanedFilename);
//        file.transferTo(saveFile);
//
//        // DB 저장을 위한 정보 설정
//        qnaBoard.setFilename(cleanedFilename);
//        qnaBoard.setFilepath("/q&a_post_files/" + cleanedFilename);

        // DB에 저장
        qnaBoardRepository.save(qnaBoard);
    }

    /* 게시글 리스트 */
    public Page <QnaBoard> qnaBoardList(Pageable pageable) {
        return qnaBoardRepository.findAll(pageable);
    }

    public Page<QnaBoard> boardSearchList(String searchKeyword, Pageable pageable) {

        return qnaBoardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    /* 특정 게시글 불러오기 */
    public QnaBoard boardView(Integer id) {

        return qnaBoardRepository.findById(id).get();
    }

    /* 특정 게시글 삭제 */
    @Transactional
    public void qnaboardDelete(Integer id) {
        qnaBoardRepository.deleteById(id);
    }

    // 사용자 정의 예외 클래스 예시
    public class DataNotFoundException extends RuntimeException {
        public DataNotFoundException(String message) {
            super(message);
        }
    }
}
