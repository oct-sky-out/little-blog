package com.example.reactivewebexample.likeit.service;

import com.example.reactivewebexample.likeit.document.LikeIt;
import com.example.reactivewebexample.likeit.repository.LikeItRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LikeItServiceImpl implements LikeItService {
    private final LikeItRepository likeItRepository;

    @Transactional
    public Mono<Void> initializeBoardLikeIt(ObjectId boardId) {
        LikeIt likeIt = new LikeIt(boardId);

        return likeItRepository.insert(likeIt)
            .then();
    }

    @Transactional
    public Mono<Void> likeIt(String boardId, String userId) {
        return likeItRepository.findById(new ObjectId(boardId))
            .flatMap(likeIt -> {
                if(!likeIt.getUsers().contains(userId)) {
                    likeIt.doLikeIt(userId);
                }
                return Mono.empty();
            });
    }

    @Transactional
    public Mono<Void> unLikeIt(String boardId, String userId) {
        return likeItRepository.findById(new ObjectId(boardId))
            .flatMap(likeIt -> {
                if(!likeIt.getUsers().contains(userId)) {
                    likeIt.doLikeIt(userId);
                }
                return Mono.empty();
            });
    }
}
