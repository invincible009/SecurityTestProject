package com.sdl.application;

import com.sdl.application.model.Book;
import com.sdl.dto.BookView;
import com.sdl.dto.CreateBookCommand;
import com.sdl.dto.UpdateBookCommand;
import com.sdl.gateway.ApplicationGateWay;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(ApplicationUseCase.class)
class ApplicationFunctionalityTest {

    @Autowired
    private ApplicationUseCase applicationUseCase;

    @MockitoBean
    private ApplicationGateWay applicationGateWay;

    @Test
    void createBookSavesAndReturnsMappedView() {
        CreateBookCommand command = new CreateBookCommand(
                "Clean Code", "Robert C. Martin", "ISBN-100", new BigDecimal("59.99"), true
        );
        Book saved = book(1L, "Clean Code", "Robert C. Martin", "ISBN-100", new BigDecimal("59.99"), true);
        when(applicationGateWay.saveBook(any(Book.class))).thenReturn(saved);

        BookView result = applicationUseCase.createBook(command);

        assertEquals("Clean Code", result.title());
        assertEquals("ISBN-100", result.isbn());
        assertTrue(result.inStock());
    }

    @Test
    void updateBookMutatesExistingBookAndPersists() {
        Book existing = book(2L, "Old Title", "Old Author", "ISBN-101", new BigDecimal("12.50"), false);
        UpdateBookCommand command = new UpdateBookCommand(
                "New Title", "New Author", "ISBN-102", new BigDecimal("22.00"), true
        );
        when(applicationGateWay.findBookById(2L)).thenReturn(Optional.of(existing));
        when(applicationGateWay.saveBook(existing)).thenReturn(existing);

        BookView result = applicationUseCase.updateBook(2L, command);

        assertEquals("New Title", result.title());
        assertEquals("New Author", result.author());
        assertEquals("ISBN-102", result.isbn());
        assertEquals(new BigDecimal("22.00"), result.price());
        assertTrue(result.inStock());
    }

    @Test
    void setInStockUpdatesStockFlag() {
        Book existing = book(3L, "DDD", "Evans", "ISBN-103", new BigDecimal("44.00"), true);
        when(applicationGateWay.findBookById(3L)).thenReturn(Optional.of(existing));
        when(applicationGateWay.saveBook(existing)).thenReturn(existing);

        BookView result = applicationUseCase.setBookInStock(3L, false);

        assertFalse(result.inStock());
    }

    @Test
    void deleteBookDelegatesToGateway() {
        when(applicationGateWay.deleteBookById(10L)).thenReturn(true);

        boolean deleted = applicationUseCase.deleteBook(10L);

        assertTrue(deleted);
        verify(applicationGateWay).deleteBookById(10L);
    }

    @Test
    void getAndFindOperationsReturnMappedViews() {
        Book first = book(4L, "Book A", "Author One", "ISBN-104", new BigDecimal("11.00"), true);
        Book second = book(5L, "Book B", "Author Two", "ISBN-105", new BigDecimal("21.00"), false);

        when(applicationGateWay.findAllBooks()).thenReturn(List.of(first, second));
        when(applicationGateWay.findBookById(4L)).thenReturn(Optional.of(first));
        when(applicationGateWay.findBookByIsbn("ISBN-105")).thenReturn(Optional.of(second));
        when(applicationGateWay.findBooksByAuthor("Author")).thenReturn(List.of(first, second));
        when(applicationGateWay.findBooksByTitleContains("Book")).thenReturn(List.of(first, second));

        assertEquals(2, applicationUseCase.getAllBooks().size());
        assertEquals("Book A", applicationUseCase.getBookById(4L).orElseThrow().title());
        assertEquals("Book B", applicationUseCase.getBookByIsbn("ISBN-105").orElseThrow().title());
        assertEquals(2, applicationUseCase.findBooksByAuthor("Author").size());
        assertEquals(2, applicationUseCase.findBooksByTitleContains("Book").size());
    }

    @Test
    void updateAndStockOperationsThrowWhenBookMissing() {
        when(applicationGateWay.findBookById(eq(99L))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> applicationUseCase.updateBook(
                99L, new UpdateBookCommand("t", "a", "i", BigDecimal.ONE, true)
        ));
        assertThrows(IllegalArgumentException.class, () -> applicationUseCase.setBookInStock(99L, false));
    }

    private Book book(Long id, String title, String author, String isbn, BigDecimal price, boolean inStock) {
        Book book = new Book(title, author, isbn, price, inStock);
        ReflectionTestUtils.setField(book, "id", id);
        ReflectionTestUtils.setField(book, "dateCreated", LocalDateTime.now());
        return book;
    }
}
