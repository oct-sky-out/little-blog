package com.example.reactivewebexample.category.document;

import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.base.util.BaseFieldFactory;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
public class Category {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String name;

    private BaseField baseField;

    private ObjectId parentId;

    @Builder
    public Category(String name) {
        this.name = name;
        this.baseField = BaseFieldFactory.create();
    }

    public void addParent(String parentId) {
        if(isSameCategory(parentId)) {
            throw new RuntimeException("같은 카테고리는 하위 카테고리로 만들 수 없습니다.");
        }

        this.parentId = new ObjectId(parentId);
    }

    private boolean isSameCategory(String parentId) {
        return Objects.nonNull(this.id) && this.id.toHexString().equals(parentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void setName(String name) {
        this.name = name;
        this.baseField.setUpdatedAt(LocalDateTime.now());
        this.baseField.setVersion(this.baseField.getVersion() + 1);
    }
}
