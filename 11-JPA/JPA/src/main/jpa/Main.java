package main.jpa;

import java.util.List;

import entities.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import utils.JpaUtils;

public class Main {
	public static void main(String[] args) {
		JpaUtils.withDbTransaction("testpu", em -> {
			deleteRecords(em);

			insertRecords(em);
			selectRecords(em);

			// Clear the entity cache to force re-select next time
			em.clear();

			updateRecords(em);
			selectRecords(em);
		});
	}

	private static void deleteRecords(EntityManager em) {
		System.out.println("Removing all items.");

		// Bulk delete with criteria API
		Query query        = em.createQuery("DELETE FROM Item");
		int   removedItems = query.executeUpdate();

		if (removedItems > 0) {
			System.out.printf("%d items removed.%n", removedItems);
		}
	}

	private static void insertRecords(EntityManager em) {
		for (int i = 0; i < 5; ++i) {
			var inv = new Item("Item #" + i, i + 2, i * 1.41f);
			em.persist(inv);
		}
	}

	private static void selectRecords(EntityManager em) {
		// Select with JPQL and print out entities
		TypedQuery<Item> q     = em.createQuery("SELECT i FROM Item AS i ORDER BY i.name", Item.class);
		List<Item>       items = q.getResultList();

		// Different arrangement.
		items = em.createQuery("""
		SELECT i
		FROM Item AS i
		ORDER BY i.name
		""", Item.class)
		.getResultList();

		System.out.println();

		for (Item inv : items) {
			System.out.println(inv);
		}

		System.out.println();
	}

	private static void updateRecords(EntityManager em) {
		System.out.println("Updating items with price 0 to 100...");

		// Update with criteria API
		Query query        = em
		        .createQuery("""
	        		UPDATE Item AS i
	        		SET i.price = :originalPrice
	        		WHERE i.price = :newPrice
	        		""")
		        .setParameter("originalPrice", 100.0)
		        .setParameter("newPrice", 0.0);
		int updatedItemCount = query.executeUpdate();

		if (updatedItemCount > 0) {
			System.out.printf("%d items updated.%n", updatedItemCount);
		}
	}
}
