package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import main.jpa.StockStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY) // TODO: see why I am not getting an error with the id even though I am not setting it
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
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

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
       String itsAuthors = authors.stream().map(Author::getName).collect(Collectors.joining(", "));
       return "Book: " + id + " has Title: " + title + " and Authors (" + authors.size() + "): " + itsAuthors;
    }

    @ManyToMany
//    @JoinTable(
//            name="authors",
//            joinColumns = @JoinColumn(name = "id"),
//            inverseJoinColumns = @JoinColumn(name = "id")
//    ) TODO: This is extra config, not really needed
    private HashSet<Author> authors = new HashSet<>();

    // Adds an author.
    public Author addAuthor(Author author) {

        this.authors.add(author);
        author.getBooks().add(this);
        return author;

    }

    // Removes an author.
    public void removeAuthor(Author author) {

        this.authors.remove(author);
        author.getBooks().remove(this);

    }

    public void removeAuthors() {

        this.authors.forEach(this::removeAuthor);

    }



}
