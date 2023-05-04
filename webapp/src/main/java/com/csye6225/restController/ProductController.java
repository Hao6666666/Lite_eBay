package com.csye6225.restController;

import com.csye6225.entity.Image;
import com.csye6225.entity.Product;
import com.csye6225.models.ProductModel;
import com.csye6225.services.ImageService;
import com.csye6225.services.ProductService;
import com.timgroup.statsd.StatsDClient;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;


@RestController
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @Autowired
    ImageService imageService;

    @Autowired
    StatsDClient statsDClient;

    @PostMapping(path="/v1/product")
    public ResponseEntity createProduct(@RequestBody Product product, HttpServletRequest Request) {
        statsDClient.incrementCounter("endpoint.product.http.post");

        final String token = Request.getHeader("Authorization");
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(product, token));
    }

    @GetMapping(path="/v1/product/{id}", produces = "application/json")
    public ProductModel getProduct(@PathVariable Integer id) {
        statsDClient.incrementCounter("endpoint.product.http.get");
        return productService.getProduct(id);
    }

    @PutMapping(path="/v1/product/{id}")
    public ResponseEntity updateProduct(@PathVariable Integer id, @RequestBody Product product, HttpServletRequest Request) {
        statsDClient.incrementCounter("endpoint.product.http.update");

        final String token = Request.getHeader("Authorization");
        return productService.updateProduct(id, product, token);
    }

    @PatchMapping(path="/v1/product/{id}")
    public ResponseEntity patchProduct(@PathVariable Integer id, @RequestBody Product product, HttpServletRequest Request) {
        statsDClient.incrementCounter("endpoint.product.http.patch");

        final String token = Request.getHeader("Authorization");
        return productService.patchProduct(id, product, token);
    }

    @DeleteMapping(path="/v1/product/{id}")
    public ResponseEntity deleteProduct(@PathVariable Integer id, HttpServletRequest Request) {
        statsDClient.incrementCounter("endpoint.product.http.delete");

        final String token = Request.getHeader("Authorization");
        return productService.deleteProduct(id, token);
    }

    @DeleteMapping(path="/v1/product/")
    public ResponseEntity deleteProductError() {
        return ResponseEntity.badRequest().body("");
    }

    @PostMapping(path="/v1/product/{id}/image")
    public ResponseEntity createProductImage(@PathVariable Integer id, HttpServletRequest Request, @RequestParam("file") MultipartFile file) throws IOException {
        statsDClient.incrementCounter("endpoint.image.http.post");

        final String token = Request.getHeader("Authorization");
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.addProductImage(id, token, file));
    }

    @GetMapping(path="/v1/product/{id}/image")
    public List<Image> getProductImage1(@PathVariable Integer id, HttpServletRequest Request){
        statsDClient.incrementCounter("endpoint.image.http.getall");
        final String token = Request.getHeader("Authorization");
        return imageService.getProductImage1(id, token);
    }

    @GetMapping(path="/v1/product/{prod_id}/image/{img_id}")
    public List<Image> getProductImage2(@PathVariable Integer prod_id, @PathVariable String img_id, HttpServletRequest Request){
        statsDClient.incrementCounter("endpoint.image.http.getone");

        final String token = Request.getHeader("Authorization");
        return imageService.getProductImage2(prod_id, img_id, token);
    }

    @DeleteMapping(path="/v1/product/{prod_id}/image/{img_id}")
    public ResponseEntity deleteProductImage(@PathVariable Integer prod_id, @PathVariable String img_id, HttpServletRequest Request) {
        statsDClient.incrementCounter("endpoint.image.http.delete");

        final String token = Request.getHeader("Authorization");
        return imageService.deleteProductImage(prod_id, img_id, token);
    }

}
