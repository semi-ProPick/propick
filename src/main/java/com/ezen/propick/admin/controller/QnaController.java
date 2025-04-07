package com.ezen.propick.admin.controller;

import com.ezen.propick.board.entity.QnaAnswer;
import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.repository.QnaAnswerRepository;
import com.ezen.propick.board.repository.QnaBoardRepository;
import com.ezen.propick.board.service.QnaBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Profile("admin")
@Controller
@RequiredArgsConstructor
public class QnaController {
    @Autowired
    private final QnaBoardService qnaBoardService;

    @Autowired
    private final QnaBoardRepository qnaBoardRepository;
    @Autowired
    private QnaAnswerRepository qnaAnswerRepository;

    //@GetMapping("주소부분 쓰기")
    @GetMapping("/qna_answer/write")
    public String qnaWriteForm() {
        return "/management/post_qna_write";
    }

    //문의사항 삭제
    @GetMapping("qna/delete")
    public String qnaDelete(Integer id){
        qnaBoardService.qnaboardDelete(id);
        return "redirect:list";
    }

    // 답변 삭제
    @PostMapping("/qna/answer/delete/{id}")
    public String deleteAnswer(@PathVariable Integer id) {
        // 답변 존재 여부 확인
        QnaAnswer qnaAnswer = qnaAnswerRepository.findById(id).orElseThrow();

        // 연관된 질문 가져오기
        QnaBoard qnaBoard = qnaAnswer.getQnaBoard();

        // 답변 삭제
        qnaAnswerRepository.delete(qnaAnswer);

        // 만약 해당 질문에 남아있는 답변이 없다면 isAnswered 상태 변경
        boolean hasOtherAnswers = qnaAnswerRepository.existsByQnaBoard(qnaBoard);
        if (!hasOtherAnswers) {
            qnaBoard.setAnswered(false);
            qnaBoardRepository.save(qnaBoard);
        }

        return "redirect:/qna/answer/" + qnaBoard.getId();

    }


    @GetMapping("/qna/answer/{id}")
    public String qnaAnswerForm(@PathVariable Integer id, Model model) {
        QnaBoard qnaBoard = qnaBoardRepository.findById(id).orElseThrow();
        model.addAttribute("qnaBoard", qnaBoard);

        // 기존 답변이 있으면 가져오기
        List<QnaAnswer> answerList = qnaAnswerRepository.findAllByQnaBoard(qnaBoard);
        model.addAttribute("qnaAnswer", answerList);

        return "/management/post_qna_write"; // 답변 작성 페이지로 이동
    }


    //답변 작성
    @PostMapping("/qna/answer/{id}")
    public String answerQna(@PathVariable Integer id, @RequestParam("answer") String answer) {
        // 1. 질문(QnaBoard) 찾기
        QnaBoard qnaBoard = qnaBoardRepository.findById(id).orElseThrow();

        // 2. 새 답변 생성
        QnaAnswer qnaAnswer = new QnaAnswer();
        qnaAnswer.setQnaBoard(qnaBoard); // 질문과 연결
        qnaAnswer.setAnswer(answer);
        qnaAnswer.setCreated_at(LocalDateTime.now());

        // 3. 저장
        qnaAnswerRepository.save(qnaAnswer);

        // 4. 질문의 isAnswered true로 변경
        qnaBoard.setAnswered(true);
        qnaBoardRepository.save(qnaBoard);

        return "redirect:/qna/list";
    }



    //작성한 게시글 리스트 출력
    @GetMapping("qna/list")
    public String qnaBoardList(Model model,
                                    @PageableDefault(page=0, size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                    String searchKeyword,
                                    @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        Page<QnaBoard> list = null;
        //searchKeyword 가 넣이면 원래 페이지 리스트 보여줌
        if(searchKeyword == null) {
            list = qnaBoardService.qnaBoardList(pageable);

            //searchKeyword 가 널이 아니면 작성한 검색 메서드 실행 후 페이지 리스트 보여줌
        } else {
            list = qnaBoardService.qnaSearchList(searchKeyword, pageable);
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

        return "management/post_qna";
    }

    //관리자용 문의사항 상세조회
    @GetMapping("/qna/view")   //localhost:8081/notice/view?id=1
    public String qnaView(Model model, Integer id){
        QnaBoard qnaBoard = qnaBoardRepository.findById(id).orElseThrow();
        model.addAttribute("qnaBoard", qnaBoard);

        // 답변 가져오기
        List<QnaAnswer> answerList = qnaAnswerRepository.findAllByQnaBoard(qnaBoard);
        model.addAttribute("qnaAnswer", answerList);
        return "/management/post_qna_view";
    }


}

