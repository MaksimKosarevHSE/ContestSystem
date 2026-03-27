package com.maksim.submissionAcceptorService.dto;

import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

public record PageResponseDto<T>(
        List<T> content,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {
    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public static <T> PageResponseDto<T> emptyPage(Class<T> clazz) {
        return new PageResponseDto<>(Collections.emptyList(), 0, 0, 0L, 0);
    }
}
