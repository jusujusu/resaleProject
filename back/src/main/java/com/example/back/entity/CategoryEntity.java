package com.example.back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 카테고리 엔티티
 *
 * @fileName : CategoryEntity
 * @since : 26. 3. 23.
 */
@Entity
@Table(name = "categories")
@Getter
@ToString(exclude = {"parent", "children"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 식별자 (PK)

    @Column(nullable = false, length = 50)
    private String name;    // 카테고리 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;    // 부모 카테고리

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryEntity> children = new ArrayList<>();    // 자식 카테고리


    private Integer sortOrder;  // 정렬 순서


    // -------------- 빌더 패턴 --------------
    @Builder
    public CategoryEntity(String name, CategoryEntity parent, int sortOrder) {
        this.name = name;
        this.parent = parent;
        this.sortOrder = sortOrder;
    }

    /*
    * 연관관계 편의 메서드
    * */
    public void addChildCategory(CategoryEntity child) {
        this.children.add(child);
        child.assignParent(this);
    }

    /*
    * 내부에서만 사용하는 부모 설정 메서드
    * */
    public void assignParent(CategoryEntity parent) {
        this.parent = parent;
    }


    // -------------- 비즈니스 로직 --------------
    /*
    * 카테고리 수정
    * */
    public void update(String name, Integer sortOrder) {
        if (name!= null && !name.isBlank()) this.name = name;
        if (sortOrder != null) this.sortOrder = sortOrder;
    }
}
