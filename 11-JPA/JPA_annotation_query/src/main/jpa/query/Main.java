package main.jpa.query;

import java.util.Date;
import java.util.List;

import entities.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import utils.JpaUtils;

public class Main {
	public static void main(String[] args) {
		JpaUtils.withDbTransaction("testpu", em -> {
			insertSomeProducts(em);
			updateProductByPrimaryKey(em);
			queryWithNamedParameter(em);
			useNamedQuery(em);

			// Apply the changes before we clear the entity manager below
			em.flush();

			testCacheClearing(em);
		});
	}

	private static void insertSomeProducts(EntityManager em) {
		for (int i = 0; i < 10; ++i) {
			insertProduct(em, i);
		}
	}

	private static void insertProduct(EntityManager em, int i) {
		var p = new Product("product" + i, new Date(), Product.Type.B);
		em.persist(p);
	}

	private static void updateProductByPrimaryKey(EntityManager em) {
		// Find a product by its primary key and update it
		Product p3 = em.find(Product.class, 3L);
		p3.setType(Product.Type.A);
		em.persist(p3);
	}

	private static void queryWithNamedParameter(EntityManager em) {
		// Query products with a substituted (and named) filter parameter
		TypedQuery<Product> tq = em.createQuery(
		        "select p from Product p where p.type = :prodType order by p.name",
		        Product.class);
		tq.setParameter("prodType", Product.Type.B);
		printResults(tq.getResultList());
	}

	private static void printResults(List<Product> results) {
		System.out.println();

		results.forEach(System.out::println);

		System.out.println();
	}

	private static void useNamedQuery(EntityManager em) {
		// Use a named query defined on the entity
		TypedQuery<Product> nq = em.createNamedQuery("getAllProducts", Product.class);
		printResults(nq.getResultList());
	}

	private static void testCacheClearing(EntityManager em) {
		// Select an entity by its primary key
		Product p7A = em.find(Product.class, 7L);
		// Empty the cache
		em.clear();
		// Select the same entity again (now it's truly a different Java object)
		Product p7B = em.find(Product.class, 7L);

		// Test entity equivalence
		System.out.printf("After clearing the EntityManager, 'find' returns a different object: %s%n", p7A != p7B);
		System.out.printf("... but they are equal to each other: %s%n", p7A.equals(p7B));
	}
}
