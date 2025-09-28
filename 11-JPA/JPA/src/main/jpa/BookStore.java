package main.jpa;

public class BookStore {
    // Constructor, gets the name of a persistence unit, and creates an appropriate `EntityManager` for it.
    BookStore(String persistenceUnitName);

    // Adds a new book into the database, and returns its identifier.
    int addBook(Book newBook);

    // Deletes the book with the given id (if it exists in the database).
    void removeBookById(int bookId);

    // Gets all books present in the database.
    java.util.List<Book> getAllBooks();
}
