package com.workledger.core.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized API response wrapper for all REST endpoints.
 *
 * @param <T> the tye of data being returned
 */
@Data
@Builder
@Schema(description = "Standard API response wrapper")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    @Schema(description = "Message")
    private String message;

    @Schema(description = "Response payload")
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private Object metadata;

    // ================== Static Factory Methods ==================

    /**
     * Creates a successful response with data and message
     *
     * @param data the response data
     * @param message success message
     * @return ApiResponse with success = true
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with data, message, and metadata.
     *
     * @param data the response data
     * @param message success message
     * @param metadata additional metadata
     * @return ApiResponse with success = true
     */
    public static <T> ApiResponse<T> success(T data, String message, Object metadata) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response for created resources (HTTP 201)
     *
     * @param data the created resource data
     * @param message creation message
     * @return ApiResponse with success = true
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response for created resources with default message
     *
     * @param data the created response data
     * @return ApiResponse with success = true
     */
    public static <T> ApiResponse<T> created(T data) {
        return created(data, "Resource created successfully");
    }

    /**
     * Creates a successful response with no content (HTTP 204)
     * Used for delete operations or updates that don't return data
     *
     * @param message success message
     * @return ApiResponse with success = true and no data
     */
    public static <T> ApiResponse<T> noContent(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with no content and default message.
     *
     * @return AiResponse with success = true and no data
     */
    public static <T> ApiResponse<T> noContent() {
        return noContent("Operation completed successfully");
    }

    /**
     * Creates an error response with message
     *
     * @param message error message
     * @return ApiResponse with success = false
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with message and metadata
     * Useful for validation errors or detailed error information
     *
     * @param message error message
     * @param metadata additional error details
     * @return ApiResponse with success = false
     */
    public static <T> ApiResponse<T> error(String message, Object metadata) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a custom response with specified success status.
     *
     * @param success whether the operation wa successful
     * @param data the response data
     * @param message response message
     * @return ApiResponse with specified success status
     */
    public static <T> ApiResponse<T> of(boolean success, T data, String message) {
        return ApiResponse.<T>builder()
                .success(success)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a custom response with all fields.
     *
     * @param success whether the operation was successful
     * @param data the response data
     * @param message response message
     * @param metadata additional metadata
     * @return ApiResponse with all fields populated
     */
    public static <T> ApiResponse<T> of(boolean success, T data, String message, Object metadata) {
        return ApiResponse.<T>builder()
                .success(success)
                .message(message)
                .data(data)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }
    // ================== Convenience Methods ==================

    public boolean isSuccess () {
        return success;
    }

    public boolean isError () {
        return !success;
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasMetadata() {
        return metadata != null;
    }

    public boolean hasMessages() {
        return message != null && !message.trim().isEmpty();
    }
}
