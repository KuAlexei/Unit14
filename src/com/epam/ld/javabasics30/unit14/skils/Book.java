package com.epam.ld.javabasics30.unit14.skils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Book {
    private String title;
    private Set<Author> authors = new HashSet<>();
    private int year;
    private int price;

    public Book(String title, Set<Author> authors, int year, int price) {
        this.title = title;
        this.authors = authors;
        this.year = year;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public int getYear() {
        return year;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return year == book.year &&
                title.equals(book.title) &&
                authors.equals(book.authors) &&
                price == book.price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, authors, year, price);
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", authors=" + authors +
                ", year=" + year +
                ", price='" + price + '\'' +
                '}';
    }
}
