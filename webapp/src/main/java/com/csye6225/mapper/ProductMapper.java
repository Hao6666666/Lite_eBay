package com.csye6225.mapper;

import com.csye6225.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper extends JpaRepository<Product, Integer> {
    List<Product> findBySku(String sku);
}
