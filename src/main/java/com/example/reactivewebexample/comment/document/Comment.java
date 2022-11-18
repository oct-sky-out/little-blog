package com.example.reactivewebexample.comment.document;

import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.board.document.Board;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
@Getter
public class Comment {
    @Id
    private String id;

    @DocumentReference(lazy = true)
    private Board board;

    private String name;

    private String content;

    private String password;

    private BaseField baseField;

    @Builder
    public Comment(String name, String content, String password, BaseField baseField) {
        this.name = name;
        this.content = content;
        this.password = password;
        this.baseField = baseField;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
