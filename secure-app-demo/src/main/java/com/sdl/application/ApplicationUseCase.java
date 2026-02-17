package com.sdl.application;

import com.sdl.dto.BookView;
import com.sdl.dto.CreateBookCommand;
import com.sdl.dto.UpdateBookCommand;
import com.sdl.gateway.ApplicationGateWay;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ApplicationUseCase {

    private final ApplicationGateWay applicationGateWay;

    public ApplicationUseCase(ApplicationGateWay applicationGateWay) {
        this.applicationGateWay = applicationGateWay;
    }

    public List<BookView> getAllBooks(){
        return applicationGateWay.findAllBooks().stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    public Optional<BookView> getBookById(Long id){
        return applicationGateWay.findBookById(id).map(this::toView);
    }

    public Optional<BookView> getBookByIsbn(String isbn){
        return applicationGateWay.findBookByIsbn(isbn).map(this::toView);
    }

    public BookView createBook(CreateBookCommand command){
        var newBook = new com.sdl.application.model.Book(
                command.title(),
                command.author(),
                command.isbn(),
                command.price(),
                command.inStock()
        );

        return toView(applicationGateWay.saveBook(newBook));
    }

    public BookView updateBook(Long id, UpdateBookCommand command){
        var book = applicationGateWay.findBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        book.setTitle(command.title());
        book.setAuthor(command.author());
        book.setIsbn(command.isbn());
        book.setPrice(command.price());
        book.setInStock(command.inStock());
        return toView(applicationGateWay.saveBook(book));
    }

    public BookView setBookInStock(Long id, boolean inStock){
        var book = applicationGateWay.findBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        book.setInStock(inStock);
        return toView(applicationGateWay.saveBook(book));
    }

    public boolean deleteBook(Long id){
        return applicationGateWay.deleteBookById(id);
    }

    public List<BookView> findBooksByAuthor(String author){
        return applicationGateWay.findBooksByAuthor(author).stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    public List<BookView> findBooksByTitleContains(String titlePart){
        return applicationGateWay.findBooksByTitleContains(titlePart).stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    private BookView toView(com.sdl.application.model.Book book) {
        return new BookView(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPrice(),
                book.isInStock(),
                book.getDateCreated()
        );
    }
}
