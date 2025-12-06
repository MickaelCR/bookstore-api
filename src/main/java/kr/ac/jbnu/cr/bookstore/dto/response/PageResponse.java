package kr.ac.jbnu.cr.bookstore.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private String sort;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(page.getSort().toString())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public static <T, R> PageResponse<R> of(Page<T> page, List<R> content) {
        return PageResponse.<R>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(page.getSort().toString())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}