package com.ashtana.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "my_bag")
//public class MyBag {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "user_name", referencedColumnName = "userName", nullable = false, unique = true)
//    private User user;
//
//    @OneToMany(mappedBy = "myBag", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<MyBagItems> items = new ArrayList<>();
//
//    @Column(name = "total_price", nullable = false)
//    private Double totalPrice = 0.0;
//
//    // ✅ Add helper methods
//    public void addItem(MyBagItems item) {
//        items.add(item);
//        item.setMyBag(this);
//        recalculateTotal();
//    }
//
//    public void removeItem(MyBagItems item) {
//        items.remove(item);
//        item.setMyBag(null);
//        recalculateTotal();
//    }
//
//    public void recalculateTotal() {
//        this.totalPrice = items.stream()
//                .mapToDouble(MyBagItems::getTotalPrice)
//                .sum();
//    }
//
//    public int getTotalItems() {
//        return items.stream()
//                .mapToInt(MyBagItems::getQuantity)
//                .sum();
//    }
//}

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "my_bag")
public class MyBag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_name", referencedColumnName = "userName", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    // CHANGE CASCADE TYPE to avoid merge issues with deleted items
    @OneToMany(mappedBy = "myBag", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MyBagItems> items = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private Double totalPrice = 0.0;

    // ✅ Add helper methods
    public void addItem(MyBagItems item) {
        items.add(item);
        item.setMyBag(this);
        recalculateTotal();
    }

    public void removeItem(MyBagItems item) {
        items.remove(item);
        item.setMyBag(null);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalPrice = items.stream()
                .mapToDouble(MyBagItems::getTotalPrice)
                .sum();
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(MyBagItems::getQuantity)
                .sum();
    }
}