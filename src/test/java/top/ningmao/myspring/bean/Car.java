package top.ningmao.myspring.bean;


import org.springframework.beans.factory.annotation.Value;
import top.ningmao.myspring.stereotype.Component;

import java.time.LocalDate;

/**
 * 车类
 *
 * @author ningmao
 * @since 2025-5-6
 */
@Component
public class Car {
    private int price;

    private LocalDate produceDate;

    @Value("${brand}")
    private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDate getProduceDate() {
        return produceDate;
    }

    public void setProduceDate(LocalDate produceDate) {
        this.produceDate = produceDate;
    }

    @Override
    public String toString() {
        return "Car{" +
                "price=" + price +
                ", produceDate=" + produceDate +
                ", brand='" + brand + '\'' +
                '}';
    }
}
