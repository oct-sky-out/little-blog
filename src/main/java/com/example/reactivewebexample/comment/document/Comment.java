package com.example.reactivewebexample.comment.document;

import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.board.document.Board;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
public class Comment {
    @Id
    private ObjectId id;

    private ObjectId boardId;

    private String name;

    private String content;

     private BaseField baseField;

    @Builder
    public Comment(String name, String content, BaseField baseField) {
        this.name = name;
        this.content = content;
        this.baseField = baseField;
    }

    public void setBoardId(Board board) {
        this.boardId = board.getId();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
