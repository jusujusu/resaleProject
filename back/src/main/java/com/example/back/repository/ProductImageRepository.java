package com.example.back.repository;

import com.example.back.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * FileName    : ProductImageRepository
 * Since       : 26. 3. 26.
 * Dsecription  :
 */
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
}
