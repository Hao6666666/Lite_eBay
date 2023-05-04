package com.csye6225.mapper;

import com.csye6225.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageMapper extends JpaRepository<Image, String> {
    List<Image> findByProductId(Integer productId);
    List<Image> findByImageId(String imageId);

}
