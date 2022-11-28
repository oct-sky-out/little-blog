package com.example.reactivewebexample.comment.service;

import com.example.reactivewebexample.board.document.Board;
import com.example.reactivewebexample.board.repository.BoardRepository;
import com.example.reactivewebexample.comment.dto.CommentCreationDto;
import com.example.reactivewebexample.comment.dto.CommentDto;
import com.example.reactivewebexample.comment.dto.UpdateCommentDto;
import com.example.reactivewebexample.comment.repository.CommentRepository;
import com.example.reactivewebexample.common.dto.CreationDto;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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
        return commentRepository.findAllByBoardId(new ObjectId(boardId))
            .filter(comment -> comment.getBaseField().getIsDeleted() != 1)
            .flatMap(CommentDto::convert);
    }

    @Transactional
    @Override
    public Mono<CreationDto> addComment(String boardId, CommentCreationDto creationDto) {
        Mono<Board> board = boardRepository.findById(new ObjectId(boardId));

        return Mono.just(creationDto)
            .flatMap(CommentCreationDto::parseComment)
            .flatMap(comment -> board
                .doOnNext(comment::setBoardId)
                .thenReturn(comment))
            .flatMap(commentRepository::save)
            .flatMap(comment -> CreationDto.toMono(comment.getId().toHexString()));
    }

    @Override
    public Mono<UpdateCommentDto> updateComment(String commentId, UpdateCommentDto updateCommentDto) {
        return commentRepository.findById(new ObjectId(commentId))
            .flatMap(comment -> {
                Integer version = comment.getBaseField().getVersion();

                comment.setContent(updateCommentDto.content());
                comment.getBaseField().setVersion(++version);

                return Mono.just(comment);
            })
            .flatMap(commentRepository::save)
            .thenReturn(updateCommentDto);
    }

}
