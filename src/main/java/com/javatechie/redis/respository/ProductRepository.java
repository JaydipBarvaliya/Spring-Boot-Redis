package com.javatechie.redis.respository;

import com.javatechie.redis.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {

    public static final String HASH_KEY = "Product";

    private final RedisTemplate redisTemplate;

    public ProductRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public Product save(Product product){
        System.out.println("DB Connection For : SAVE()");
        redisTemplate.opsForHash().put(HASH_KEY, product.getId(), product);
        return product;
    }

    public List<Product> findAll(){

        System.out.println("DB Connection For : findAll()");

        return redisTemplate.opsForHash().values(HASH_KEY);
    }

    public Product findProductById(int id){

        System.out.println("DB Connection For : findProductById ::" + id);

        return (Product) redisTemplate.opsForHash().get(HASH_KEY, id);
    }


    public String deleteProduct(int id){

        System.out.println("DB Connection For : Deletion");

         redisTemplate.opsForHash().delete(HASH_KEY, id);
        return "product removed !!";
    }

    public Product updateProduct(Product product) {

        System.out.println("DB Connection For : updateProduct()");

        redisTemplate.opsForHash().put(HASH_KEY, product.getId(), product);

        return product;
    }
}
