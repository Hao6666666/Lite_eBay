package com.csye6225.restController;

import com.csye6225.Util.ProductGenerator;
import com.csye6225.Util.User;
import com.csye6225.Util.UserGenerator;
import com.csye6225.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;



@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductGenerator productGenerator;

    @Autowired
    UserGenerator userGenerator;

    String token;

    // craete a user and get the authentication token
    @BeforeEach
    void setUp() throws Exception {
        User user = userGenerator.getUser();
        String userJson = userGenerator.toJson(user);
        userGenerator.createUser("/v1/user", userJson);
        this.token = userGenerator.generateToken(user.getUsername(), user.getPassword());
    }


    @Test
    @DisplayName("Test for create a product")
    public void testCreateProduct() throws Exception {
        Product product = productGenerator.getProduct();
        String productJson =productGenerator.toJson(product);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson)
                        .header("Authorization", this.token))
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(product.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value(product.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("sku").value(product.getSku()))
                .andExpect(MockMvcResultMatchers.jsonPath("manufacturer").value(product.getManufacturer()))
                .andExpect(MockMvcResultMatchers.jsonPath("quantity").value(product.getQuantity()))
                .andExpect(MockMvcResultMatchers.jsonPath("date_added").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("date_last_updated").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("owner_user_id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Test for unauth create a product")
    public void testUnauthCreateProduct() throws Exception {
        Product product = productGenerator.getProduct();
        String productJson =productGenerator.toJson(product);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Test for duplicated sku")
    public void testDuplicatedSku() throws Exception {
        Product product = productGenerator.getProduct();
        String productJson =productGenerator.toJson(product);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson)
                        .header("Authorization", this.token))
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(product.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value(product.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("sku").value(product.getSku()))
                .andExpect(MockMvcResultMatchers.jsonPath("manufacturer").value(product.getManufacturer()))
                .andExpect(MockMvcResultMatchers.jsonPath("quantity").value(product.getQuantity()))
                .andExpect(MockMvcResultMatchers.jsonPath("date_added").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("date_last_updated").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("owner_user_id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/product")
                .content(productJson)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.token))
                // this request should return 400
               .andExpect(MockMvcResultMatchers.status().isBadRequest())
               .andDo(MockMvcResultHandlers.print())
               .andReturn();
    }

}