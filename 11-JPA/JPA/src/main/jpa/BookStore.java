package main.jpa;

import entities.Book;
import jakarta.persistence.*;

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
}
