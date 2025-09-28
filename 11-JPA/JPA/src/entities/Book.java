package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import main.jpa.StockStatus;

import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    @Getter
    @Setter
    private String title;

    @Column(nullable = false, length = 255)
    @Getter
    @Setter
    private String author;

    @Column()
    @Getter
    @Setter
    private LocalDate publishedAt;

    @Column()
    @Getter
    @Setter
    private Integer pages;

    @Column(precision = 10, scale = 2)
    @Getter
    @Setter
    private java.math.BigDecimal price;

    @Column()
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;

    public Book() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != getClass()) return false;

        Book book = (Book) o;
        if (book.getId() == null) return false;
        return id != null && id.equals(book.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public String toString() {
       return "Book: " + id + " has Title: " + title + " and Author: " + author;
    }

}
