package com.example.reactivewebexample.comment.document;

import com.example.reactivewebexample.base.document.BaseField;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
public class Comment {
    @Id
    private String id;

    private String name;

    private String contnet;

    private BaseField baseField;
}