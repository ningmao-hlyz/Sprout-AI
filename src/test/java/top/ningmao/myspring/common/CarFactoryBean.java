package top.ningmao.myspring.common;

import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.bean.factory.FactoryBean;

/**
 * @author ningmao
 * @since 2025-5-15
 */
public class CarFactoryBean implements FactoryBean<Car> {
    
    private String brand;
    
    
    @Override
    public Car getObject() throws Exception {
        Car car = new Car();
        car.setBrand(brand);
        return car;
    }
    
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    public void setBrand(String brand){
        this.brand = brand;
    }
}
