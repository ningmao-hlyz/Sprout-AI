package top.ningmao.myspring.service;
/**
 * @author NingMao
 * @since 2025-07-12
 */
public class WorldServiceWithExceptionImpl implements WorldService{
    @Override
    public void explode() {
        System.out.println("The Earth is going to explode with an Exception");
        throw new RuntimeException();
    }


}
