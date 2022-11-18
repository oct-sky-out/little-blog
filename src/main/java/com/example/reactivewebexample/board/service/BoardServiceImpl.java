package com.example.reactivewebexample.board.service;

import com.example.reactivewebexample.board.document.Board;
import com.example.reactivewebexample.board.dto.BoardDto;
import com.example.reactivewebexample.board.dto.UpdateBoardDto;
import com.example.reactivewebexample.board.repository.BoardRepository;
import com.example.reactivewebexample.common.dto.ModifyDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    @Override
    public Flux<Board> findBoards() {
        return boardRepository.findAll()
            .filter(board -> board.getBaseField().getIsDeleted() != 1);
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

    @Override
    public Mono<Void> deleteBoard(String boardId) {
        return boardRepository.findById(boardId)
            .switchIfEmpty(Mono.error(new RuntimeException("게시글이 존재하지 않습니다.")))
            .map(Board::deleteBoard)
            .flatMap(boardRepository::save)
            .then();
    }
}
