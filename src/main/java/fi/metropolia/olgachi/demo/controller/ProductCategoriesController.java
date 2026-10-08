package fi.metropolia.olgachi.demo.controller;

import fi.metropolia.olgachi.demo.entity.ProductCategories;
import fi.metropolia.olgachi.demo.repository.ProductCategoriesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-category")
public class ProductCategoriesController {

    private final ProductCategoriesRepository repository;

    public ProductCategoriesController(ProductCategoriesRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategories> getCategoryById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(category ->ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ProductCategories> getAllCategories() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategories> updateCategory(
            @PathVariable Integer id,
            @RequestBody ProductCategories details) {

        return repository.findById(id)
                .map(category -> {
                    category.setName(details.getName());
                    category.setDescription(details.getDescription());

                    return ResponseEntity.ok(repository.save(category));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }

    @PostMapping
    public ProductCategories create(@RequestBody ProductCategories category) {
        category.getProducts().forEach(product ->
                product.setCategory(category)
        );

        return repository.save(category);
    }
}
