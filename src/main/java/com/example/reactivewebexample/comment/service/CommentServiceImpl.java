package com.example.reactivewebexample.comment.service;

import com.example.reactivewebexample.board.document.Board;
import com.example.reactivewebexample.board.repository.BoardRepository;
import com.example.reactivewebexample.comment.dto.CommentCreationDto;
import com.example.reactivewebexample.comment.dto.CommentDto;
import com.example.reactivewebexample.comment.repository.CommentRepository;
import com.example.reactivewebexample.common.dto.CreationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    @Override
    public Flux<CommentDto> retrieveComments(String boardId) {
        return commentRepository.findAllByBoardId(boardId)
            .flatMap(CommentDto::convert);
    }

    @Transactional
    @Override
    public Mono<CreationDto> addComment(String boardId, CommentCreationDto creationDto) {
        Mono<Board> board = boardRepository.findById(boardId);

        return Mono.just(creationDto)
            .map(CommentCreationDto::parseComment)
            .flatMap(comment -> board
                .doOnNext(comment::setBoard)
                .thenReturn(comment))
            .flatMap(commentRepository::save)
            .flatMap(comment -> CreationDto.toMono(comment.getId()));
    }
}
