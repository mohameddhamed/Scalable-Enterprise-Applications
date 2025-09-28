
package entities;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Item implements Serializable {
    @Id
    private String name;

    private int quantity;

    private float price;
}

// Unfortunately, Java records cannot be used as entities.
// If they could, it would be even simpler.
// For more details, see
// https://javatechonline.com/java-records-vs-jpa-entities-and-lombok/

//public record Item(
//@Id
//String name,
//int quantity,
//float price
//) implements Serializable {}
