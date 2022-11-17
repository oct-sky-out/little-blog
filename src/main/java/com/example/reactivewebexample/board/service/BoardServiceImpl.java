package com.example.reactivewebexample.board.service;

import com.example.reactivewebexample.board.document.Board;
import com.example.reactivewebexample.board.repository.BoardRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public Flux<Board> findBoards() {
        return boardRepository.findAll();
    }
}
