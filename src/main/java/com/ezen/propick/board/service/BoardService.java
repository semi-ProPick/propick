package com.ezen.propick.board.service;

import com.ezen.propick.board.repository.BoardCommentRepository;
import com.ezen.propick.board.repository.BoardImageRepository;
import com.ezen.propick.board.repository.BoardRepository;
import com.ezen.propick.board.repository.BoardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardRepository boardRepository;
    private final BoardTypeRepository boardTypeRepository;
    private final BoardImageRepository boardImageRepository;


}
