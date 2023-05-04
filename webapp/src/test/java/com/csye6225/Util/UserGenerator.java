package com.csye6225.Util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;


@Configuration
public class UserGenerator {
    @Autowired
    MockMvc mockMvc;

    private Faker faker = new Faker();

    public User getUser() {
        User user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setUsername(faker.name().firstName() + "1zzaq1@xtest.com");
        user.setPassword(faker.internet().password());

        return user;
    }

    public static String toJson(User user) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        return json;
    }

    public MockHttpServletResponse createUser(String url, String usrJson) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(usrJson).contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

    }

    public String generateToken(String username, String password) {
        String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + encoding;
    }

}
