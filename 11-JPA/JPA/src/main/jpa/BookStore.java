package main.jpa;

import entities.Author;
import entities.Book;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

public class BookStore {
    private EntityManagerFactory emf;

    // Constructor, gets the name of a persistence unit, and creates an appropriate `EntityManager` for it.
    BookStore(String persistenceUnitName) {
        try {
            emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        } catch (Exception e) {
            System.out.println("Entity Manager Factory Error: " + e.getMessage());
        }
    }

    // Adds a new book into the database, and returns its identifier.
    int addBook(Book newBook) {
        EntityManager em = emf.createEntityManager();

        try {
            EntityTransaction et = em.getTransaction();

            et.begin();
            em.persist(newBook);
            et.commit();

            return newBook.getId();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    Author addAuthor(String name, LocalDate birthDate /* can be null */) {
        Author author = new Author(name, birthDate);
        return this.addAuthor(author);
    }
    Author addAuthor(Author author) {

        EntityManager em = emf.createEntityManager();

        try {
            EntityTransaction et = em.getTransaction();


            et.begin();
            em.persist(author);
            et.commit();

            return author;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }

    }

    // Deletes the book with the given id (if it exists in the database).
    void removeBookById(int bookId) {

        EntityManager em = emf.createEntityManager();

        try {
            EntityTransaction et = em.getTransaction();

            et.begin();
            Book bookToRemove = em.find(Book.class, bookId);
            if (bookToRemove != null) {
                em.remove(bookToRemove);
            }
            et.commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }

    }

    // Gets all books present in the database.
    java.util.List<Book> getAllBooks() {

        EntityManager em = emf.createEntityManager();

        try {
            EntityTransaction et = em.getTransaction();

            et.begin();
            TypedQuery<Book> tq = em.createQuery("SELECT b FROM Book b", Book.class);
            List<Book> books = tq.getResultList();
            et.commit();

            return books;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    // Adds a book with one author. It will be UNKNOWN in stock.
    Book addBook(String title, Author author) {

        EntityManager em = emf.createEntityManager();

        try {

            Book newBook = new Book(title, author.getName());
            newBook.addAuthor(author);
            em.persist(newBook);

            return newBook;

        } finally {
            em.close();
        }
    }

    // Associates a book with an author.
    void assignAuthorToBook(Author author, Book book) {

        EntityManager em = emf.createEntityManager();

        try {
            EntityTransaction et = em.getTransaction();

            et.begin();

//            Book managedBook = em.merge(book);
//            Author managedAuthor = em.merge(author);
//
//            managedBook.addAuthor(managedAuthor);
            book.addAuthor(author);

            et.commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }

    }

    // Removes a book.
    void removeBook(Book book) {

        EntityManager em = emf.createEntityManager();

        try {

            EntityTransaction et = em.getTransaction();
            et.begin();

            book.removeAuthors();
            em.persist(book);

            et.commit();

        } finally {
            em.close();
        }
    }
}
