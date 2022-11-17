package com.example.reactivewebexample.board.service;

import com.example.reactivewebexample.board.document.Board;
import reactor.core.publisher.Flux;

public interface BoardService {
    Flux<Board> findBoards();
}
