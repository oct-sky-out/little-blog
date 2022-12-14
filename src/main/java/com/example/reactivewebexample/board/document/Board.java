package com.example.reactivewebexample.board.document;

import com.example.reactivewebexample.base.document.BaseField;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "board")
public class Board {
    @Id
    private ObjectId id;

    private String title;

    private String content;

    private BaseField baseField;

    @Builder
    public Board(String title, String content, BaseField baseField) {
        this.title = title;
        this.content = content;
        this.baseField = baseField;
    }

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Board deleteBoard() {
        this.getBaseField().setIsDeleted(1);

        return this;
    }
}
