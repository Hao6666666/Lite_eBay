package com.csye6225.services;

import com.csye6225.errors.*;
import com.csye6225.models.UserModel;
import com.csye6225.entity.User;
import com.csye6225.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service
@Slf4j
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserMapper UserMapper;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserModel register(User user) {
        if (StringUtils.isEmpty(user.getUsername())){
            logger.error("Username can not be empty");
            throw new badRequest("Username can not be empty");
        }

        if(user.getCreatedAt()!= null || user.getUpdatedAt()!= null) {
            logger.error("You can not change createdAt or updatedAt");
            throw new badRequest("You can not change createdAt or updatedAt");
        }
        User UserFromUserMapper = UserMapper.findByUsername(user.getUsername());
        UserModel userModel = new UserModel();
        if(UserFromUserMapper != null){
            logger.error("Username is in use: " + user.getUsername());
            throw new EmailError("Username is in use: " + user.getUsername() );
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UserMapper.save(user);
        BeanUtils.copyProperties(user, userModel);

        Object json = gson(userModel);
        logger.info("User created: " +  json);

        return userModel;
    }

    public UserModel get(Integer id, String username) {
        checkUserExist(id);

        User user = UserMapper.findById(id).get();
        UserModel userModel = new UserModel();
        if (!user.getUsername().equals(username)) {
            logger.error("You can not access other users info");
            throw new GetUserInfoError("You can not access other users info");
        }
        BeanUtils.copyProperties(user, userModel);

        Object json = gson(userModel);
        logger.info("User get: " +  json);

        return userModel;
    }

    public void update(Integer id, User user) {
        checkUserExist(id);

        User currentUser = UserMapper.findById(id).get();
//        System.out.println(currentUser.getId() + "=======" + id);
        if (!currentUser.getUsername().equals(user.getUsername())) {
            logger.error("You can not change others data");
            throw new IllegalChangeError("You can not change others data");
        }
        if (user.getCreatedAt()!= null || user.getUpdatedAt()!= null){
            logger.error("Cannot change create time or update time");
            throw new UpdateError("Cannot change create time or update time");
        }

        if (StringUtils.isEmpty(user.getFirstName())
                || StringUtils.isEmpty(user.getLastName())
                || StringUtils.isEmpty(user.getPassword()) ) {
            logger.error("User info missing data");
            throw new badRequest("User info missing data");

        }
        String username = currentUser.getUsername();
        Date createdDate = currentUser.getCreatedAt();
        String newPassword = bCryptPasswordEncoder.encode(user.getPassword());
        BeanUtils.copyProperties(user, currentUser);
        currentUser.setId(id);
        currentUser.setUsername(username);
        currentUser.setCreatedAt(createdDate);
        currentUser.setPassword(newPassword);
        UserMapper.save(currentUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = UserMapper.getUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Username not found" + username);
        }else{
            return user;
        }
    }

    private void checkUserExist(Integer id) {
        if(!UserMapper.existsById(id)) {
            throw new NoPoductFoundError("User not found");
        }
    }

    private Object gson(UserModel userModel){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", userModel.getId());
        jsonObject.addProperty("username", userModel.getUsername());
        jsonObject.addProperty("first_name", userModel.getFirstName());
        jsonObject.addProperty("last_name", userModel.getLastName());
        jsonObject.addProperty("account_created", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userModel.getCreatedAt()));
        jsonObject.addProperty("account_updated", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userModel.getUpdatedAt()));

        String json = gson.toJson(jsonObject);
        return json;
    }

}
