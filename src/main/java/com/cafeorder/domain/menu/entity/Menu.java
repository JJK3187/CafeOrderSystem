package com.cafeorder.domain.menu.entity;

import com.cafeorder.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "menus")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "menu_name")
    private String name;

    @Column(nullable = false, name = "menu_price")
    private BigDecimal price;

    @Column(nullable = false, name = "stock_quantity")
    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_status", nullable = false)
    private MenuStatus menuStatus;

    public static Menu createMenu(
            String name,
            BigDecimal price,
            int stockQuantity,
            MenuStatus menuStatus
    ) {
        Menu menu = new Menu();
        menu.name = name;
        menu.price = price;
        menu.stockQuantity = stockQuantity;
        menu.menuStatus = menuStatus;
        return menu;
    }

     public void updateMenu(
            String name,
            BigDecimal price,
            int stockQuantity,
            MenuStatus menuStatus
    ) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.menuStatus = menuStatus;
    }

     public void decreaseStock(int quantity) {
         if (this.stockQuantity < quantity) {
             throw new IllegalArgumentException("재고가 부족합니다.");
         }
         this.stockQuantity -= quantity;
         if (this.stockQuantity == 0) {
             this.menuStatus = MenuStatus.SOLD_OUT;
         }
     }

     public void increaseStock(int quantity) {
         this.stockQuantity += quantity;
     }

}
