package com.example.reactivewebexample.category.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.spy;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CategoryTest {

    @Test
    @DisplayName("같은 객체여야한다.")
    void 같은_객체여야한다() {
        Category category1 = new Category("hello");
        Category category2 = new Category("hello");

        ObjectId oid = new ObjectId();
        ReflectionTestUtils.setField(category1, "id", oid);
        ReflectionTestUtils.setField(category2, "id", oid);

        assertThat(category1).isEqualTo(category2);
    }

    @Test
    @DisplayName("같은 카테고리 도큐먼트는 하위 카테고리로 못 넣는다.")
    void 같은_카테고리_도큐먼트는_하위_카테고리로_못_넣는다() {
        Category category1 = new Category("hello");
        Category category2 = new Category("hello");

        ObjectId oid = new ObjectId();
        ReflectionTestUtils.setField(category1, "id", oid);
        ReflectionTestUtils.setField(category2, "id", oid);

        assertThatThrownBy(() -> category1.addParent(category2.getId().toString()))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("다른 카테고리 도큐먼트는 하위 카테고리로 넣을 수 있다.")
    void 다른_카테고리_도큐먼트는_하위_카테고리로_넣을_수_있다() {
        Category category1 = spy(new Category("hello"));
        Category category2 = new Category("hello");

        ReflectionTestUtils.setField(category1, "id", new ObjectId());
        ReflectionTestUtils.setField(category2, "id", new ObjectId());

        willDoNothing()
            .given(category1)
            .addParent(category2.getId().toString());
    }
}
