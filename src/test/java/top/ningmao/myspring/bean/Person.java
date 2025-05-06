package top.ningmao.myspring.bean;
/**
 * Person 类
 *
 * @author ningmao
 * @since 2025-4-30
 */
public class Person {
    
    private String name;
    
    private int age;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
