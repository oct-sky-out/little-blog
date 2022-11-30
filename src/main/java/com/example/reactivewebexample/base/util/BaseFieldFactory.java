package com.example.reactivewebexample.base.util;

import com.example.reactivewebexample.base.document.BaseField;
import java.time.LocalDateTime;

public class BaseFieldFactory {

    private BaseFieldFactory() {
    }

    public static BaseField create() {
        return BaseField.builder()
            .createdAt(LocalDateTime.now())
            .updatedAt(null)
            .isDeleted(0)
            .version(1)
            .build();
    }
}
