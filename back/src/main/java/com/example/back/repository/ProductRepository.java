package com.example.back.repository;

import com.example.back.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 상품 Repository
 *
 * @fileName : ProductRepository
 * @since : 26. 3. 23.
 */
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
