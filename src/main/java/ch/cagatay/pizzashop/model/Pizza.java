package ch.cagatay.pizzashop.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "pizzas")
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private float price;
    private boolean active;

    @ManyToMany(mappedBy = "pizzas")
    private List<Order> orders;

    protected Pizza() {}

    public Pizza(String name, String description, float price, boolean active) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.active = active;
    }
}
