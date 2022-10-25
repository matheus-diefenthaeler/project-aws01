package br.com.diefenthaeler.matheus.aws_project01.controller;

import br.com.diefenthaeler.matheus.aws_project01.enums.EventType;
import br.com.diefenthaeler.matheus.aws_project01.model.Product;
import br.com.diefenthaeler.matheus.aws_project01.repository.ProductRepository;
import br.com.diefenthaeler.matheus.aws_project01.service.ProductPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository repository;

    private final ProductPublisherService productPublisherService;

    @GetMapping
    public ResponseEntity<Iterable<Product>> findAll() {
        Iterable<Product> all = repository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id) {
        Optional<Product> optProduct = repository.findById(id);
        return optProduct.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product productCreated = repository.save(product);

        productPublisherService.publishProductEvent(productCreated, EventType.PRODUCT_CREATED, "matheus");

        return new ResponseEntity<Product>(productCreated, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") long id) {

        if (repository.existsById(id)){
            product.setId(id);

            Product productUpdated = repository.save(product);

            productPublisherService.publishProductEvent(productUpdated, EventType.PRODUCT_UPDATED, "lucas");


            return new ResponseEntity<Product>(productUpdated, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {

        Optional<Product> optionalProduct = repository.findById(id);
        if (optionalProduct.isPresent()){
            Product product = optionalProduct.get();

            repository.delete(product);

            productPublisherService.publishProductEvent(product, EventType.PRODUCT_DELETED, "marcelo");


            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optionalProduct = repository.findByCode(code);
        return optionalProduct
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
