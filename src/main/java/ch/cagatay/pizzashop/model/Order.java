package ch.cagatay.pizzashop.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String address;
    private String phone;
    private OrderStatus status;

    @ManyToMany
    @JoinTable(
            name = "orders_pizzas",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "pizza_id"))
    private List<Pizza> pizzas;

    protected Order() { }

    public Order(String address, String phone, OrderStatus status, List<Pizza> pizzas) {
        this.address = address;
        this.phone = phone;
        this.status = status;
        this.pizzas = pizzas;
    }
}
