package com.csye6225.Util;

import com.csye6225.entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Random;

@Configuration
public class ProductGenerator {
    @Autowired
    MockMvc mockMvc;
    private Faker faker = new Faker();
    Random random = new Random();

    public Product getProduct() {
        Product product = new Product();
        product.setName(faker.superhero().name());
        product.setDescription(faker.company().buzzword());
        product.setSku(faker.commerce().productName()+ random.nextInt(1000));
        product.setManufacturer(faker.company().name());
        product.setQuantity(Math.abs(random.nextInt(100)));

        return product;
    }

    public static String toJson(Product product) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(product);
        return json;
    }

}
