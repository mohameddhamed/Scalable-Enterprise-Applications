package main.jpa;

import entities.Author;
import entities.Book;

import java.time.LocalDate;

public class Application {
    public static void main(String[] args) {
        BookStore bookStore = new BookStore("testpu");

        Book firstBook = new Book();
        Book secondBook = new Book();

        firstBook.setAuthor("Marshall");
        firstBook.setTitle("Pride of a nation");
        secondBook.setAuthor("Trotsky");
        secondBook.setTitle("Free Watermelon");

        bookStore.addBook(firstBook);
        System.out.println("Created book: " + firstBook);
        bookStore.addBook(secondBook);
        System.out.println("Created book with ID: " + secondBook);
//
//        bookStore.getAllBooks().forEach(System.out::println);
//        bookStore.removeBookById(firstId);
//        System.out.println("Deleted book with ID: " + firstId);
//        bookStore.getAllBooks().forEach(System.out::println);
        Author newAuthor3 = new Author("Douagi", LocalDate.of(2012, 5, 22));
        bookStore.addAuthor(newAuthor3);
        System.out.println("Created new Author - " + newAuthor3);

        bookStore.assignAuthorToBook(newAuthor3, secondBook);
        System.out.println("Associated Author - " + newAuthor3);

//        Book newBook = bookStore.addBook("med4's book", newAuthor2);
        System.out.println("from the new author" + newAuthor3.getBooks().stream().toList().get(0).toString());
        System.out.println("from the second book" + secondBook);
        bookStore.getAllBooks().forEach(System.out::println);

//        Book newBook= new Book("mein kampf", "painter");
//        System.out.println("Created new Book - " + newBook);

    }
}
