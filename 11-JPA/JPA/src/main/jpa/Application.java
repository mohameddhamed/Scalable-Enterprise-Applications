package main.jpa;

import entities.Book;

public class Application {
    public static void main(String[] args) {
        BookStore bookStore = new BookStore("testpu");

        Book firstBook = new Book();
        Book secondBook = new Book();

        firstBook.setAuthor("Goebels");
        firstBook.setTitle("Pride of a nation");
        secondBook.setAuthor("Trotsky");
        secondBook.setTitle("Free Watermelon");

        int firstId = bookStore.addBook(firstBook);
        System.out.println("Created book with ID: " + firstId);
        int secondId = bookStore.addBook(secondBook);
        System.out.println("Created book with ID: " + secondId);

        bookStore.getAllBooks().forEach(System.out::println);
        bookStore.removeBookById(firstId);
        System.out.println("Deleted book with ID: " + firstId);
        bookStore.getAllBooks().forEach(System.out::println);
    }
}
