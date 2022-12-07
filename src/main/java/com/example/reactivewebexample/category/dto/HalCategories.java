package com.example.reactivewebexample.category.dto;

import com.example.reactivewebexample.category.document.Category;
import java.util.List;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;

@Getter
public class HalCategories {
    private final EntityModel<Category> category;
    private final List<HalCategories> children;

    public HalCategories(EntityModel<Category> category, List<HalCategories> children) {
        this.category = category;
        this.children = children;
    }
}
