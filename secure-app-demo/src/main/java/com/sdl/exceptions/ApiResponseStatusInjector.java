package com.sdl.exceptions;

import com.sdl.dto.ApiResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.sdl")
public class ApiResponseStatusInjector implements ResponseBodyAdvice<ApiResponse> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return ApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public @Nullable ApiResponse beforeBodyWrite(@Nullable ApiResponse body,
                                                 MethodParameter returnType,
                                                 MediaType selectedContentType,
                                                 Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                                 ServerHttpRequest request,
                                                 ServerHttpResponse response) {

            int status = ((ServletServerHttpResponse) response)
                    .getServletResponse()
                    .getStatus();

            return ApiResponse.builder()
                    .statusCode(status)
                    .message(body.message())
                    .data(body.data())
                    .errors(body.errors())
                    .path(body.path())
                    .timestamp(body.timestamp())
                    .build();
    }
}
