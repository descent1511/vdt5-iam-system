package com.example.iam.repository;

import com.example.iam.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
} 