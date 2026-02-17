package com.sdl.application.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "author", nullable = false, length = 120)
    private String author;

    @Column(name = "isbn", nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "in_stock", nullable = false)
    private boolean inStock = true;

    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    protected Book() {
    }

    public Book(String title, String author, String isbn, BigDecimal price, boolean inStock) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.inStock = inStock;
    }

    @PrePersist
    void prePersist() {
        if (dateCreated == null) {
            dateCreated = LocalDateTime.now();
        }
        if (price == null) {
            price = BigDecimal.ZERO;
        }
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }

    public void setIsbn(String isbn) { this.isbn = isbn; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isInStock() { return inStock; }

    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public LocalDateTime getDateCreated() { return dateCreated; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return inStock == book.inStock && Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(isbn, book.isbn) && Objects.equals(price, book.price) && Objects.equals(dateCreated, book.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, isbn, price, inStock, dateCreated);
    }
}
