package com.csye6225.restController;

import com.csye6225.errors.AuthenticationError;
import com.csye6225.errors.IllegalChangeError;
import com.csye6225.mapper.UserMapper;
import com.csye6225.models.UserModel;
import com.csye6225.entity.User;
import com.csye6225.services.UserService;
import com.timgroup.statsd.StatsDClient;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    StatsDClient statsDClient;

    @PostMapping(path="/v1/user")
    public ResponseEntity createUser(@RequestBody User user) {
        statsDClient.incrementCounter("endpoint.user.http.post");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(user));
    }

    @GetMapping(path= "/v1/user/{id}", produces = "application/json")
    public UserModel getUser(@PathVariable Integer id, HttpServletRequest Request) {
        statsDClient.incrementCounter("endpoint.user.http.get");

        final String token = Request.getHeader("Authorization");
        if (token == null || !token.toLowerCase().startsWith("basic")) {
            logger.error("User " + id + " failed to get");
            throw new AuthenticationError("Unauthorized");
        }
        // Authorization: Basic base64credentials
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        String username=values[0];

        return userService.get(id, username);
    }

    @PutMapping(path="/v1/user/{id}")
    public ResponseEntity updateUser(@PathVariable Integer id, @RequestBody User user, HttpServletRequest Request) throws IllegalChangeError {
        statsDClient.incrementCounter("endpoint.user.http.put");

        final String token = Request.getHeader("Authorization");
        if (token != null && token.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = token.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username=values[0];
            String password=values[1];
            if (!username.equals(user.getUsername()) ){
                logger.error("User " + id + " failed to update; Username cannot be changed");
                throw new IllegalChangeError("Username cannot be changed");
            }
        }
        userService.update(id, user);
        logger.info("User " + id + " updated successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/healthz")
    public String unauth(){
        statsDClient.incrementCounter("endpoint.healthz.http.get");
        logger.info("Access healthz");

        return "It is healhty.";
    }

}
