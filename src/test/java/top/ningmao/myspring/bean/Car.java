package top.ningmao.myspring.bean;


import top.ningmao.myspring.stereotype.Component;

/**
 * 车类
 *
 * @author ningmao
 * @since 2025-5-6
 */
@Component
public class Car {
    
    private String brand;
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                '}';
    }
}
