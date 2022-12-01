package com.example.reactivewebexample.likeit.document;

import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.base.util.BaseFieldFactory;
import java.util.List;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Getter
public class LikeIt {
    @Field("boardId")
    private ObjectId boardId;

    private Integer count;

    private List<ObjectId> users;

    private BaseField baseField;


    public void doLikeIt(String user) {
        this.count++;
        this.users.add(new ObjectId(user));
    }

    public void doUnlikeIt(String user) {
        this.count--;
        this.users.remove(new ObjectId(user));
    }

    public LikeIt(ObjectId boardId) {
        this.boardId = boardId;
        this.count = 0;
        this.users = List.of();
        this.baseField = BaseFieldFactory.create();
    }
}
