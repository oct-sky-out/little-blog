package com.example.reactivewebexample.board.service;

import com.example.reactivewebexample.board.document.Board;
import com.example.reactivewebexample.board.dto.BoardDto;
import com.example.reactivewebexample.board.dto.UpdateBoardDto;
import com.example.reactivewebexample.board.repository.BoardRepository;
import com.example.reactivewebexample.common.dto.ModifyDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @Override
    public Mono<Board> createBoard(BoardDto boardDto) {
        return boardRepository.insert(boardDto.parseBoard());
    }

    @Override
    public Mono<ModifyDto<UpdateBoardDto>> updateBoard(String boardId, UpdateBoardDto boardDto) {
        return boardRepository.findById(boardId)
            .switchIfEmpty(Mono.error(new RuntimeException("게시글 정보가 없습니다.")))
            .map(board -> {
                Integer version = board.getBaseField().getVersion();
                board.updateBoard(boardDto.title(), boardDto.content());
                board.getBaseField().setUpdatedAt(LocalDateTime.now());
                board.getBaseField().setVersion(version + 1);

                return board;
            })
            .flatMap(boardRepository::save)
            .flatMap(board -> ModifyDto.toMono(board.getId(), boardDto));
    }
}
