package com.example.reactivewebexample.board.service;

import com.example.reactivewebexample.board.document.Board;
import com.example.reactivewebexample.board.dto.BoardDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BoardService {
    Flux<Board> findBoards();

    Mono<Board> createBoard(BoardDto boardDto);
}
