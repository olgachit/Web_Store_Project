package fi.metropolia.olgachi.demo.service;

import fi.metropolia.olgachi.demo.entity.Product;
import fi.metropolia.olgachi.demo.repository.ProductCriteriaRepository;
import fi.metropolia.olgachi.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCriteriaRepository criteriaRepository;

    public ProductService(ProductRepository productRepository, ProductCriteriaRepository criteriaRepository) {
        this.criteriaRepository = criteriaRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public int increasePrices(Integer categoryId, double percentage) {
        return productRepository.increasePricesByCategory(
                categoryId,
                percentage
        );
    }

    public List<Product> searchProducts(
            BigDecimal minPrice,
            Integer minStock) {

        return criteriaRepository.search(
                minPrice,
                minStock
        );
    }
}