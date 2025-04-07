package com.ezen.propick.board.service;

import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.BoardImageRepository;
import com.ezen.propick.board.repository.UserPostBoardRepository;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserPostBoardService {

    @Autowired
    private final UserPostBoardRepository userPostBoardRepository;
    private final UserRepository userRepository;
    private final BoardImageRepository boardImageRepository;

    /* 게시글 작성 */
    public void write(UserPostBoard userPostBoard, MultipartFile file, Integer userNo) throws Exception {
        // 파일 저장 경로 지정
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\user_post_files";

        // 저장 폴더가 없으면 생성
        File dir = new File(projectPath);
        if (!dir.exists()) {
            dir.mkdirs(); // 폴더 생성
        }

        // 랜덤 파일 이름 생성 (한글, 공백 문제 해결)
        UUID uuid = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename();
        String cleanedFilename = uuid + "_" + originalFilename.replaceAll("[^a-zA-Z0-9.]", "_");

        //실제 파일 저장
        File saveFile = new File(projectPath, cleanedFilename);
        file.transferTo(saveFile);

        // DB 저장을 위한 정보 설정
        userPostBoard.setFilename(cleanedFilename);
        userPostBoard.setFilepath("/user_post_files/" + cleanedFilename);

        // DB에 저장
        userPostBoardRepository.save(userPostBoard);
    }

    /* 게시글 수정 */
    public void modify(UserPostBoard userPostBoard) {
        userPostBoardRepository.save(userPostBoard);
    }


    /* 게시글 리스트 */
    public Page <UserPostBoard> userPostBoardList(Pageable pageable) {
        return userPostBoardRepository.findAll(pageable);
    }

    public Page<UserPostBoard> boardSearchList(String searchKeyword, Pageable pageable) {

        return userPostBoardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    /* 특정 게시글 불러오기 */
    public UserPostBoard boardView(Integer id) {

        return userPostBoardRepository.findById(id).get();
    }

    /* 특정 게시글 삭제 */
    public void boardDelete(Integer id) {
        userPostBoardRepository.deleteById(id);
    }

    /* 조회수 */
    public UserPostBoard getCount(Integer id) {
        Optional<UserPostBoard> userPostBoard = this.userPostBoardRepository.findById(id);
        if (userPostBoard.isPresent()) {
            UserPostBoard view = userPostBoard.get();
            view.setCountview(view.getCountview() + 1);
            this.userPostBoardRepository.save(view);
            return view;
        }else{
            throw new DataNotFoundException("question not found");
        }

    }

    // 사용자 정의 예외 클래스 예시
    public class DataNotFoundException extends RuntimeException {
        public DataNotFoundException(String message) {
            super(message);
        }
    }





    /* 댓글 등록 */
        /* 댓글 삭제 */
        /* 댓글 수정 */

        /* 이미지 등록 */
        /* 이미지 삭제 */


}
