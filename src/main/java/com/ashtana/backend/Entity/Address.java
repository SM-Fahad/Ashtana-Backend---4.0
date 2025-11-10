//package com.ashtana.backend.Entity;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "addresses")
//public class Address {
//    @Id
//    @GeneratedValue (strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String recipientName;
//
//    private String street;
//    private String city;
//    private String state;
//    private String country;
//    private String postalCode;
//    private String type; // "SHIPPING" or "BILLING"
//
//    @Column
//    private String phone;
//
////    @ManyToOne (fetch = FetchType.LAZY)
////    @JoinColumn(name = "user_id", nullable = false)
////    @JsonBackReference
////    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_name", referencedColumnName = "userName", nullable = false)
//    private User user;
//}
//

package com.ashtana.backend.Entity;

import com.ashtana.backend.Enums.AddressType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipientName;

    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    @Enumerated(EnumType.STRING)
    private AddressType type; // Use enum instead of String

    @Column
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name", referencedColumnName = "userName", nullable = false)
    @JsonBackReference
    private User user;
}
