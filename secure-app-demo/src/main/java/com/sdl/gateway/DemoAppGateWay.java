package com.sdl.gateway;

import com.sdl.application.model.Book;
import com.sdl.application.model.User;
import com.sdl.application.model.views.UserView;
import com.sdl.gateway.jpaRepository.BookRepository;
import com.sdl.gateway.jpaRepository.UserRepository;
import com.sdl.gateway.jpaRepository.view.UserViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DemoAppGateWay implements ApplicationGateWay, UserManagementGateWay{

    private final UserRepository userRepository;
    private final BookRepository bookRepository;


    private final UserViewRepository userViewRepository;

    public DemoAppGateWay(UserRepository userRepository,
                          BookRepository bookRepository,
                          UserViewRepository userViewRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userViewRepository = userViewRepository;
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }


    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Override
    public List<Book> findBooksByTitleContains(String titlePart) {
        return bookRepository.findByTitleContainingIgnoreCase(titlePart);
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public boolean deleteBookById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }


    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }


    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByUserName(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public UserView getUserViewByUsername(String name) {
        return userViewRepository.findByUsername(name)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated principal username not found in UserView: " + name
                ));
    }

    @Override
    public Page<UserView> findSystemUsers(Pageable pageRequest) {
        return userViewRepository.findAll(pageRequest);
    }
}
