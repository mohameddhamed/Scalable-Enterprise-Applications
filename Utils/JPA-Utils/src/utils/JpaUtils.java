package utils;

import java.util.function.Consumer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class JpaUtils {
	/**
	 * Runs a JPA {@code transaction} on the persistence unit named {@code puName}.
	 */
	public static void withDbTransaction(String puName, Consumer<EntityManager> transaction) {
		// Get an entity manager factory by persistence unit name
		// The database table will be automatically created by eclipselink
		var emf = Persistence.createEntityManagerFactory(puName);

		// Create an entity manager
		var em = emf.createEntityManager();

		// Begin transaction
		em.getTransaction().begin();

		// The caller has specified what to do, run those steps now...
		transaction.accept(em);

		// Commit the transaction and close the entity manager
		em.getTransaction().commit();
		em.close();
	}
}
