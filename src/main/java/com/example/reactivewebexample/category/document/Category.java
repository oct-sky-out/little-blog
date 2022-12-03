package com.example.reactivewebexample.category.document;

import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.base.util.BaseFieldFactory;
import java.util.List;
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
    private ObjectId id;

    private String name;

    private BaseField baseField;

    private List<Category> children;

    @Builder
    public Category(String name) {
        this.name = name;
        this.baseField = BaseFieldFactory.create();
    }

    public void addChild(Category childCategory) {
        if(this.equals(childCategory)) {
            throw new RuntimeException("같은 카테고리는 하위 카테고리로 만들 수 없습니다.");
        }

        this.children.add(childCategory);
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
}
