package com.sdl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse(int statusCode,
                          String message,
                          Object data,
                          Object errors,
                          String path,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                          LocalDateTime timestamp) {

    public ApiResponse {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
