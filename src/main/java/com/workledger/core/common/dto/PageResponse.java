package com.workledger.core.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Standardized pagination response wrapper.
 *
 * @param <T>
 */
@Data
@Builder
@Schema(description = "Paginated response")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    @Schema(description = "Page content")
    private List<T> content;

    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    private int numberOfElements;
    private boolean empty;

    // ================== Static Factory Methods ==================

    /**
     * Create a PageResponse from Sping Data Page object
     *
     * @param page Spring Data page object
     * @return PageResponse with all pagination metadata
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty())
                .build();
    }

    /**
     * Creates a PageResponse from Spring Data Page with content transformation
     * Useful when we convert entities to DTOs
     *
     * @param page Spring Data Page object
     * @param mapper function to transform content
     * @return PageResponse with transformed content
     */
    public static <T, R> PageResponse<R> from(Page<T> page, Function<T, R> mapper) {
        List<R> transformedContent = page.getContent().stream()
                .map(mapper)
                .toList();

        return PageResponse.<R>builder()
                .content(transformedContent)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .numberOfElements(transformedContent.size())
                .empty(transformedContent.isEmpty())
                .build();
    }

    /**
     * Create an empty PageResponse
     *
     * @return empty PageResponse
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .content(List.of())
                .pageNumber(0)
                .pageSize(0)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .numberOfElements(0)
                .empty(true)
                .build();
    }
}
