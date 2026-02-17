package com.sdl.gateway;


import com.sdl.application.model.Book;


import java.util.List;
import java.util.Optional;

public interface ApplicationGateWay {

    Optional<Book> findBookById(Long id);

    List<Book> findAllBooks();

    Optional<Book> findBookByIsbn(String isbn);

    List<Book> findBooksByAuthor(String author);

    List<Book> findBooksByTitleContains(String titlePart);

    Book saveBook(Book book);

    boolean deleteBookById(Long id);
}
