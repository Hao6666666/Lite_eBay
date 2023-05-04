package com.csye6225.restController;

import com.csye6225.Util.User;
import com.csye6225.Util.UserGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Random;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    Random random;

    @BeforeEach
    void setUp() {}

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Test for create a user")
    public void testCreateUser() throws Exception {
        UserGenerator userGenerator = new UserGenerator();
        User user = userGenerator.getUser();
        String userJson = UserGenerator.toJson(user);
        System.out.println(userJson);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(userJson))
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("first_name").value(user.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("last_name").value(user.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("account_created").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("account_updated").isNotEmpty())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Test for create a user but bad username")
    public void testCreateUser1() throws Exception {
        UserGenerator userGenerator = new UserGenerator();
        User user = userGenerator.getUser();
        user.setUsername("asdfgh");
        String userJson = UserGenerator.toJson(user);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .content(userJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }


    @Test
    @DisplayName("Test for duplicated account")
    public void testDuplicatedUser() throws Exception {
        UserGenerator userGenerator = new UserGenerator();
        User user = userGenerator.getUser();
        String userJson = UserGenerator.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .content(userJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("first_name").value(user.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("last_name").value(user.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("account_created").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("account_updated").isNotEmpty())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .content(userJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Username is in use: " + user.getUsername() ))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Test for unauthorized account")
    public void testGetUser() throws Exception {
        UserGenerator userGenerator = new UserGenerator();
        User user = userGenerator.getUser();

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/user"+ user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Unauthenticated get on healthz")
    public void testhealthz() throws Exception{
        UserGenerator userGenerator = new UserGenerator();
        User user = userGenerator.getUser();
        String userJson1 = UserGenerator.toJson(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/healthz").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}

