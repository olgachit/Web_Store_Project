package fi.metropolia.olgachi.demo.repository;

import fi.metropolia.olgachi.demo.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Product> search(
            BigDecimal minPrice,
            Integer minStock) {

        CriteriaBuilder cb =
                entityManager.getCriteriaBuilder();

        CriteriaQuery<Product> query =
                cb.createQuery(Product.class);

        Root<Product> product =
                query.from(Product.class);

        List<Predicate> conditions = new ArrayList<>();

        if (minPrice != null) {
            conditions.add(
                    cb.greaterThanOrEqualTo(
                            product.get("price"),
                            minPrice
                    )
            );
        }

        if (minStock != null) {
            conditions.add(
                    cb.greaterThanOrEqualTo(
                            product.get("stockQuantity"),
                            minStock
                    )
            );
        }

        query.select(product)
                .where(conditions.toArray(new Predicate[0]));

        return entityManager
                .createQuery(query)
                .getResultList();
    }
}