package top.ningmao.myspring.bean;


/**
 * 车类
 *
 * @author ningmao
 * @since 2025-5-6
 */
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
