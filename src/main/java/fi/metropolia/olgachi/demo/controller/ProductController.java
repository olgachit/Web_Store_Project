package fi.metropolia.olgachi.demo.controller;

import fi.metropolia.olgachi.demo.entity.Product;
import fi.metropolia.olgachi.demo.repository.ProductRepository;
import fi.metropolia.olgachi.demo.entity.Supplier;
import fi.metropolia.olgachi.demo.repository.SupplierRepository;
import fi.metropolia.olgachi.demo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductService productService;

    public ProductController(
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            ProductService productService) {

        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Integer id,
            @RequestBody Product details) {

        return productRepository.findById(id)
                .map(product -> {
                    product.setName(details.getName());
                    product.setDescription(details.getDescription());
                    product.setPrice(details.getPrice());
                    product.setStockQuantity(details.getStockQuantity());

                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{productId}/supplier/{supplierId}")
    public ResponseEntity<Product> addSupplier(
            @PathVariable Integer productId,
            @PathVariable Integer supplierId) {

        Product product = productRepository.findById(productId)
                .orElse(null);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElse(null);

        if (product == null || supplier == null) {
            return ResponseEntity.notFound().build();
        }

        product.getSuppliers().add(supplier);
        supplier.getProducts().add(product);

        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}/supplier/{supplierId}")
    public ResponseEntity<Product> removeSupplier(
            @PathVariable Integer productId,
            @PathVariable Integer supplierId) {

        Product product = productRepository.findById(productId)
                .orElse(null);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElse(null);

        if (product == null || supplier == null) {
            return ResponseEntity.notFound().build();
        }

        product.getSuppliers().remove(supplier);
        supplier.getProducts().remove(product);

        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/increase-prices")
    public String increasePrices(
            @RequestParam Integer categoryId,
            @RequestParam double percentage) {

        int updated = productService.increasePrices(
                categoryId,
                percentage
        );

        return updated + " products updated";
    }

    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) Integer minStock) {

        return productService.searchProducts(
                minPrice,
                minStock
        );
    }
}
