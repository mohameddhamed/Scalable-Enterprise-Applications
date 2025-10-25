package entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @PrimaryKeyJoinColumn : TODO: @Id automatically means primary key
    private Integer id;

    @Column(length = 255, nullable = false)
    @Getter
    private String name;

    @Column()
    @Getter
    private LocalDate birthDate;

    @ManyToMany
    @Getter
    private HashSet<Book> books;

    public Author() {}

    public Author(String name) {
        this.name = name;
    }
    public Author(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
        this.books = new HashSet<>();
    }

    @Override
    public String toString() {
        String hisBooks = !books.isEmpty() ? books.toString() : "";
        return "Id: " + id + " & Name: " + name + " & BirthDate: " + birthDate + " and has these books: " + hisBooks;
    }
}
