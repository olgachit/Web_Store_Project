package fi.metropolia.olgachi.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoriesRepository extends JpaRepository<fi.metropolia.olgachi.demo.entity.ProductCategories, Integer> {
}
