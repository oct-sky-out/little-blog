package com.example.reactivewebexample.board.document;

import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.comment.document.Comment;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Getter
@Document(collection = "board")
public class Board {
    @Id
    private String id;

    private String title;

    private String content;

    @DocumentReference(lazy = true)
    private Comment comment;

    private BaseField baseField;
}
