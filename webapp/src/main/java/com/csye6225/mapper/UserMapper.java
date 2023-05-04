package com.csye6225.mapper;

import com.csye6225.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UserMapper extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User getUserByUsername(String username);
}