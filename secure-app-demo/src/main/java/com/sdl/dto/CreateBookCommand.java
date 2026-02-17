package com.sdl.dto;

import java.math.BigDecimal;

public record CreateBookCommand(String title,
                                String author,
                                String isbn,
                                BigDecimal price,
                                boolean inStock) {
}
