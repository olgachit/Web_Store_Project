package fi.metropolia.olgachi.demo.repository;

import fi.metropolia.olgachi.demo.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.price = p.price * (1 + :percentage / 100.0)
        WHERE p.category.id = :categoryId
    """)
    int increasePricesByCategory(
            @Param("categoryId") Integer categoryId,
            @Param("percentage") double percentage
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Integer id);
}