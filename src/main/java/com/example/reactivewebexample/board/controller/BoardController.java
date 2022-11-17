package com.example.reactivewebexample.board.controller;

import com.example.reactivewebexample.board.dto.BoardDto;
import com.example.reactivewebexample.board.dto.UpdateBoardDto;
import com.example.reactivewebexample.board.service.BoardService;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public Flux<BoardDto> retrieveBoard() {
        return boardService.findBoards()
                .flatMap(BoardDto::toFlux);
    }

    @PostMapping
    public Mono<CreationDto> createBoard(@RequestBody @Valid BoardDto boardDto) {
        return boardService.createBoard(boardDto)
            .flatMap(board -> CreationDto.toMono(board.getId()));
    }

    @PutMapping("/{boardId}")
    public Mono<ModifyDto<UpdateBoardDto>> updateBoard(@RequestBody @Valid UpdateBoardDto boardDto,
                                               @PathVariable("boardId") String boardId) {
        return boardService.updateBoard(boardId, boardDto);
    }
}
