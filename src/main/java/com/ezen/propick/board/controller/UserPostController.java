package com.ezen.propick.board.controller;

import com.ezen.propick.board.dto.CommentRequestDTO;
import com.ezen.propick.board.dto.CommentResponseDTO;
import com.ezen.propick.board.dto.UserPostResponseDTO;
import com.ezen.propick.board.entity.Comment;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.CommentRepository;
import com.ezen.propick.board.repository.UserPostBoardRepository;
import com.ezen.propick.board.service.CommentService;
import com.ezen.propick.board.service.UserPostBoardService;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class UserPostController {
    @Autowired
    private final UserPostBoardService userPostBoardService;
    @Autowired
    private UserPostBoardRepository userPostBoardRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final UserRepository userRepository;


    //@GetMapping("주소부분 쓰기")
    @GetMapping("main/free_write")
    public String userPostWriteForm() {
        return "main/free_write";
    }

    //게시글 작성
    @PostMapping("main/free_write")
    public String userPostWrite(UserPostBoard userPostBoard, Model model, MultipartFile file, Principal principal) throws Exception{
        String username = principal.getName(); // 현재 로그인한 아이디
        Optional<User> user = userRepository.findByUserId(username);
        userPostBoardService.write(userPostBoard, file, user);
        System.out.println(userPostBoard.getTitle());
        System.out.println(userPostBoard.getContents());
        model.addAttribute("message","작성 완료!");
        model.addAttribute("searchUrl","/main/free_board");
        return "/main/postmessage";
    }

    //작성한 게시글 리스트 출력
    @GetMapping("main/free_board")
    public String userPostBoardList(Model model,
                                    @PageableDefault(page=0, size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                    String searchKeyword,
                                    @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        Page<UserPostBoard> list = null;
        //searchKeyword 가 넣이면 원래 페이지 리스트 보여줌
        if(searchKeyword == null) {
            list = userPostBoardService.userPostBoardList(pageable);

            //searchKeyword 가 널이 아니면 작성한 검색 메서드 실행 후 페이지 리스트 보여줌
        } else {
            list = userPostBoardService.boardSearchList(searchKeyword, pageable);
        }
        // 현재 페이지를 기반으로 pageable 생성
        pageable = PageRequest.of(page, pageable.getPageSize(), pageable.getSort());


        //현재 페이지 가져오기  페이지는 0에서 시작하기때문에 1 더해줌
        int nowPage = list.getPageable().getPageNumber() + 1;
        //페이지 수가 음수가 나올 경우 1 반환
        int startPage = Math.max(nowPage -4 , 1);

        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "main/free_board";
    }


    //상세 페이지 조회
    @GetMapping("/main/Free_boardcm")  //이 경로로 왔을때 상세 페이지 조회됨
    @Transactional
    public String boardView(Integer id, Model model){

        model.addAttribute("userPostBoard",userPostBoardService.boardView(id));

        List<Comment> commentList = commentService.getCommentsByPostId(id);
        model.addAttribute("commentList", commentList);

        UserPostBoard userPostBoard = userPostBoardRepository.findById(id).get();
        userPostBoard.setCountview(userPostBoard.getCountview() + 1 );
        return "/main/Free_boardcm";  //상세페이지 뷰를 담당하는 html 주소
    }

    //작성한 게시글 리스트 출력
    @GetMapping("main/my_free_board")
    public String userPostBoardList1(Model model,
                                    @PageableDefault(page=0, size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                    String searchKeyword,
                                    @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        Page<UserPostBoard> list = null;
        //searchKeyword 가 넣이면 원래 페이지 리스트 보여줌
        if(searchKeyword == null) {
            list = userPostBoardService.userPostBoardList(pageable);

            //searchKeyword 가 널이 아니면 작성한 검색 메서드 실행 후 페이지 리스트 보여줌
        } else {
            list = userPostBoardService.boardSearchList(searchKeyword, pageable);
        }
        // 현재 페이지를 기반으로 pageable 생성
        pageable = PageRequest.of(page, pageable.getPageSize(), pageable.getSort());


        //현재 페이지 가져오기  페이지는 0에서 시작하기때문에 1 더해줌
        int nowPage = list.getPageable().getPageNumber() + 1;
        //페이지 수가 음수가 나올 경우 1 반환
        int startPage = Math.max(nowPage -4 , 1);

        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "main/my_free_board";
    }


    //삭제
    @GetMapping("main/board_delete")
    public String boardDelete(Integer id){
        userPostBoardService.boardDelete(id);
        return "redirect:free_board";
    }

    //수정 @PathVariable 사용
    @GetMapping("main/free_modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model){

        model.addAttribute("userPostBoard", userPostBoardService.boardView(id));
        return "main/free_modify";
    }

    //수정
    @PostMapping("/main/free_update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, UserPostBoard userPostBoard) {

        //기존 내용 가져옴
        UserPostBoard boardTemp = userPostBoardService.boardView(id);
        //위의 인수로 받은 새로운 내용 가져오기-> 덮어씌우기
        boardTemp.setTitle(userPostBoard.getTitle());
        boardTemp.setContents(userPostBoard.getContents());

        userPostBoardService.modify(boardTemp);

        return "redirect:/main/free_board";
    }

    //댓글

    //댓글 등록
    @PostMapping("/main/Free_boardcm/{id}/comments")
    public ResponseEntity<Comment> save(
            @PathVariable Integer id,
            @RequestBody CommentRequestDTO requestDTO,
            Principal principal) {

        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // 인증되지 않은 경우
        }

        Comment savedComment = commentService.commentSave(id, requestDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/api/main/Free_boardcm/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId) {
        commentService.deleteCommentById(commentId);
        return ResponseEntity.ok().body("삭제 완료");
    }

    // 수정 폼으로 이동
    @GetMapping("/main/comments/{id}/edit")
    public String editCommentForm(@PathVariable Integer id, Model model) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        model.addAttribute("comment", comment);
        return "/main/comment/edit"; // 수정 폼 템플릿
    }

    // 수정 폼 제출
    @PostMapping("/main/comments/{id}/update")
    public String updateComment(@PathVariable Integer id, @RequestParam String contents) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setContents(contents);
        commentRepository.save(comment);
        return "redirect:/main/Free_boardcm?id=" + comment.getUserPostBoard().getId(); // 원래 글로 리다이렉트
    }

}

