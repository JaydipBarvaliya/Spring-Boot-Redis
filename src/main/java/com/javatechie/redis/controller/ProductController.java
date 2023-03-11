package com.javatechie.redis.controller;

import com.javatechie.redis.entity.Product;
import com.javatechie.redis.respository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/product")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProductController {

    private final ProductRepository repository;
    private final ProductController productController;

    public ProductController(ProductRepository repository, ProductController productController) {
        this.repository = repository;
        this.productController = productController;
    }


    @Cacheable(cacheNames = "Products")
    @GetMapping
        public List<Product> getAllProducts() {
            return repository.findAll();
        }

    @PostMapping
    @CacheEvict(cacheNames = "Products", allEntries = true)
    @CachePut(cacheNames = "Product", key = "#product.id")
    public Product save(@RequestBody Product product) {
        return repository.save(product);
    }

    //@Cacheput   - Update in Cache and DB
    //@Cachable   - Only store data in defined Cache
    //@CacheEvict - Remove data from the cache
    //@Caching    - Regroups multiple cache operations to be applied on a method



    @GetMapping("/{id}")
    @Cacheable(key = "#id", value = "Product", unless = "#result == null")
    public Product findProductById(@PathVariable int id) {
        return repository.findProductById(id);
    }



    @DeleteMapping("/{id}")
    @Caching(evict = { @CacheEvict(cacheNames = "Product", key = "#id"),
                       @CacheEvict(cacheNames = "Products", allEntries = true) })
    public String remove(@PathVariable int id) {
        return repository.deleteProduct(id);
    }

    @PutMapping
    @Caching(evict = {@CacheEvict(cacheNames = "Products", allEntries = true) })
    @CachePut(cacheNames = "Product", key = "#product.id")
    public Object update(@RequestBody Product product) {

        //Here to tell "findProductById" method that the request comes from outside, so it will retrieve data from
        //Cache if exist instead of calling database everytime even if requested data exist in Cache.
        Product productById = productController.findProductById(product.getId());

        if(productById == null) return "Product doesn't exist";

        return repository.updateProduct(product);
    }

}