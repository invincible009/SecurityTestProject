package com.sdl.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookView(Long id,
                       String title,
                       String author,
                       String isbn,
                       BigDecimal price,
                       boolean inStock,
                       LocalDateTime dateCreated) {
}
