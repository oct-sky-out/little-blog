package com.example.reactivewebexample.board.controller;

import com.example.reactivewebexample.board.document.Board;
import com.example.reactivewebexample.board.dto.BoardDto;
import com.example.reactivewebexample.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public Flux<BoardDto> retireveBoard() {
        return boardService.findBoards()
                .flatMap(this::wrapBoardDto);

}

    private Flux<BoardDto> wrapBoardDto(Board board) {
        return Flux.just(BoardDto
            .builder()
            .id(board.getId())
            .title(board.getTitle())
            .content(board.getContent())
            .createdAt(board.getBaseField().getCreatedAt())
            .updatedAt(board.getBaseField().getUpdatedAt())
            .build());
    }
}
